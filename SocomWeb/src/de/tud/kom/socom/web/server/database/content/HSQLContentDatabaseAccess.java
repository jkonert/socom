package de.tud.kom.socom.web.server.database.content;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import de.tud.kom.socom.web.client.sharedmodels.GameContent;
import de.tud.kom.socom.web.client.sharedmodels.GameContentComment;
import de.tud.kom.socom.web.client.sharedmodels.GlobalConfig;
import de.tud.kom.socom.web.server.database.HSQLAccess;
import de.tud.kom.socom.web.server.database.user.HSQLUserDatabaseAccess;
import de.tud.kom.socom.web.server.util.Logger;
import de.tud.kom.socom.web.server.util.LoggerFactory;

public class HSQLContentDatabaseAccess implements ContentDatabaseAccess, GlobalConfig {

	private static ContentDatabaseAccess instance = new HSQLContentDatabaseAccess();
	private Logger logger = LoggerFactory.getLogger();
	private static HSQLAccess db;

	private HSQLContentDatabaseAccess() {
		db = HSQLAccess.getInstance();
	}

	public static ContentDatabaseAccess getInstance() {
		return instance;
	}


	@Override
	public GameContent getGameContent(long uid, long cid) {
		String query = "SELECT " +
				"gamecontent.id, " +
				"gamecontent.visibility, " +
				"gamecontent.title, " +
				"gamecontent.description, " +
				"gamecontexts.name AS contextname, " +
				"contenttypes.name AS contenttype, " +
				"gamecontent.owner, " +
				"users.name AS ownername, " +
				"gamecontent.time, " +
				"AVG(contentratings.value) AS ratingavg, " +
				"COUNT(contentratings.value) AS ratingcount, " +
				"COUNT(contentcomments.id) AS commentcount, " +
				"gamecontent.hits " +
			"FROM " +
				"(((((gamecontent LEFT JOIN users ON gamecontent.owner = users.uid) " +
					"LEFT JOIN gamecontexts ON gamecontexts.id = gamecontent.contextid) " +
						"LEFT JOIN contentratings ON contentratings.contentid = gamecontent.id) " +
							"LEFT JOIN contentcomments ON contentcomments.contentid = gamecontent.id) " +
								"LEFT JOIN contenttypes ON contenttypes.id = gamecontent.type) " +
			"WHERE " +
				"gamecontent.id = " + cid +
				(userIsAdmin(uid) ? " " : 
					" AND (gamecontent.visibility = 2 OR gamecontent.owner = " + uid +
							"OR (gamecontent.visibility = 1 AND " +
									"gamecontent.owner = (SELECT uid FROM usersnfriends WHERE uid = gamecontent.owner AND friendid = " + uid + "))) ") +
			"GROUP BY " +
				"gamecontent.id, " +
				"gamecontent.visibility, " +
				"gamecontent.title, " +
				"gamecontent.description, " +
				"gamecontexts.name, " +
				"gamecontent.type, " +
				"gamecontent.owner, " +
				"contenttypes.name, " +
				"users.name, " +
				"gamecontent.time, " +
				"gamecontent.hits " +
			"ORDER BY " + 
				"gamecontexts.name ASC " + 
			";";
		try {
			ResultSet rs = db.execQueryWithResult(query);
			if(!rs.next())
				return null;
			
			GameContent content = new GameContent(rs.getLong("id"), rs.getInt("visibility"), rs.getString("title"), 
					rs.getString("description"), rs.getString("contextname"), rs.getString("contenttype"), rs.getLong("owner"), 
					rs.getString("ownername"), rs.getTimestamp("time"), rs.getDouble("ratingavg"), rs.getInt("ratingcount"), 
					rs.getInt("commentcount"), rs.getInt("hits"), getLastComment(rs.getLong("id")));

			return content;
		} catch (SQLException e) {
			logger.Error(e);
		}
		return null;
	}
	
