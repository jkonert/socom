package de.tud.kom.socom.web.server.database.game;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.tud.kom.socom.web.client.sharedmodels.GameContext;
import de.tud.kom.socom.web.client.sharedmodels.GameInstance;
import de.tud.kom.socom.web.client.sharedmodels.GlobalConfig;
import de.tud.kom.socom.web.server.database.HSQLAccess;
import de.tud.kom.socom.web.server.database.user.HSQLUserDatabaseAccess;
import de.tud.kom.socom.web.server.util.Logger;
import de.tud.kom.socom.web.server.util.LoggerFactory;

public class HSQLGameDatabaseAccess implements GameDatabaseAccess, GlobalConfig {

	private static GameDatabaseAccess instance = new HSQLGameDatabaseAccess();
	private Logger logger = LoggerFactory.getLogger();
	private static HSQLAccess db;

	private HSQLGameDatabaseAccess() {
		db = HSQLAccess.getInstance();
	}

	public static GameDatabaseAccess getInstance() {
		return instance;
	}

	@Override
	public String getGameName(long gid) {
		String result = "";
		try {
			String query = "SELECT " + "games.name, " + "gameinstances.version " + "FROM "
					+ "games INNER JOIN gameinstances ON games.gameid = gameinstances.gameid " + "WHERE " + "gameinstances.id = " + gid + ";";
			ResultSet queryResult = db.execQueryWithResult(query);

			if (!queryResult.next())
				return null;
			result = queryResult.getString("name") + " " + queryResult.getString("version");
		} catch (Exception e) {
			logger.Error(e);
		}
		return result;
	}
	
	public List<GameInstance> getGameInstances(long uid, int page) {
		return getGameInstances(uid, page, true);
	}
	
	public List<GameInstance> getGameInstances(long uid) {
		return getGameInstances(uid, -1, false);
	}
	
	public List<GameInstance> getGameInstances(long uid, int page, boolean limit) {
		List<GameInstance> result = new ArrayList<GameInstance>();
		boolean admin = userIsAdmin(uid);
		String whereClause = 
				admin ? " " : 
					((uid >= 0 ? "WHERE usergames.uid = " + uid + " "  :  "WHERE true " ) + 
					" AND (gamecontent.visibility = 2 OR gamecontent.owner = " + uid +
							" OR (gamecontent.visibility = 1 AND " +
									"gamecontent.owner = (SELECT uid FROM usersnfriends WHERE uid = gamecontent.owner AND friendid = " + uid + "))) ");
		
		String query = "SELECT " +
				"gameinstances.id AS gameinstanceid, " +
				"gameinstances.version, " +
				"gameinstances.gameid, " +
				"gameinstances.description, " +
				"gameinstances.image, " +
				"gameinstances.hits, " +
				"games.name AS gamename, " +
				"gamegenres.name AS genrename, " +
				"COUNT(gamecontent.id) AS contentcount " +
			" FROM " +
				"((((gameinstances LEFT JOIN games ON gameinstances.gameid = games.gameid) " +
					" LEFT JOIN gamegenres ON games.genre = gamegenres.id) " +
						"LEFT JOIN gamecontexts ON gamecontexts.gameinstid = gameinstances.id) " +
							"LEFT JOIN gamecontent ON gamecontent.contextid = gamecontexts.id) " +
								(!admin && uid > -1 ? "LEFT JOIN usergames ON usergames.gameinstanceid = gameinstances.id " : "") +
			whereClause +
			" GROUP BY " +
				"gameinstances.id, " +
				"gameinstances.version, " +
				"gameinstances.gameid, " +
				"gameinstances.description, " +
				"gameinstances.image, " +
				"gameinstances.hits, " +
				"games.name, " +
				"gamegenres.name " +
			"ORDER BY " +
				"gamename ASC " +
			(limit ? ("LIMIT " +
			((page - 1) * EntriesPerPage_ContentGames) + ", " + 
            EntriesPerPage_ContentGames) : "") + 
			";";
		try {
			ResultSet rs = db.execQueryWithResult(query);
			while(rs.next()){
				GameInstance g = new GameInstance(rs.getLong("gameinstanceid"), rs.getString("gamename"), rs.getString("version"), 
						rs.getString("genrename"), rs.getString("description"), rs.getInt("contentcount"), rs.getString("image"), rs.getInt("hits"));
				result.add(g);
			}
			
		} catch (SQLException e) {
			logger.Error(e);
		}
		
		return result;
	}
	
