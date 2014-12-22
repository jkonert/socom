package de.tud.kom.socom.database.content;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import de.tud.kom.socom.SocomCore;
import de.tud.kom.socom.GlobalConfig;
import de.tud.kom.socom.database.HSQLDatabase;
import de.tud.kom.socom.database.user.HSQLUserDatabase;
import de.tud.kom.socom.util.ContentMediaConverter;
import de.tud.kom.socom.util.datatypes.GameContent;
import de.tud.kom.socom.util.enums.ContentCategory;
import de.tud.kom.socom.util.exceptions.ContentNotAvailableException;
import de.tud.kom.socom.util.exceptions.ContentNotFoundException;
import de.tud.kom.socom.util.exceptions.IllegalAccessException;

/**
 * @author Rhaban Hark
 * @see(GameContentDatabase)
 * 
 *                           HSQLGameContentDatabase implements
 *                           GameContentDatabase Database connection to the HSQL
 *                           DB
 */
public class HSQLGameContentDatabase extends HSQLDatabase implements GameContentDatabase, GlobalConfig {

	private static HSQLGameContentDatabase instance = new HSQLGameContentDatabase();
	private static HSQLUserDatabase userdb = HSQLUserDatabase.getInstance();

	private HSQLGameContentDatabase() {
		super();
	}

	public static HSQLGameContentDatabase getInstance() {
		return instance;
	}

	@Override
	public String createGameContent(long uid, long contextID, String title, String description, ContentCategory category, Map<String, String> attributes, String type, int visibility)
			throws SQLException {
		long typeId = lazyInsert("contenttypes", type);
		
		String query = "INSERT INTO gamecontent (owner, contextid, title, description, "
				+ "metadata, content, type, secretident, visibility, time, hits, category) "
				+ "VALUES (?, ?, ?, ?, ?, NULL, ?, ?, ?, NOW(), 0, ?)";
		
		PreparedStatement selectQuery = db
				.getPreparedStatement("SELECT id FROM gamecontent WHERE secretident = ?;");
		
		PreparedStatement statement = db.getPreparedStatement(query);
		statement.setLong(1, uid);
		statement.setLong(2, contextID);
		statement.setString(3, title);
		statement.setString(4, description);
		statement.setObject(5, attributes);
		statement.setLong(6, typeId);
		
		String secret = Math.abs(new Random().nextInt()) + "";
		selectQuery.setString(1, secret);
		while (selectQuery.executeQuery().next()) {
			secret = new Random().nextInt() + "";
			selectQuery.setString(1, secret);
		}		
		statement.setString(7, secret);
		statement.setInt(8, visibility);
		statement.setInt(9, category.ordinal());
		statement.execute();
		return secret;
	}

	@Override
	public long uploadGameContent(long uid, String identifier, InputStream is) throws SQLException, ContentNotFoundException {
		String query = "UPDATE gamecontent SET content = ?, secretident = NULL WHERE secretident = ?;";
		PreparedStatement statement = db.getPreparedStatementGetKey(query);
		statement.setBlob(1, is);
		statement.setString(2, identifier);
		statement.executeUpdate();

		ResultSet keys = statement.getGeneratedKeys();
		keys.next();
		long id = keys.getLong(1);
		ContentMediaConverter.convertIfNecessary(id); 
		return id;
	}
	
	@Override
	public String getType(long id) throws SQLException, ContentNotFoundException {
		String query = "SELECT name " +
				"FROM gamecontent INNER JOIN contenttypes on contenttypes.id = gamecontent.type " +
				"WHERE gamecontent.id = ?;";
		PreparedStatement statement = db.getPreparedStatement(query);
		statement.setLong(1, id);
		ResultSet rs = statement.executeQuery();
		if(!rs.next())
			throw new ContentNotFoundException();
		return  rs.getString(1);
	}
	
	@Override
	public void setContent(long id, InputStream in) throws SQLException{
		String query = "UPDATE gamecontent SET content = ? WHERE id = ?;";
		PreparedStatement statement = db.getPreparedStatement(query);
		statement.setBlob(1, in);
		statement.setLong(2, id);
		statement.execute();
	}


	@Override
	public List<GameContent> fetchContent(long uid, long contextid, Date since) throws SQLException {
		boolean isAdmin = userdb.userIsAdmin(uid);
		String query = "SELECT " +
							"gamecontent.id, " +
							"gamecontent.owner, " +
							"gamecontent.title, " +
							"gamecontent.category, "+
							"gamecontent.contextid, " +
							"gamecontent.description, " +
							"contenttypes.name AS typename, " +
							"users.name, " +
							"gamecontent.metadata, " +
							"gamecontent.time, " +
							"gamecontent.hits, " +
							"gamecontent.visibility " +
						"FROM " +
							"((gamecontent INNER JOIN contenttypes ON gamecontent.type = contenttypes.id) " +
								"INNER JOIN users ON users.uid = gamecontent.owner) " +
						"WHERE " +
							"gamecontent.contextid = ? AND gamecontent.deleted = 0 " +
							(since != null ?  ("AND gamecontent.time >= '" + SocomCore.getDateFormat().format(since) + "';") : ";");
		PreparedStatement statement = db.getPreparedStatement(query);
		statement.setLong(1, contextid);
		return fetchContentDetails(uid, statement.executeQuery(), isAdmin);
	}
	