	@Override

	public int getGameContentsPages(long userId, long contextId) {
	String query ="SELECT COUNT(DISTINCT gamecontent.id) AS contentcount " +
			"FROM " +
				"gamecontent LEFT JOIN gamecontexts ON gamecontexts.id = gamecontent.contextid " +
			"WHERE " +
				"gamecontexts.id = " + contextId +
				(userIsAdmin(userId) ? " " : " AND (gamecontent.visibility = 2 OR gamecontent.owner = " + userId + "" +
						"OR (gamecontent.visibility = 1 AND " +
						"gamecontent.owner = (SELECT uid FROM usersnfriends WHERE uid = gamecontent.owner AND friendid = " + userId + "))) ");
			try {
				ResultSet rs = db.execQueryWithResult(query);
				if(!rs.next()){
					return -1;
				}
				return (int) Math.ceil(((double) rs.getInt("contentcount")) / EntriesPerPage_ContentContexts);
			} catch (SQLException e) {
				logger.Error(e);
			}
			return -1;
	}
	
	@Override
	public GameContent getGameContentNames(long cid) {
		GameContent result = null;
		try {
			String query = "SELECT " + 
							"gamecontent.id, " + 
							"gamecontent.title, " + 
							"gamecontent.description, " + 
							"gamecontent.time, " + 
							"gamecontent.visibility, " + 
							"gamecontent.hits, " + 
							"gamecontexts.id AS contextid, " + 
							"gamecontexts.name AS contextname, " + 
							"gameinstances.id AS gameid, " + 
							"gameinstances.version, " + 
							"games.name AS gamename " + 
						"FROM "	+ 
							"((gamecontent INNER JOIN gamecontexts ON gamecontent.contextid = gamecontexts.id) " + 
								"INNER JOIN gameinstances ON gamecontexts.gameinstid = gameinstances.gameid) " + 
									"INNER JOIN games ON gameinstances.gameid = games.gameid " + 
						"WHERE " + 
							"gamecontent.id = " + cid;

			ResultSet queryResult = db.execQueryWithResult(query);
			if (!queryResult.next())
				return null;

			result = new GameContent(queryResult.getLong("id"), queryResult.getInt("visibility"), queryResult.getString("title"),
					queryResult.getString("description"), queryResult.getLong("contextid"), queryResult.getString("contextname"), queryResult.getLong("gameid"),
					queryResult.getString("gamename") + " " + queryResult.getString("version"), queryResult.getTimestamp("time"), queryResult.getInt("hits"));
		} catch (Exception e) {
			logger.Error(e);
		}
		return result;
	}

