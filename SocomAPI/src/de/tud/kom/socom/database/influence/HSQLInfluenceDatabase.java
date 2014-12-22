package de.tud.kom.socom.database.influence;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Random;

import de.tud.kom.socom.components.influence.InfluenceFactory;
import de.tud.kom.socom.database.HSQLDatabase;
import de.tud.kom.socom.database.game.HSQLGameDatabase;
import de.tud.kom.socom.database.user.HSQLUserDatabase;
import de.tud.kom.socom.util.datatypes.InfluenceConfiguration;
import de.tud.kom.socom.util.datatypes.InfluenceResult;
import de.tud.kom.socom.util.exceptions.AlreadyStartedException;
import de.tud.kom.socom.util.exceptions.ContextNotFoundException;
import de.tud.kom.socom.util.exceptions.IllegalAccessException;
import de.tud.kom.socom.util.exceptions.IllegalParameterException;
import de.tud.kom.socom.util.exceptions.InfluenceNotAvailableException;
import de.tud.kom.socom.util.exceptions.InfluenceTemplateException;

public class HSQLInfluenceDatabase extends HSQLDatabase implements InfluenceDatabase {

	private static InfluenceDatabase instance = new HSQLInfluenceDatabase();

	private HSQLInfluenceDatabase() {
		super();
	}

	public static InfluenceDatabase getInstance() {
		return instance;
	}

	@Override
	public String copyInfluenceTemplate(long uid, long gid, String templateid) throws SQLException, InfluenceNotAvailableException, InfluenceTemplateException {
		String queryS = "SELECT id, gameinstid, contextid, question, type, allowfreeanswers, " +
				"minchoices, maxchoices, maxdigits, maxlines, visibility, freevotable, maxbytes, template " +
				"FROM influence WHERE externalid = ?;";
		PreparedStatement query = db.getPreparedStatement(queryS);
		query.setString(1, templateid);
		ResultSet rs = query.executeQuery();
		if(!rs.next()) 
			throw new InfluenceNotAvailableException();
		if(!rs.getBoolean("template"))
			throw new InfluenceTemplateException("not an template");
		if(rs.getLong("gameinstid") != gid)
			throw new InfluenceTemplateException("games do not match");
		
		String externalid = getRandomExternalId();
		
		long contextid = rs.getLong("contextid");
		String insertQ1 = "INSERT INTO influence (externalid, gameinstid, ownerid, " +
				"question, timeout, type, allowfreeanswers, minchoices, maxchoices, maxdigits, maxlines, visibility, freevotable, " +
				"maxbytes, contextid) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
		if(contextid == 0) {
			insertQ1 = "INSERT INTO influence (externalid, gameinstid, ownerid, " +
					"question, timeout, type, allowfreeanswers, minchoices, maxchoices, maxdigits, maxlines, visibility, freevotable, " +
					"maxbytes) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
			}
		PreparedStatement insert = db.getPreparedStatement(insertQ1);
		
		insert.setString(1, externalid);
		insert.setLong(2, gid);
		insert.setLong(3, uid);
		insert.setString(4, rs.getString("question"));
		insert.setTimestamp(5, new Timestamp(0));
		insert.setLong(6, rs.getLong("type"));
		insert.setBoolean(7, rs.getBoolean("allowfreeanswers"));
		insert.setInt(8, rs.getInt("minchoices"));
		insert.setInt(9, rs.getInt("maxchoices"));
		insert.setInt(10, rs.getInt("maxdigits"));
		insert.setInt(11, rs.getInt("maxlines"));
		insert.setInt(12, rs.getInt("visibility"));
		insert.setBoolean(13, rs.getBoolean("freevotable"));
		insert.setLong(14, rs.getLong("maxbytes"));
		if(contextid != 0) insert.setLong(15, contextid);
		insert.executeUpdate();
		
		copyPredefinedAnswers(rs.getLong("id"), externalid);
		
		return externalid;
	}

	private void copyPredefinedAnswers(long fromId, String toExternalId) throws SQLException {
		String query = "INSERT INTO INFLUENCEPREDEFINEDANSWERS " + 
				"(influenceid, text, answercount) " +
				"VALUES " +
				"((SELECT id FROM influence WHERE externalid = ?), " +
				"?,0);";
		
		PreparedStatement statement = db.getPreparedStatement(query);
		statement.setString(1, toExternalId);
		
		String select = "SELECT text FROM influencepredefinedanswers WHERE influenceid = " + fromId;
		ResultSet rs = db.execQueryWithResult(select);
		while(rs.next()) {
			statement.setString(2, rs.getString("text"));
			statement.addBatch();
		}
		
		statement.executeBatch();		
	}