	@Override
	public int getGameInstancesPages(long userId) {
		int result = 0;
		try {
			String query = "SELECT " +
						"COUNT(DISTINCT gameinstances.id) AS gamescount " +
					"FROM " +
						"gameinstances LEFT JOIN gamecontexts ON gameinstances.id = gamecontexts.gameinstid " +
							"LEFT JOIN usergames ON usergames.gameinstanceid = gamecontexts.id " +
								"LEFT JOIN gamecontent ON gamecontent.contextid = gamecontexts.id " +
						(userId < 0 ? "WHERE gamecontent.visibility = 2;" : (userIsAdmin(userId) ? ";" : "WHERE usergames.uid = " + userId + ";"));

			ResultSet queryResult = db.execQueryWithResult(query);

			if (!queryResult.next())
				return -1;
			result = (int) Math.ceil(((double) queryResult.getInt("gamescount")) / EntriesPerPage_ContentGames);
		} catch (Exception e) {
			logger.Error(e);
		}
		return Math.max(1, result);
	}

	@Override
	public List<GameContext> getGameContexts(long userId, long gameId, int page) {
		List<GameContext> result = new ArrayList<GameContext>();
		String query = "SELECT " +
				"gameinstances.id AS gameid, " +
				"gameinstances.version, " +
				"games.name AS gamename, " +
				"gamecontexts.id AS contextid, " +
				"gamecontexts.image, " +
				"gamecontexts.name AS contextname, " +
				"COUNT(gamecontent.id) AS contentcount " +
			"FROM " +
				"(((gamecontexts LEFT JOIN gameinstances ON gamecontexts.gameinstid = gameinstances.id)" +
					"LEFT JOIN games ON gameinstances.gameid = games.gameid)" +
						"LEFT JOIN gamecontent ON gamecontent.contextid = gamecontexts.id) " +
			"WHERE " +
				"gameinstances.id = " + gameId +
					(userIsAdmin(userId) ? " " : " AND (gamecontent.visibility = 2 OR gamecontent.owner = " + userId + "" +
							"OR (gamecontent.visibility = 1 AND " +
							"gamecontent.owner = (SELECT uid FROM usersnfriends WHERE uid = gamecontent.owner AND friendid = " + userId + "))) ") +
			" GROUP BY " +
				"gameinstances.id, " +
				"gameinstances.version, " +
				"games.name, " +
				"gamecontexts.id, " +
				"gamecontexts.image, " +
				"gamecontexts.name " +
			"ORDER BY " +
				"contextname ASC " +
			"LIMIT " +
				((page - 1) * EntriesPerPage_ContentContexts) + ", " + 
				EntriesPerPage_ContentContexts + 
			";";

		try {
			ResultSet rs = db.execQueryWithResult(query);
			
			while(rs.next()){
				GameContext context = new GameContext(rs.getLong("contextid"), rs.getString("contextname"), rs.getLong("gameid"), 
						rs.getString("gamename") + " " + rs.getString("version"), rs.getInt("contentcount"), rs.getString("image"));
				result.add(context);
			}
			
		} catch (SQLException e) {
			logger.Error(e);
		}
		return result;
	}
	