	@Override
	public List<GameContent> getGameContents(long uid, long contextid, int page) {
		List<GameContent> result = new ArrayList<GameContent>();
		
		String query = "SELECT " +
				"gamecontent.id, " +
				"gamecontent.visibility, " +
				"gamecontent.title, " +
				"gamecontent.description, " +
				"gamecontexts.name AS contextname, " +
				"contenttypes.name AS contenttype, " +
				"gamecontent.owner, " +
				"users.name AS ownername, " +
				"gamecontent.time, " +
				"AVG(contentratings.value) AS ratingavg, " +
				"COUNT(contentratings.value) AS ratingcount, " +
				"COUNT(contentcomments.id) AS commentcount, " +
				"gamecontent.hits " +
			"FROM " +
				"(((((gamecontent LEFT JOIN users ON gamecontent.owner = users.uid) " +
					"LEFT JOIN gamecontexts ON gamecontexts.id = gamecontent.contextid) " +
						"LEFT JOIN contentratings ON contentratings.contentid = gamecontent.id) " +
							"LEFT JOIN contentcomments ON contentcomments.contentid = gamecontent.id) " +
								"LEFT JOIN contenttypes ON contenttypes.id = gamecontent.type) " +
			"WHERE " +
				"gamecontent.contextid = " + contextid +
				(userIsAdmin(uid) ? " " : 
					" AND (gamecontent.visibility = 2 OR gamecontent.owner = " + uid + "" +
						"OR (gamecontent.visibility = 1 AND " +
							"gamecontent.owner = (SELECT uid FROM usersnfriends WHERE uid = gamecontent.owner AND friendid = " + uid + "))) ") +
			"GROUP BY " +
				"gamecontent.id, " +
				"gamecontent.visibility, " +
				"gamecontent.title, " +
				"gamecontent.description, " +
				"gamecontexts.name, " +
				"contenttypes.name, " +
				"gamecontent.type, " +
				"gamecontent.owner, " +
				"users.name, " +
				"gamecontent.time, " +
				"gamecontent.hits " +
			"ORDER BY " + 
				"gamecontexts.name ASC " + 
			"LIMIT " +
				((page - 1) * EntriesPerPage_ContentContexts) + ", " + 
				EntriesPerPage_ContentContexts + ";";
		
		try {
			ResultSet rs = db.execQueryWithResult(query);
			
			while(rs.next()){
				GameContent content = new GameContent(rs.getLong("id"), rs.getInt("visibility"), rs.getString("title"), 
						rs.getString("description"), rs.getString("contextname"), rs.getString("contenttype"), rs.getLong("owner"), 
						rs.getString("ownername"), rs.getTimestamp("time"), rs.getDouble("ratingavg"), rs.getInt("ratingcount"), 
						rs.getInt("commentcount"), rs.getInt("hits"), getLastComment(rs.getLong("id")));
				result.add(content);
			}			
		} catch (SQLException e) {
			logger.Error(e);
		}
		
		return result;
	}
	
	@Override
	public List<GameContentComment> getGameContentComments(long contentId, int page) {
		List<GameContentComment> result = new ArrayList<GameContentComment>();
		try {
			String query = "SELECT " + 
							"contentcomments.id, " + 
							"contentcomments.text, " + 
							"contentcomments.time, " + 
							"users.uid AS ownerid, " + 
							"users.name AS ownername " + 
						"FROM " + 
							"contentcomments INNER JOIN users ON contentcomments.uid = users.uid " + 
						"WHERE " + 
							"contentcomments.contentid = " + contentId + " " + 
						"ORDER BY " + 
							"time ASC " + 
						"LIMIT " + 
							((page - 1) * EntriesPerPage_ContentComments) + ", " + 
							EntriesPerPage_ContentComments;
			ResultSet queryResult = db.execQueryWithResult(query);

			while (queryResult.next()) {
				result.add(new GameContentComment(queryResult.getLong("id"), queryResult.getLong("ownerid"), queryResult.getString("ownername"), queryResult
						.getString("text"), queryResult.getTimestamp("time")));
			}
		} catch (Exception e) {
			logger.Error(e);
		}
		return result;
	}

	@Override
	public int getGameCommentsPages(long contentId) {
		int result = 0;
		try {
			String query = "SELECT " + 
							"COUNT(DISTINCT id) AS gamecontentcommentscount " +
						"FROM " + 
							"contentcomments " + 
						"WHERE " + 
							"contentid = " + contentId
					+ ";";
			ResultSet queryResult = db.execQueryWithResult(query);

			if (!queryResult.next())
				return -1;
			result = (int) Math.ceil(((double) queryResult.getInt("gamecontentcommentscount")) / EntriesPerPage_ContentComments);
		} catch (Exception e) {
			logger.Error(e);
		}
		return result;
	}

