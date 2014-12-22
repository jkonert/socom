package de.tud.kom.socom.web.server.database.user;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.tud.kom.socom.web.client.sharedmodels.GameContext;
import de.tud.kom.socom.web.client.sharedmodels.GlobalConfig;
import de.tud.kom.socom.web.client.sharedmodels.SimpleUser;
import de.tud.kom.socom.web.client.sharedmodels.SocialNetworkUser;
import de.tud.kom.socom.web.client.sharedmodels.User;
import de.tud.kom.socom.web.server.database.HSQLAccess;
import de.tud.kom.socom.web.server.util.EasyEncrypter;
import de.tud.kom.socom.web.server.util.Logger;
import de.tud.kom.socom.web.server.util.LoggerFactory;

public class HSQLUserDatabaseAccess implements UserDatabaseAccess, GlobalConfig {

	private static UserDatabaseAccess instance = new HSQLUserDatabaseAccess();
	private Logger logger = LoggerFactory.getLogger();
	private static HSQLAccess db;

	private HSQLUserDatabaseAccess() {
		db = HSQLAccess.getInstance();
	}

	public static UserDatabaseAccess getInstance() {
		return instance;
	}

	@Override
	public User getUser(long userId, long userToFetch, boolean includeDeleted) {
		User result = null;
		List<SocialNetworkUser> snus = new ArrayList<SocialNetworkUser>();
		try {
			String query = "SELECT usersnaccounts.username, socialnetworks.name, socialnetworks.urlprofile FROM "
					+ "usersnaccounts INNER JOIN socialnetworks ON usersnaccounts.snid = socialnetworks.id WHERE usersnaccounts.uid = " + userToFetch
					+ " ORDER BY name ASC;";
			ResultSet queryResult = db.execQueryWithResult(query);

			while (queryResult.next()) {
				snus.add(new SocialNetworkUser(queryResult.getString("username"), queryResult.getString("name"), queryResult.getString("urlprofile")));
			}

			query = "SELECT " +
						"users.deleted, " +
						"users.uid, " +
						"users.name, " +
						"users.currentGameInst, " +
						"users.visibility, " + 
						"users.currentGameInst IS NULL AS isNotPlaying, " +
						"currgameinst.name AS currentGameName, " + 
						"currgameinst.version AS currentGameVersion, " +
						"userstates.name AS currentStateName, "	+ 
						"visiblegames.uid IS NOT NULL AS gamevisible, " +
						"fim.friendid IS NOT NULL AS isfriend, " + 
						"COUNT(DISTINCT gamecontent.id) AS contentcount, " +
						"COUNT(DISTINCT contentcomments.id) AS commentcount, " + 
						"COUNT(DISTINCT contentratings.contentid) AS ratingscount " + 
					"FROM " + 
						"(((((users INNER JOIN userstates ON users.currentState = userstates.id) " + 
						"LEFT JOIN (" + 
							"SELECT " + 
								"gameinstances.id, " + 
								"gameinstances.version, " + 
								"games.name " + 
							"FROM "	+ 
								"gameinstances INNER JOIN games ON gameinstances.gameid = games.gameid) " +
							"AS currgameinst ON users.currentGameInst = currgameinst.id) " + 
						"LEFT JOIN (" + 
							"SELECT " + 
								"userprogress.uid, " + 
								"gameinstances.id " + 
							"FROM "	+ 
								"(gameinstances INNER JOIN gamecontexts ON gameinstances.id = gamecontexts.gameinstid) " + 
						"INNER JOIN " +
							"userprogress ON gamecontexts.id = userprogress.scnid " + 
							"WHERE " + 
								"userprogress.uid = " + userId + ") AS visiblegames " + 
							"ON users.currentGameInst = visiblegames.id " + 
						"LEFT JOIN (" + 
							"SELECT " + 
								"uid, " + 
								"friendid "	+ 
							"FROM "	+ 
								"usersnfriends " + 
							"WHERE " + 
								"uid = " + userToFetch + 
							" AND " +
								"friendid = " + userId + 
							") AS fim ON users.uid = fim.uid) "	+ 
						"LEFT JOIN gamecontent ON gamecontent.owner = users.uid) " + 
						"LEFT JOIN contentcomments ON contentcomments.uid = users.uid) " + 
						"LEFT JOIN contentratings ON contentratings.uid = users.uid " + 
						"WHERE " + 
							"users.uid = " + userToFetch + 
							(includeDeleted ? " " : " AND users.deleted = 0 ") + 
						"GROUP BY "	+ 
							"users.uid, " + 
							"users.deleted, " + 
							"users.name, " + 
							"users.currentGameInst, " + 
							"users.visibility, " + 
							"currgameinst.name, " + 
							"currgameinst.version, " + 
							"userstates.name, " + 
							"visiblegames.uid, " + 
							"fim.friendid";

			queryResult = db.execQueryWithResult(query);

			if (!queryResult.next())
				return null;

			if (userId == userToFetch || queryResult.getInt("visibility") == 2 || (queryResult.getInt("visibility") == 1 && isFriendOf(userToFetch, userId)) 
					|| userIsAdmin(userId))
				result = new User(queryResult.getLong("uid"), queryResult.getString("name"), true, queryResult.getInt("visibility"),
						(!queryResult.getBoolean("isNotPlaying") ? (queryResult.getBoolean("gamevisible") ? queryResult.getLong("currentGameInst") : -2) : -1),
						queryResult.getString("currentGameName") + " " + queryResult.getString("currentGameVersion"),
						queryResult.getString("currentStateName"), snus, queryResult.getInt("contentCount"), queryResult.getInt("commentCount"),
						queryResult.getInt("ratingsCount"), queryResult.getInt("deleted"));
			else
				result = new User(queryResult.getLong("uid"), queryResult.getString("name"), false, -1, -1, null, null, null, -1, -1, -1,
						queryResult.getInt("deleted"));
		} catch (Exception e) {
			logger.Error(e);
		}
		return result;
	}