	@Override
	public GameContext getGameContextNames(long contextid) {
		GameContext result = null;
		try {
			String query = "SELECT " + 
								"gamecontexts.id AS contextid, " + 
								"gamecontexts.name AS contextname, " + 
								"gamecontexts.image, " + 
								"gameinstances.id AS gameid, " + 
								"gameinstances.version, " + 
								"games.name AS gamename " + 
						"FROM "	+ 
								"(gamecontexts INNER JOIN gameinstances ON gamecontexts.gameinstid = gameinstances.id) " + 
								"INNER JOIN games ON gameinstances.gameid = games.gameid " + 
						"WHERE " + 
								"gamecontexts.id = " + contextid;

			ResultSet queryResult = db.execQueryWithResult(query);
			if (!queryResult.next())
				return null;

			result = new GameContext(queryResult.getLong("contextid"), queryResult.getString("contextname"), queryResult.getLong("gameid"),
					queryResult.getString("gamename") + " " + queryResult.getString("version"), -1, queryResult.getString("image"));
		} catch (Exception e) {
			logger.Error(e);
		}
		return result;
	}

	@Override
	public int getGameContextsPages(long userId, long gameId)  {
		String query = "SELECT COUNT(DISTINCT gamecontexts.id) AS contextcount " + 
							"FROM " +
								"(gamecontexts LEFT JOIN gameinstances ON gamecontexts.gameinstid = gameinstances.id) " +
								"LEFT JOIN gamecontent ON gamecontent.contextid = gamecontexts.id " +
							"WHERE " +
								"gameinstances.id = " + gameId + 
								(userIsAdmin(userId) ? " " : " AND (gamecontent.visibility = 2 OR gamecontent.owner = " + userId + "" +
										"OR (gamecontent.visibility = 1 AND " +
										"gamecontent.owner = (SELECT uid FROM usersnfriends WHERE uid = gamecontent.owner AND friendid = " + userId + "))) ");
		try {
			ResultSet rs = db.execQueryWithResult(query);
			if(!rs.next())
				return -1;
			return (int) Math.ceil(((double) rs.getInt("contextcount")) / EntriesPerPage_ContentContexts);
		} catch (SQLException e) {
			logger.Error(e);
		}
		return -1;
	}
	
	
	@Override
	public boolean registerGameHit(long gid) {
		try {
			String query = "UPDATE gameinstances SET hits = hits + 1 WHERE id = '" + gid + "'";
			return db.execQuery(query) == 1;
		} catch (SQLException e) {
			logger.Error(e);
			return false;
		}
	}

	@Override
	public boolean isUserPlayingGame(long userId, long gameId) {
		try {
			String query = "SELECT * FROM usergames WHERE uid = " + userId + " AND gameinstanceid = " + gameId + ";";
			ResultSet rs = db.execQueryWithResult(query);
			return rs.next();
		} catch (SQLException e) {
			logger.Error(e);
		}
		return false;
	}

	private boolean userIsAdmin(long userId) {
		return HSQLUserDatabaseAccess.getInstance().userIsAdmin(userId);
	}