	@Override
	public boolean setGameContentRating(long userId, long contentId, double rating) {
		try {
			// Already rated?
			String query = "SELECT " + "uid " + "FROM " + "contentratings " + "WHERE " + "uid = " + userId + " " + "AND contentid = " + contentId + ";";
			ResultSet queryResult = db.execQueryWithResult(query);

			if (!queryResult.next()) {
				// Insert new entry
				query = "INSERT INTO " + "contentratings " + "VALUES (" + userId + ", " + contentId + ", " + rating + ", " + "NOW());";
				db.execQuery(query);
			} else {
				// Update old entry
				query = "UPDATE " + "contentratings " + "SET " + "value = " + rating + ", " + "time = NOW() " + "WHERE " + "uid = " + userId + " "
						+ "AND contentid = " + contentId + ";";
				db.execQuery(query);
			}
		} catch (Exception e) {
			logger.Error(e);
			return false;
		}
		return true;
	}

	@Override
	public boolean setGameContentComment(long userId, long contentId, String text) {
		try {
			String query = "INSERT INTO contentcomments(uid, contentid, text, time) VALUES (" + userId + ", " + contentId
					+ ", '" + text + "', " + "NOW());";
			db.execQuery(query);
		} catch (Exception e) {
			logger.Error(e);
			return false;
		}
		return true;
	}

	@Override
	public boolean setContentVisibility(long userId, long contentId, int selectedIndex) {
		try {
			String query = "UPDATE " + "gamecontent " + "SET " + "visibility = " + selectedIndex + " " + "WHERE " + "id = " + contentId + " " + "AND owner = "
					+ userId + ";";
			db.execQuery(query);
		} catch (Exception e) {
			logger.Error(e);
			return false;
		}
		return true;
	}

	@Override
	public boolean removeContent(long userId, long contentId) {
		try {
			if (userIsAdmin(userId) || userOwnsContent(userId, contentId)) {
				String query = "DELETE FROM " + "gamecontent " + "WHERE " + "id = " + contentId;
				db.execQuery(query);

				query = "DELETE FROM " + "contentcomments " + "WHERE " + "contentid = " + contentId;
				db.execQuery(query);

				query = "DELETE FROM " + "contentratings " + "WHERE " + "contentid = " + contentId;
				db.execQuery(query);
			} else
				return false;
		} catch (Exception e) {
			logger.Error(e);
			return false;
		}
		return true;
	}

	@Override
	public boolean removeContentComment(long userId, long commentId) {
		try {
			if (userIsAdmin(userId) || userOwnsComment(userId, commentId)) {
				String query = "DELETE FROM " + "contentcomments " + "WHERE " + "id = " + commentId;
				db.execQuery(query);
			} else
				return false;
		} catch (Exception e) {
			logger.Error(e);
			return false;
		}
		return true;
	}

	private GameContentComment getLastComment(long cid) {
		GameContentComment result = null;
		try {
			String query = "SELECT " + 
						"contentcomments.id, " + 
						"contentcomments.text, " + 
						"contentcomments.time, " + 
						"users.uid AS ownerid, " + 
						"users.name AS ownername " + 
					"FROM " + 
						"contentcomments INNER JOIN users ON contentcomments.uid = users.uid " + 
					"WHERE " + 
						"contentcomments.contentid = " + cid + " " + 
					"ORDER BY " + 
						"time DESC " + 
					"LIMIT 0, 1";
			ResultSet queryResult = db.execQueryWithResult(query);

			if (queryResult.next())
				result = new GameContentComment(queryResult.getLong("id"), queryResult.getLong("ownerid"), queryResult.getString("ownername"),
						queryResult.getString("text"), queryResult.getTimestamp("time"));
		} catch (Exception e) {
			logger.Error(e);
		}
		return result;
	}

	private boolean userOwnsContent(long userId, long contentId) {
		boolean result = false;
		try {
			String query = "SELECT owner FROM gamecontent WHERE id = " + contentId + ";";
			ResultSet queryResult = db.execQueryWithResult(query);

			if (!queryResult.next())
				return false;

			result = (queryResult.getLong("owner") == userId);
		} catch (Exception e) {
			logger.Error(e);
			return false;
		}
		return result;
	}