	@Override
	public String getUserName(long uid) {
		String result = "";
		try {
			String query = "SELECT name FROM users WHERE uid = " + uid + ";";
			ResultSet queryResult = db.execQueryWithResult(query);

			if (!queryResult.next())
				return null;
			result = queryResult.getString("name");
		} catch (Exception e) {
			logger.Error(e);
		}
		return result;
	}
	
	@Override
	public long getUserId(String userName) {
		try{
			String query = "SELECT uid FROM users WHERE name = '" + userName + "';";
			ResultSet rs = db.execQueryWithResult(query);
			if(rs.next()){
				return rs.getLong(1);
			} else return -1;
		}catch(SQLException e){
			logger.Error(e);
			return -1;
		}
	}
	
	@Override
	public boolean userIsAdmin(long userId) {
		boolean result = false;
		try {
			String query = "SELECT isadmin FROM users WHERE uid = " + userId + ";";
			ResultSet queryResult = db.execQueryWithResult(query);
			
			if (!queryResult.next())
				return false;

			result = queryResult.getBoolean("isadmin");
		} catch (Exception e) {
			logger.Error(e);
			return false;
		}
		return result;
	}

	@Override
	public boolean setProfileVisibility(long userId, int selectedIndex) {
		try {
			String query = "UPDATE users SET visibility = " + selectedIndex + " WHERE uid = " + userId + ";";
			db.execQuery(query);
		} catch (Exception e) {
			logger.Error(e);
			return false;
		}
		return true;
	}

	@Override
	public List<GameContext> getUserHistory(long userId, long gameInstId) {
		List<GameContext> result = new ArrayList<GameContext>();
		try {
			String query = "SELECT gamecontexts.id, gamecontexts.name, gamecontexts.image FROM "
					+ "gamecontexts INNER JOIN userprogress ON gamecontexts.id = userprogress.scnid " + "WHERE gamecontexts.gameinstid = '" + gameInstId + "' "
					+ "AND userprogress.uid = '" + userId + "'";
			ResultSet queryResult = db.execQueryWithResult(query);

			while (queryResult.next()) {
				result.add(new GameContext(queryResult.getLong("id"), queryResult.getString("name"), -1, "", -1, queryResult.getString("image")));
			}
		} catch (Exception e) {
			logger.Error(e);
			e.printStackTrace();
		}

		return result;
	}

	@Override
	public String[] getDeletedStates() {
		List<String> result = new LinkedList<String>();
		try {
			String query = "SELECT * FROM deletedflags";
			ResultSet rs;
			rs = db.execQueryWithResult(query);
			while (rs.next()) {
				result.add(rs.getString("reason"));
			}
		} catch (SQLException e) {
			logger.Error(e);
			e.printStackTrace();
		}
		return result.toArray(new String[result.size()]);
	}

	@Override
	public SimpleUser getSimpleUserByName(String name) {
		try {
			String query = "SELECT uid, isadmin, deleted FROM users WHERE name = '" + name + "';";
			ResultSet rs = db.execQueryWithResult(query);
			if (!rs.next())
				return null;

			return new SimpleUser(name, rs.getLong("uid"), rs.getBoolean("isadmin"), rs.getInt("deleted"));
		} catch (SQLException e) {
			logger.Error(e);
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<SimpleUser> getSimpleUsersByName(String startingWith) {
		List<SimpleUser> result = new LinkedList<SimpleUser>();
		try {
			String query = "SELECT uid, name, isadmin, deleted FROM users WHERE UPPER(name) LIKE UPPER('" + startingWith + "%') ORDER BY name ASC;";
			ResultSet rs = db.execQueryWithResult(query);
			while (rs.next())
				result.add(new SimpleUser(rs.getString("name"), rs.getLong("uid"), rs.getBoolean("isadmin"), rs.getInt("deleted")));

		} catch (SQLException e) {
			logger.Error(e);
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public boolean changeUserDeletionFlag(long uid, int flag) {
		try {
			String query = "SELECT * FROM deletedflags WHERE id = " + flag + ";";
			ResultSet rs = db.execQueryWithResult(query);

			if (!rs.next())
				return false;

			query = "UPDATE users SET deleted = " + flag + " WHERE uid = " + uid + ";";
			return db.execQuery(query) > 0;

		} catch (SQLException e) {
			logger.Error(e);
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public boolean isFriendOf(long uid, long friendid) {
		try {
			String query = "SELECT uid FROM usersnfriends WHERE uid = " + uid + " AND friendid = " + friendid + ";";
			ResultSet rs = db.execQueryWithResult(query);
			return rs.next();
		} catch (SQLException e) {
			logger.Error(e);
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public String getUsersSecretEncrypted(long uid) {
		try {
			PreparedStatement statement = db.getPreparedStatement("SELECT password FROM users WHERE uid = ?;");
			statement.setLong(1, uid);
			ResultSet rs = statement.executeQuery();
			if (!rs.next())
				return null;

			String secret = rs.getString("password");
			byte[] secretDecrypted = EasyEncrypter.getInstance().encryptString(secret);
			secret = "";
			for (byte be : secretDecrypted) {
				String hex = String.format("%h", be);
				secret += hex.substring(hex.length() > 1 ? hex.length() - 2 : 0) + "-";
			}

			return secret;
		} catch (SQLException e) {
			logger.Error(e);
			e.printStackTrace();
			return null;
		}
	}	
}