	@Deprecated
	public List<GameInstance> getGameInstances1(long uid, int page) {
		logger.Error("DEPRECATED METHOD USED (getGameInstances1)");
	
		List<GameInstance> result = new ArrayList<GameInstance>();
		try {
			
			// Seems there is a bug in hsql that prevents the direct use of
			// DISTINCT here. Workaround: Nested select.
					String query = "SELECT DISTINCT * FROM (" + 
	                        "SELECT " + 
	                                        "gameinstances.id, " + 
	                                        "gameinstances.version, " + 
	                                        "gameinstances.description, " + 
	                                        "gameinstances.image, " + 
	                                        "gameinstances.hits, " + 
	                                        "games.name, " + 
	                                        "gamegenres.name AS genrename, " + 
	                                        "COUNT(DISTINCT gamecontent.id) AS contents " + 
	                        "FROM " + 
	                                        "(((((gameinstances INNER JOIN games ON gameinstances.gameid = games.gameid) " + 
	                                                "INNER JOIN gamegenres ON games.genre = gamegenres.id) " + 
	                                                        "INNER JOIN gamecontexts ON gameinstances.id = gamecontexts.gameinstid) " + 
	                                                                "INNER JOIN userprogress ON gamecontexts.id = userprogress.scnid) " + 
	                                                                        "LEFT JOIN gamecontent ON gamecontexts.id = gamecontent.contextid) " + 
	                                        "LEFT JOIN (" + 
	                                                "SELECT " + 
	                                                        "uid, " + 
	                                                        "friendid " + 
	                                                "FROM " + 
	                                                        "usersnfriends " + 
	                                                "WHERE " + 
	                                                        "friendid = " + uid     + ") " +
	                                                        "AS fim ON gamecontent.owner = fim.uid " + 
	                                                                "WHERE " + 
	                                                                        "userprogress.uid = " + uid + " " + 
	                                                                                (!userIsAdmin(uid) ? 
	                                                                                "AND (gamecontent.visibility = 2 OR (gamecontent.visibility = 1 AND fim.friendid IS NOT NULL))" : "") + 
	                                                                "GROUP BY "     + 
	                                                                        "gameinstances.id, " + 
	                                                                        "gameinstances.version, " + 
	                                                                        "gameinstances.description, " + 
	                                                                        "gameinstances.image, " + 
	                                                                        "gameinstances.hits, " + 
	                                                                        "games.name, " + 
	                                                                        "gamegenres.name) "     + 
	                                                                "ORDER BY "     + 
	                                                                        "name ASC "     + 
	                                                                "LIMIT " + 
	                                                                        ((page - 1) * EntriesPerPage_ContentGames) + ", " + 
	                                                                        EntriesPerPage_ContentGames + 
	                                                                        ";";
	
			ResultSet queryResult = db.execQueryWithResult(query);
			
			while (queryResult.next()) {
				result.add(new GameInstance(queryResult.getLong("id"), queryResult.getString("name"), queryResult.getString("version"), queryResult
						.getString("genrename"), queryResult.getString("description"), queryResult.getInt("contents"), queryResult.getString("image"),
						queryResult.getInt("hits")));
			}
		} catch (Exception e) {
			logger.Error(e);
		}
		return result;
	}