	@Override
	public List<GameContent> fetchContent(long uid, long gameInstanceId, String[] contextids, Date since, String[] types, String[] titles, String[] keywords, String[] metadata) throws SQLException {
		boolean isAdmin = userdb.userIsAdmin(uid);
		
		//FIXME TODO no security agains sql-attacks, use PreparedStatements to ensure secure parameter setting (rh)
		
		String whereClause = "WHERE " +
								"gamecontent.deleted = 0 " +
								(since != null ?  ("AND gamecontent.time >= '" + SocomCore.getDateFormat().format(since) + "' ") : "");
		whereClause += appendWhereClause(contextids, "gamecontent.contextid", "=", false);
		whereClause += appendWhereClause(types, "contenttypes.name", "=", true);
		for(int i = 0; i < (titles != null ? titles.length : 0); i++)
			titles[i] = "%" + titles[i] + "%";
		whereClause += appendWhereClause(titles, "gamecontent.title", "LIKE", true);
		for(int i = 0; i < (keywords != null ? keywords.length : 0); i++)
			keywords[i] = "%" + keywords[i] + "%";
		whereClause += appendWhereClause(keywords, "gamecontent.description", "LIKE", true);
		
		String query = "SELECT " +
				"gamecontent.id, " +
				"gamecontent.owner, " +
				"gamecontent.title, " +
				"gamecontent.category, " +
				"gamecontent.contextid, " +
				"gamecontent.description, " +
				"contenttypes.name AS typename, " +
				"users.name, " +
				"gamecontent.metadata, " +
				"gamecontent.time, " +
				"gamecontent.hits, " +
				"gamecontent.visibility " +
			"FROM " +
				"((gamecontent INNER JOIN contenttypes ON gamecontent.type = contenttypes.id) " +
					"INNER JOIN users ON users.uid = gamecontent.owner) " +
			whereClause + ";";	
		
		ResultSet rs = db.execQueryWithResult(query);
		List<GameContent> tmp = fetchContentDetails(uid, rs, isAdmin);
		if(metadata == null)
			return tmp;
		
		List<GameContent> result = new LinkedList<GameContent>();
		contents: for(GameContent gc : tmp){
			metas: for(String meta : metadata){
				String[] metaParts = meta.split("(?<!/):");
				if(metaParts.length < 2){
					continue metas;
				}
				String deescapedKey = metaParts[0].replaceAll("/,",",").replaceAll("/:", ":");
				String deescapedValue = metaParts[1].replaceAll("/,",",").replaceAll("/:", ":");
				if(gc.getMetadata().containsKey(deescapedKey) && gc.getMetadata().get(deescapedKey).equals(deescapedValue)){
					 result.add(gc);
					 continue contents;
				}
			}
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private List<GameContent> fetchContentDetails(long uid, ResultSet rs, boolean isAdmin) throws SQLException{
		List<GameContent> result = new LinkedList<GameContent>();
		PreparedStatement commentStatement = db.getPreparedStatement(
				"SELECT contentcomments.id, contentcomments.uid, users.name, contentcomments.text, contentcomments.time " +
				"FROM (contentcomments INNER JOIN users ON users.uid = contentcomments.uid) " +
				"WHERE contentid = ? AND deleted = 0;");
		PreparedStatement ratingsStatement = db.getPreparedStatement("SELECT * FROM contentratings WHERE contentid = ?;");
		
		while(rs.next()){
			long ownerId = rs.getLong("owner");
			boolean isFriend = userdb.isFriendOf(ownerId, uid);
			
			if(isAdmin || rs.getInt("visibility") == 2 || (rs.getInt("visibility") == 1 && isFriend)){
				long contentid = rs.getLong("id");
				GameContent content = new GameContent(contentid, rs.getLong("contextid"), ownerId, rs.getString("title"), 
					rs.getString("description"), ContentCategory.values()[rs.getInt("category")], rs.getString("typename"), rs.getString("name"),
					(Map<String, String>) rs.getObject("metadata"),rs.getTimestamp("time"), rs.getInt("hits"), -1, -1, -1);
				
				commentStatement.setLong(1, contentid);
				ratingsStatement.setLong(1, contentid);
				ResultSet commentResult = commentStatement.executeQuery();
				ResultSet ratingsResult = ratingsStatement.executeQuery();
				while(commentResult.next()){
					content.addComment(content.new ContentComment(commentResult.getLong("id"), commentResult.getLong("uid"), contentid, 
							commentResult.getString("name"), commentResult.getString("text"), commentResult.getTimestamp("time")));
				}
				double val = 0;
				int count = 0;
				while(ratingsResult.next()){
					if(ratingsResult.getLong("uid") == uid)
						content.setCurrentUsersRating(ratingsResult.getDouble("value"));
					val += ratingsResult.getDouble("value");
					count++;
				}
				content.setRating(count > 0 ? (Math.ceil((val / (double)count)*100))/100 : -1);
				content.setRatingCount(count);
				result.add(content);
			}
		}
		return result;
	}

	private String appendWhereClause(String[] data, String fieldToMatch, String matchOperator, boolean caseInsensitive){
		//FIXME not secure (see line 151)
		if(data != null && data.length > 0){
			String clause = "AND ( " + 
						(caseInsensitive ? "UPPER(" : "(") + fieldToMatch + ") " + 
							matchOperator + 
							(caseInsensitive ? " UPPER('" : "('") + data[0] + "')";
			for(int i = 1; i < data.length; i++)
					clause += " OR " +
							(caseInsensitive ? "UPPER(" : "(") + fieldToMatch + ") " + 
								matchOperator + 
								(caseInsensitive ? " UPPER('" : "('") + data[i] + "')";
			return clause + ") ";
		}
		return "";
	}

	@Override
	public byte[] downloadContent(long uid, long id, boolean increaseHits) throws SQLException, IOException,
			ContentNotAvailableException, IllegalAccessException {
		if(increaseHits) {
			String query = "UPDATE gamecontent SET hits = hits + 1 WHERE id = ?;";
			PreparedStatement statement = db.getPreparedStatement(query);
			statement.setLong(1, id);
			statement.executeUpdate();
		}
		
		String query = "SELECT content, visibility, owner FROM gamecontent WHERE id = ?;";
		PreparedStatement statement = db.getPreparedStatement(query);
		statement.setLong(1, id);
		ResultSet queryResult = statement.executeQuery();;
		if (!queryResult.next())
			throw new ContentNotAvailableException();
		
		long ownerId = queryResult.getLong("owner");
		int visibility = queryResult.getInt("visibility");
		//uid -2 (call by ContentMediaConverter.convertAudio(long))
		if(!(uid == -2 || visibility == 2 || ownerId == uid || userdb.userIsAdmin(uid) || 
				(visibility == 1 && userdb.isFriendOf(ownerId, uid))))
			throw new IllegalAccessException();
		
		Blob blob = queryResult.getBlob("content");
		if(blob == null)
			throw new ContentNotAvailableException();
		InputStream input = blob.getBinaryStream();
		if (input == null)
			return null;
		ByteArrayOutputStream byteout = new ByteArrayOutputStream();

		while (input.available() > 0)
			byteout.write(input.read());

		return byteout.toByteArray();
	}

	@Override
	public void rateContent(long uid, long id, double rating)
			throws SQLException {
		
		// Already rated?
		String query = "SELECT " +
							"uid " +
						"FROM " +
							"contentratings " +
						"WHERE " +
							"uid = ? " +
							"AND contentid = ?;";
		PreparedStatement statement = db.getPreparedStatement(query);
		statement.setLong(1, uid);
		statement.setLong(2, id);
		ResultSet queryResult = statement.executeQuery();
		
		if (!queryResult.next()) {
			// Insert new entry
			query = 	"INSERT INTO " +
							"contentratings " +
						"VALUES (?,?,?, NOW());";
			statement = db.getPreparedStatement(query);
			statement.setLong(1, uid);
			statement.setLong(2, id);
			statement.setDouble(3, rating);
			statement.executeUpdate();
		}
		else {
			// Update old entry
			query = 	"UPDATE " +
							"contentratings " +
						"SET " +
							"value = ?, " +
							"time = NOW() " +
						"WHERE " +
							"uid = ? " +
							"AND contentid = ?;";
			statement = db.getPreparedStatement(query);
			statement.setDouble(1, rating);
			statement.setLong(2, uid);
			statement.setLong(3, id);
			statement.executeUpdate();
		}
	}

	@Override
	public long addComment(long uid, long contentid, String message) throws SQLException {
		String query = "INSERT INTO contentcomments(uid, contentid, text, time) VALUES (?,?,?, NOW());";
		PreparedStatement statement = db.getPreparedStatementGetKey(query);
		statement.setLong(1, uid);
		statement.setLong(2, contentid);
		statement.setString(3, message);
		statement.executeUpdate();
		
		ResultSet keys = statement.getGeneratedKeys();
		keys.next();
		return keys.getLong(1);
	}

	@Override
	public boolean deleteComment(long uid, long commentid, int delete) throws SQLException, IllegalAccessException {
		String query = "SELECT " +
						"users.isadmin, contentcomments.uid " +
					"FROM " +
						"users, contentcomments " +
					"WHERE " +
						"users.uid = ? AND contentcomments.id = ?;";
		PreparedStatement statement = db.getPreparedStatement(query);
		statement.setLong(1, uid);
		statement.setLong(2, commentid);
		ResultSet rs = statement.executeQuery();
		rs.next();
		boolean isAdmin = rs.getBoolean("isadmin");
		boolean isOwner = rs.getLong("uid") == uid;
		
		if(!isAdmin && !isOwner)
			throw new IllegalAccessException();
		
		statement = db.getPreparedStatement("UPDATE contentcomments SET deleted = ? WHERE id = ?;");
		statement.setInt(1, delete);
		statement.setLong(2, commentid);
		return statement.executeUpdate() > 0;
	}
}