	private boolean userOwnsComment(long userId, long commentId) {
		boolean result = false;
		try {
			String query = "SELECT uid FROM contentcomments WHERE id = " + commentId + ";";
			ResultSet queryResult = db.execQueryWithResult(query);

			if (!queryResult.next())
				return false;

			result = (queryResult.getLong("uid") == userId);
		} catch (Exception e) {
			logger.Error(e);
			return false;
		}
		return result;
	}


	@Override
	public boolean registerContentHit(long cid) {
		try {
			String query = "UPDATE gamecontent SET hits = hits + 1 WHERE id = '" + cid + "'";
			return db.execQuery(query) == 1;
		} catch (SQLException e) {
			logger.Error(e);
			return false;
		}
	}
	
	private boolean userIsAdmin(long userId) {
		return HSQLUserDatabaseAccess.getInstance().userIsAdmin(userId);
	}

	@Override
	public byte[] getContentBytes(long contentid) {
		try{
			String query = "SELECT content FROM gamecontent WHERE id = " + contentid;
			ResultSet rs = db.execQueryWithResult(query);
			if(!rs.next())
				return null;
			Blob b = rs.getBlob("content");
			if(b == null)
				return null;
			InputStream input = b.getBinaryStream();
			if (input == null)
				return null;
			ByteArrayOutputStream byteout = new ByteArrayOutputStream();
			while (input.available() > 0)
				byteout.write(input.read());

			byte[] contentBytes = byteout.toByteArray();
			return contentBytes;
		}catch(IOException e){
			logger.Error(e);
		} catch (SQLException e) {
			logger.Error(e);
		}
		return null;
	}