	@Deprecated
	public List<GameContext> getGameContexts1(long userId, long gameId, int page) {
		logger.Error("DEPRECATED METHOD USED (getGameContexts1)");
	
		List<GameContext> result = new ArrayList<GameContext>();
		try {
			// Seems there is a bug in hsql that prevents the direct use of
			// DISTINCT here. Workaround: Nested select.
			String query = 
					"SELECT DISTINCT * FROM (" + 
							"SELECT " + 
								"gameinstances.id AS gameid, " + 
								"gameinstances.version, " + 
								"games.name AS gamename, " + 
								"gamecontexts.id AS contextid, " + 
								"gamecontexts.name AS contextname, " + 
								"gamecontexts.image, " + 
								"COUNT(DISTINCT gamecontent.id) AS contents " + 
							"FROM " + 
								"((((gameinstances INNER JOIN games ON gameinstances.gameid = games.gameid) " + 
									"INNER JOIN gamecontexts ON gameinstances.id = gamecontexts.gameinstid) " + 
										"INNER JOIN userprogress ON gamecontexts.id = userprogress.scnid) " + 
											"LEFT JOIN gamecontent ON gamecontexts.id = gamecontent.contextid) " + 
												"LEFT JOIN (" + 
														"SELECT " + 
															"uid, " + 
															"friendid " + 
														"FROM "	+ 
															"usersnfriends " + 
														"WHERE " + 
															"friendid = " + userId + ") AS fim ON gamecontent.owner = fim.uid " + 
												"WHERE " + 
													"userprogress.uid = " + userId + " " + 
													(!userIsAdmin(userId) ? 
														"AND (gamecontent.visibility = 2 OR (gamecontent.visibility = 1 AND fim.friendid IS NOT NULL))" : "") + 
												"GROUP BY " + 
														"gameinstances.id, " + 
														"gameinstances.version, " + 
														"gamecontexts.id, " + 
														"gamecontexts.name, " + 
														"gamecontexts.image, " + 
														"games.name) " + 
												"ORDER BY " + 
														"contextname ASC " + 
												"LIMIT " + 
														((page - 1) * EntriesPerPage_ContentContexts) + ", " + EntriesPerPage_ContentContexts + ";";
			ResultSet queryResult = db.execQueryWithResult(query);
	
			while (queryResult.next()) {
				result.add(new GameContext(queryResult.getLong("contextid"), queryResult.getString("contextname"), queryResult.getLong("gameid"), queryResult
						.getString("gamename") + " " + queryResult.getString("version"), queryResult.getInt("contents"), queryResult.getString("image")));
	
			}
		} catch (Exception e) {
			logger.Error(e);
		}
		return result;
	}

	
	@Deprecated
	public int getGameContextsPages1(long userId, long gameId) {
		logger.Error("DEPRECATED METHOD USED (getGameContextsPages1)");
		int result = 0;
		try {
			String query = "SELECT " + 
								"COUNT(DISTINCT gamecontexts.id) AS contextcount " + 
							"FROM "	+ 
								"(gameinstances INNER JOIN gamecontexts ON gameinstances.id = gamecontexts.gameinstid) " + 
									"INNER JOIN userprogress ON gamecontexts.id = userprogress.scnid " + 
							"WHERE " + 
								"userprogress.uid = " + userId + " " + 
								"AND gameinstances.id = " + gameId + ";";
	
			System.out.println(query);
			ResultSet queryResult = db.execQueryWithResult(query);
	
			if (!queryResult.next())
				return -1;
			result = (int) Math.ceil(((double) queryResult.getInt("contextcount")) / EntriesPerPage_ContentContexts);
		} catch (Exception e) {
			logger.Error(e);
		}
		return result;
	}

	@Override
	public String[][] getAllGames() {
		/*
		 * 1 - for each game
		 * 	1.1 - name
		 * 	1.2 - identifier
		 * 	1.3	- image
		 * 	1.4	- description
		 * 	1.5 - genre
		 */
		Set<Long> alreadyStored = new HashSet<Long>();
		String[][] result = null;
		String query = "SELECT " +
						"games.gameid, " +
						"name , " +
						"REPLACE(LOWER(name), ' ', '') AS ident, " +
						"description, " +
						"image, " +
						"gamegenres.name AS genre, " +
						"gameinstances.id AS instanceid, " +
						"(SELECT COUNT(*) FROM games) AS gamecount " +
					"FROM games " +
						"LEFT JOIN gameinstances ON games.gameid = gameinstances.gameid " +
							"LEFT JOIN gamegenres ON games.genre = gamegenres.id " +
					"ORDER BY gameinstances.id DESC";
		try {
			ResultSet rs = db.execQueryWithResult(query);
			int current = 0;
			while(rs.next()) {
				if(current == 0){
					int size = rs.getInt("gamecount");
					result = new String[size][5];
				}
				
				//check if this game is already stored
				long gameid = rs.getLong("gameid");
				if(alreadyStored.contains(gameid)) continue;
				alreadyStored.add(gameid);
				
				result[current][0] = rs.getString("name");
				result[current][1] = rs.getString("ident");
				result[current][2] = rs.getString("image");
				result[current][3] = rs.getString("description");
				result[current][4] = rs.getString("genre");
				current++;
			}
			return result;
		} catch (SQLException e) {
			logger.Error(e);
		}
		return null;		
	}

	@Override
	public boolean isGameIdentValid(String gameident) {
		try {
			PreparedStatement statement = db.getPreparedStatement("SELECT * FROM games " +
					"WHERE REPLACE(LOWER(name), ' ') = ?;");
			statement.setString(1, gameident);
			ResultSet rs = statement.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			logger.Error(e);
			return false;
		}
	}
}