	@Override
	public String prepareInfluence(long uid, long gid, String context, String question, String type, boolean allowFreeAnswers, int minChoices, int maxChoices,
			int maxlines, int maxdigits, int visibility, boolean freeAnswersVotable, long maxBytes, boolean template) throws SQLException, IllegalParameterException {
		PreparedStatement query = db.getPreparedStatement("INSERT INTO influence "
				+ "(externalid, gameinstid, contextid, ownerid, question, type, allowfreeanswers, "
				+ "minchoices, maxchoices, maxdigits, maxlines, visibility, freevotable, maxbytes, timeout, template)" +
				" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);");
		long typeId = getId("influencetypes", type);

		String externalId = getRandomExternalId();

		query.setString(1, externalId);
		query.setLong(2, gid);
		if (context != "") {
			try {
				query.setLong(3, HSQLGameDatabase.getInstance().getGameContextId(context, gid));
			} catch (ContextNotFoundException e) {
				// Do not set
				query.setNull(3, java.sql.Types.BIGINT);
			}
		} else
			query.setNull(3, java.sql.Types.BIGINT);
		query.setLong(4, uid);
		query.setString(5, question);
		query.setLong(6, typeId);
		query.setBoolean(7, allowFreeAnswers);
		query.setInt(8, minChoices);
		query.setInt(9, maxChoices);
		query.setInt(10, maxdigits);
		query.setInt(11, maxlines);
		query.setInt(12, visibility);
		query.setBoolean(13, freeAnswersVotable);
		query.setLong(14, maxBytes);
		if(template) query.setNull(15, java.sql.Types.TIMESTAMP);
		else query.setTimestamp(15, new Timestamp(0));
		query.setBoolean(16, template);
		query.execute();
		
		return externalId;
	}

	private String getRandomExternalId() throws SQLException {
		PreparedStatement selectQuery = db.getPreparedStatement("SELECT id FROM influence WHERE externalid = ?;");

		String externalId = Math.abs(new Random().nextInt()) + "";
		selectQuery.setString(1, externalId);
		while (selectQuery.executeQuery().next()) {
			externalId = new Random().nextInt() + "";
			selectQuery.setString(1, externalId);
		}
		return externalId;
	}

	@Override
	public long addPredefinedAnswer(long uid, String externalId, String answer) throws SQLException, InfluenceNotAvailableException, IllegalAccessException {
		long id = getInfluenceId(uid, externalId);
		PreparedStatement query = db.getPreparedStatementGetKey("INSERT INTO influencepredefinedanswers (influenceid, text, answercount) VALUES (?,?,?);");

		query.setLong(1, id);
		query.setString(2, answer);
		query.setInt(3, 0);
		query.executeUpdate();

		ResultSet keys = query.getGeneratedKeys();
		keys.next();
		return keys.getInt(1);
	}

	@Override
	public boolean removePredefinedAnswer(long uid, String externalid, long answerid) throws SQLException {
		PreparedStatement statement = db.getPreparedStatementGetKey(
//				"UPDATE INFLUENCEPREDEFINEDANSWERS SET deleted = 1 " + // if we just want to set 'deleted'
				"DELETE FROM INFLUENCEPREDEFINEDANSWERS " + // but in this case i think we really want do
				"WHERE id = ? " +
					"AND influenceid = (SELECT id from influence where externalid = ?) " +
					"AND ((SELECT ownerid FROM influence LEFT JOIN users ON influence.ownerid = users.uid " +
						"WHERE externalid = ?) = ? " +
					"OR (SELECT isadmin FROM users WHERE uid = ?))");
		statement.setLong(1, answerid);
		statement.setString(2, externalid);
		statement.setString(3, externalid);
		statement.setLong(4, uid);
		statement.setLong(5, uid);
		return statement.executeUpdate() == 1;
	}

	@Override
	public long addFreeAnswer(long uid, String externalId, String answer) throws SQLException, InfluenceNotAvailableException, InfluenceTemplateException {
		PreparedStatement selQuery = db.getPreparedStatement("SELECT id, template FROM influence WHERE externalId = ?;");
		selQuery.setString(1, externalId);
		ResultSet rs = selQuery.executeQuery();

		if (!rs.next())
			throw new InfluenceNotAvailableException();

		long id = rs.getLong("id");
		if(rs.getBoolean("template"))
			throw new InfluenceTemplateException("is template");
		
		PreparedStatement query = db.getPreparedStatementGetKey("INSERT INTO influencefreeanswers (influenceid, text, ownerid, answercount) "
				+ "VALUES (" + id + ", ?," + uid + ",0);");
		query.setString(1, answer);
		query.executeUpdate();
		ResultSet keys = query.getGeneratedKeys();
		keys.next();
		return keys.getLong(1);
	}