	@Deprecated
	public GameContent getGameContent1(long uid, long cid) {	
		logger.Error("DEPRECATED METHOD USED (getGameContent1)");
		GameContent result = null;
		try {
			// Blobs do not work with AVG, so two queries are necessary
			String query = 
					"SELECT " + 
							"gamecontent.content " + 
					"FROM " + 
							"(gamecontent INNER JOIN gamecontexts ON gamecontent.contextid = gamecontexts.id) " + 
							"INNER JOIN userprogress ON gamecontent.contextid = userprogress.scnid " + 
					"WHERE " + 
							"gamecontent.id = " + cid + " "	+ 
							"AND userprogress.uid = " + uid + ";";
			ResultSet queryResult = db.execQueryWithResult(query);
	
			if (!queryResult.next())
				return null;
	
			Blob b = queryResult.getBlob("content");
			if(b == null)
				return null;
			InputStream input = b.getBinaryStream();
			if (input == null)
				return null;
			ByteArrayOutputStream byteout = new ByteArrayOutputStream();
	
			while (input.available() > 0)
				byteout.write(input.read());
	
			@SuppressWarnings("unused")
			byte[] content = byteout.toByteArray();
	
			query = "SELECT " + 
						"gamecontent.id, " + 
						"gamecontent.title, " + 
						"gamecontent.description, " + 
						"gamecontent.time, " + 
						"gamecontent.visibility, " + 
						"gamecontent.hits, " + 
						"contenttypes.name AS typename, " + 
						"users.uid AS ownerid, " + 
						"users.name AS ownername, "	+ 
						"gamecontexts.name AS context, " + 
						"ratingsown.value AS ratingown, " + 
						"fim.friendid IS NOT NULL AS isfriend, " + 
						"COUNT(DISTINCT contentcomments.id) as comments, " + 
						"COUNT(ratingsavg.value) as ratingscount, " + 
						"AVG(ratingsavg.value) AS ratingavg " + 
					"FROM " + 
						"((((((gamecontent INNER JOIN contenttypes ON gamecontent.type = contenttypes.id) "	+ 
							"INNER JOIN users ON gamecontent.owner = users.uid) " + 
							"INNER JOIN gamecontexts ON gamecontent.contextid = gamecontexts.id) " + 
							"INNER JOIN userprogress ON gamecontent.contextid = userprogress.scnid) "	+ 
							"LEFT JOIN (SELECT contentid, value FROM contentratings WHERE uid = " + uid + ") " +
									"AS ratingsown ON gamecontent.id = ratingsown.contentid) " + 
							"LEFT JOIN (" + 
									"SELECT " + 
										"uid, "	+ 
										"friendid " + 
									"FROM "	+ 
										"usersnfriends " + 
									"WHERE " + 
										"friendid = " + uid	+ ") " +
								"AS fim ON gamecontent.owner = fim.uid " + 
							"LEFT JOIN contentratings AS ratingsavg ON gamecontent.id = ratingsavg.contentid) "	+ 
							"LEFT JOIN contentcomments ON gamecontent.id = contentcomments.contentid " + 
					"WHERE " + 
						"gamecontent.id = "	+ cid + " " + 
						"AND userprogress.uid = " + uid	+ " " + 
					"GROUP BY "	+ 
						"gamecontent.id, " + 
						"gamecontent.title, " + 
						"gamecontent.description, "	+ 
						"gamecontent.time, " + 
						"gamecontent.visibility, " + 
						"gamecontent.hits, " + 
						"contenttypes.name, " + 
						"users.uid, " + 
						"users.name, " + 
						"gamecontexts.name, " + 
						"ratingsown.value, " + 
						"fim.friendid";
	
			queryResult = db.execQueryWithResult(query);
			if (!queryResult.next())
				return null;
	
			if (queryResult.getInt("visibility") == 2 || (queryResult.getInt("visibility") == 1 && queryResult.getBoolean("isfriend")) || userIsAdmin(uid)
					|| uid == queryResult.getInt("ownerid"))
				result = new GameContent(queryResult.getLong("id"), queryResult.getInt("visibility"), queryResult.getString("title"),
						queryResult.getString("description"), queryResult.getString("context"), queryResult.getString("typename"), queryResult.getInt("ownerid"),
						queryResult.getString("ownername"), queryResult.getTimestamp("time"), queryResult.getDouble("ratingavg"),
						queryResult.getInt("ratingscount"), queryResult.getInt("comments"),
						queryResult.getInt("hits"), getLastComment(cid));
			else
				result = new GameContent(queryResult.getLong("id"), -1, null, null, null, null, -1, null, null, -1, -1, -1,  -1, null);
		} catch (Exception e) {
			logger.Error(e);
		}
		return result;
	}