	@Override
	public void startInfluence(long uid, String externalId, int duration) throws SQLException, InfluenceNotAvailableException, IllegalAccessException, InfluenceTemplateException {
		long id = getInfluenceId(uid, externalId);
		boolean template = isInfluenceTemplate(id);
		if(template) throw new InfluenceTemplateException("is template");

		PreparedStatement query = db.getPreparedStatement("UPDATE influence SET timeout = NOW() + ? SECOND WHERE id = ? AND ownerid = " + uid + ";");

		if(duration == -1) query.setNull(1, java.sql.Types.TIMESTAMP);
		else query.setLong(1, duration);
		query.setLong(2, id);
		query.execute();
	}

	private boolean isInfluenceTemplate(long id) throws SQLException, InfluenceNotAvailableException {
		PreparedStatement query = db.getPreparedStatement("SELECT template FROM influence WHERE id = ?;");
		query.setLong(1, id);
		ResultSet rs = query.executeQuery();
		if(!rs.next())
			throw new InfluenceNotAvailableException();
		return rs.getBoolean("template");
	}

	@Override
	public void stopInfluence(long uid, String externalId) throws SQLException, InfluenceNotAvailableException, IllegalAccessException, InfluenceTemplateException {
		startInfluence(uid, externalId, 0);
	}

	@Override
	public InfluenceResult fetchResult(long uid, String externalId) throws SQLException, InfluenceNotAvailableException, IllegalAccessException, InfluenceTemplateException {
		long id = getInfluenceId(uid, externalId);

		String queryInfluence = "SELECT template, question, allowfreeanswers, freevotable " +
				"FROM influence WHERE id = ?;";
		PreparedStatement statement = db.getPreparedStatement(queryInfluence);
		statement.setLong(1, id);
		ResultSet rs = statement.executeQuery();
		if (!rs.next())
			throw new InfluenceNotAvailableException();
		if(rs.getBoolean("template"))
			throw new InfluenceTemplateException("is template");
		boolean allowFreeAnswers = rs.getBoolean("allowfreeanswers");
		boolean freeAnswersVotable = rs.getBoolean("freevotable");

		InfluenceResult result = new InfluenceResult(rs.getString("question"), allowFreeAnswers, freeAnswersVotable);

		String queryPredefined = "SELECT id, text, answercount FROM influencepredefinedanswers WHERE influenceid = ?;";
		statement = db.getPreparedStatement(queryPredefined);
		statement.setLong(1, id);
		rs = statement.executeQuery();
		while (rs.next()) {
			result.addPredefinedAnswer(rs.getLong("id"), rs.getString("text"), rs.getInt("answercount"));
		}

		if (allowFreeAnswers) {
			String queryFree = "SELECT id, text, answercount, deleted FROM influencefreeanswers WHERE influenceid = ?;";
			statement = db.getPreparedStatement(queryFree);
			statement.setLong(1, id);
			rs = statement.executeQuery();
			while (rs.next()) {
				if (rs.getInt("deleted") == 0) {
					if (freeAnswersVotable) {
						result.addFreeAnswer(rs.getLong("id"), rs.getString("text"), rs.getInt("answercount"));
					} else
						result.addFreeAnswer(rs.getLong("id"), rs.getString("text"));
				}
			}
		}
		return result;
	}

	@Override
	public long getInfluenceId(long uid, String externalId) throws SQLException, InfluenceNotAvailableException, IllegalAccessException {
		String query = "SELECT id, ownerid, visibility FROM influence WHERE externalId = ?;";
		PreparedStatement statement = db.getPreparedStatement(query);
		statement.setString(1, externalId);
		ResultSet rs = statement.executeQuery();

		if (!rs.next())
			throw new InfluenceNotAvailableException();

		long ownerId = rs.getLong("ownerid");
		int visibility = rs.getInt("visibility");
		if (!(ownerId == uid || visibility == 2 || HSQLUserDatabase.getInstance().userIsAdmin(uid) || (visibility == 1 && HSQLUserDatabase.getInstance()
				.isFriendOf(ownerId, uid))))
			throw new IllegalAccessException();

		return rs.getLong("id");
	}

	@Override
	public long getMaxUploadSize(String influenceId) throws InfluenceNotAvailableException, SQLException {
		String query = "SELECT maxBytes FROM influence WHERE externalId = ?;";
		PreparedStatement statement = db.getPreparedStatement(query);
		statement.setString(1, influenceId);
		ResultSet rs = statement.executeQuery();
		if (!rs.next())
			throw new InfluenceNotAvailableException();
		return rs.getLong(1);
	}

	@Override
	public InfluenceConfiguration readConfiguration(long uid, String externalid) throws SQLException, InfluenceNotAvailableException, IllegalAccessException {
		PreparedStatement query = db.getPreparedStatement("SELECT * " +
				"FROM influence LEFT JOIN influencetypes ON influencetypes.id = influence.type " +
				"WHERE externalid = ?;");
		query.setString(1, externalid);
		ResultSet rs = query.executeQuery();
		if(!rs.next())
			throw new InfluenceNotAvailableException();
		if(rs.getLong("ownerid") != uid)
			throw new IllegalAccessException();
		
		String question = rs.getString("question"), type = rs.getString("name"); //name is from join the type-name
		long contextid = rs.getLong("contextid");
		long gameInstanceId = rs.getLong("gameinstid"), contextId = contextid == 0 ? -1 : contextid;
		int minchoices = rs.getInt("minchoices"), maxchoices = rs.getInt("maxchoices"), 
				maxdigits = rs.getInt("maxdigits"), maxlines = rs.getInt("maxlines"), maxbytes = rs.getInt("maxbytes"), 
				visibility = rs.getInt("visibility");
		boolean allowFreeAnswers = rs.getBoolean("allowFreeanswers"), freeAnswersVotable = rs.getBoolean("freevotable"), 
				isTemplate = rs.getBoolean("template");
		
		return new InfluenceConfiguration(externalid, question, type, gameInstanceId, contextId, minchoices, 
				maxchoices, maxdigits, maxlines, maxbytes, visibility, allowFreeAnswers, freeAnswersVotable, isTemplate);
	}

	@Override
	public boolean changeConfiguration(long uid, String externalid, String question, String type,
			int minchoices, int maxchoices, int maxdigits, int maxlines, int maxBytes, int visibility,
			boolean containsAllowFree, boolean allowFree, boolean containsFreeVotable, boolean freeVotable) throws SQLException, InfluenceNotAvailableException, IllegalAccessException, IllegalParameterException, AlreadyStartedException{
		
		PreparedStatement query1 = db.getPreparedStatement("SELECT * FROM influence WHERE externalid = ?;");
		query1.setString(1, externalid);
		ResultSet rs = query1.executeQuery();
		if(!rs.next())
			throw new InfluenceNotAvailableException();
		if(rs.getLong("ownerid") != uid)
			throw new IllegalAccessException();
		Timestamp timeout = rs.getTimestamp("timeout");
		if(timeout != null && timeout.getTime() > 0)
			throw new AlreadyStartedException();
		
		if(minchoices == -1) minchoices = rs.getInt("minchoices");
		if(maxchoices == -1) maxchoices = rs.getInt("maxchoices");
		if(maxdigits == -1) maxdigits = rs.getInt("maxdigits");
		if(maxlines == -1) maxlines = rs.getInt("maxlines");
		if(maxBytes == -1) maxBytes = rs.getInt("maxbytes");
		if(visibility == -1) visibility = rs.getInt("visibility");
		
		if(!containsAllowFree) allowFree = rs.getBoolean("allowfreeanswers");
		if(!containsFreeVotable) freeVotable = rs.getBoolean("freevotable");
 		
		InfluenceFactory.checkInfluenceConfiguration(minchoices, maxchoices, allowFree, freeVotable, type, maxlines, maxdigits, maxBytes);
			
		String query2 = "UPDATE influence SET externalid = externalid ";
		if(question != null)	query2 += ", question = ? ";
		if(type != null) {
			long typeId = lazyInsert("influencetypes", type);
			query2 += ", type = " + typeId;
		}
		query2 += ", minchoices = " + minchoices;
		query2 += ", maxchoices = " + maxchoices;
		query2 += ", maxdigits = " + maxdigits;
		query2 += ", maxlines = " +  maxlines;
		query2 += ", maxBytes = " + maxBytes;
		query2 += ", visibility = " + visibility;
		query2 += ", allowfreeanswers = " + allowFree;
		query2 += ", freevotable = " + freeVotable;
		query2 += " WHERE externalid = ?";
		
		PreparedStatement statement = db.getPreparedStatement(query2);
		if(question != null) statement.setString(1, question);
		statement.setString(question != null ? 2 : 1, externalid);
		
		return statement.executeUpdate() == 1;
	}
}