	@Deprecated
	public List<GameContent> getGameContents1(long uid, long gid, int page) {
		logger.Error("DEPRECATED METHOD USED (getGameContents1)");
	
		List<GameContent> result = new ArrayList<GameContent>();
		try {
			String query = "SELECT " + 
								"gamecontent.id, " + 
								"gamecontent.title, " + 
								"gamecontent.description, " + 
								"gamecontent.time, " + 
								"gamecontent.visibility, " + 
								"gamecontent.hits, " + 
								"contenttypes.name AS typename, " + 
								"users.uid AS ownerid, " + 
								"users.name AS ownername, " + 
								"gamecontexts.name AS context, " + 
								"ratingsown.value AS ratingown, " + 
								"COUNT(DISTINCT contentcomments.id) as comments, " + 
								"COUNT(ratingsavg.value) as ratingscount, " + 
								"AVG(ratingsavg.value) AS ratingavg " + 
							"FROM " + 
								"(((((((gamecontent INNER JOIN contenttypes ON gamecontent.type = contenttypes.id) " + 
									"INNER JOIN users ON gamecontent.owner = users.uid) " + 
										"INNER JOIN gamecontexts ON gamecontent.contextid = gamecontexts.id) " + 
											"INNER JOIN userprogress ON gamecontent.contextid = userprogress.scnid) "	+ 
												"LEFT JOIN (" +
														"SELECT contentid, value FROM contentratings WHERE uid = " + uid + ") " +
															"AS ratingsown ON gamecontent.id = ratingsown.contentid) " + 
													"LEFT JOIN (" + 
															"SELECT " + 
																"uid, " + 
																"friendid "	+ 
															"FROM "	+ 
																"usersnfriends " + 
															"WHERE " + 
																"friendid = " + uid	+ ") " +
																"AS fim ON gamecontent.owner = fim.uid) " + 
														"LEFT JOIN contentratings AS ratingsavg ON gamecontent.id = ratingsavg.contentid) "	+ 
															"LEFT JOIN contentcomments ON gamecontent.id = contentcomments.contentid " + 
							"WHERE " + 
								"gamecontexts.gameinstid = " + gid + " " + 
									"AND userprogress.uid = " + uid	+ " " + 
									(!userIsAdmin(uid) ? 
									"AND (gamecontent.visibility = 2 OR (gamecontent.visibility = 1 AND fim.friendid IS NOT NULL))" : "") + 
							"GROUP BY "	+ 
								"gamecontent.id, " + 
								"gamecontent.title, " + 
								"gamecontent.time, " + 
								"gamecontent.description, "	+ 
								"gamecontent.visibility, " + 
								"gamecontent.hits, " + 
								"contenttypes.name, " + 
								"users.uid, " + 
								"users.name, " + 
								"gamecontexts.name, "	+ 
								"ratingsown.value "	+ 
							"ORDER BY " + 
								"gamecontexts.name ASC " + 
							"LIMIT " + 
								((page - 1) * EntriesPerPage_ContentContexts) + ", " + 
								EntriesPerPage_ContentContexts + ";";
			
			ResultSet queryResult = db.execQueryWithResult(query);
	
			while (queryResult.next()) {
				result.add(new GameContent(queryResult.getLong("id"), queryResult.getInt("visibility"), queryResult.getString("title"), queryResult
						.getString("description"), queryResult.getString("context"), queryResult.getString("typename"), queryResult.getInt("ownerid"),
						queryResult.getString("ownername"), queryResult.getTimestamp("time"), queryResult.getDouble("ratingavg"),
						queryResult.getInt("ratingscount"), queryResult.getInt("comments"), queryResult.getInt("hits"),
						getLastComment(queryResult.getLong("id"))));
			}
		} catch (Exception e) {
			logger.Error(e);
		}
		return result;
	}

	@Deprecated
	public int getGameContentsPages1(long userId, long gameId) {
		logger.Error("DEPRECATED METHOD USED (getGameContentsPages1)");
	
		int result = 0;
		try {
			String query = "SELECT " + 
						"COUNT(DISTINCT gamecontent.id) AS gamecontentscount " + 
					"FROM "	+ 
						"((gamecontent INNER JOIN gamecontexts ON gamecontent.contextid = gamecontexts.id) " + 
							"INNER JOIN userprogress ON gamecontent.contextid = userprogress.scnid) " + 
								"LEFT JOIN (" + 
										"SELECT " + 
											"uid, " + 
											"friendid " + 
										"FROM " + 
											"usersnfriends " + 
										"WHERE " + 
											"friendid = " + userId + ") AS fim ON gamecontent.owner = fim.uid " + 
					"WHERE " + 
						"gamecontexts.gameinstid = " + gameId + " " + 
						"AND userprogress.uid = " + userId + 
						(!userIsAdmin(userId) ? 
								"AND (gamecontent.visibility = 2 " +
								"OR (gamecontent.visibility = 1 AND fim.friendid IS NOT NULL))" : "");
			
			ResultSet queryResult = db.execQueryWithResult(query);
	
			if (!queryResult.next())
				return -1;
			result = (int) Math.ceil(((double) queryResult.getInt("gamecontentscount")) / EntriesPerPage_ContentContexts);
		} catch (Exception e) {
			logger.Error(e);
		}
		return result;
	}
}
