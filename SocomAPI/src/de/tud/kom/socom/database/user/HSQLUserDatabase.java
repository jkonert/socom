package de.tud.kom.socom.database.user;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.tud.kom.socom.GlobalConfig;
import de.tud.kom.socom.components.game.GameInstance;
import de.tud.kom.socom.database.HSQLDatabase;
import de.tud.kom.socom.util.EasyEncrypter;
import de.tud.kom.socom.util.Logger;
import de.tud.kom.socom.util.LoggerFactory;
import de.tud.kom.socom.util.datatypes.Profile;
import de.tud.kom.socom.util.datatypes.SocialNetworkUser;
import de.tud.kom.socom.util.datatypes.User;
import de.tud.kom.socom.util.datatypes.UserMetadata;
import de.tud.kom.socom.util.exceptions.ContentAlreadyExistsException;
import de.tud.kom.socom.util.exceptions.ContentDeletedException;
import de.tud.kom.socom.util.exceptions.ContentNotFoundException;
import de.tud.kom.socom.util.exceptions.SocomException;
import de.tud.kom.socom.util.exceptions.IllegalAccessException;
import de.tud.kom.socom.util.exceptions.SocialNetworkUnsupportedException;
import de.tud.kom.socom.util.exceptions.UIDOrSecretNotValidException;
import de.tud.kom.socom.util.exceptions.UserAlreadyExistsException;
import de.tud.kom.socom.util.exceptions.UserNotFoundException;

/**
 * 
 * @author rhaban
 * 
 */
public class HSQLUserDatabase extends HSQLDatabase implements UserDatabase, GlobalConfig {

	private static HSQLUserDatabase instance = new HSQLUserDatabase();
	private static Logger logger;

	private HSQLUserDatabase() {
		super();
		logger = LoggerFactory.getLogger();
	}

	public static HSQLUserDatabase getInstance() {
		return instance;
	}

	@Override
	public boolean validateUser(long uid, String password) throws SQLException, UIDOrSecretNotValidException {
		PreparedStatement statement = db.getPreparedStatement("SELECT deleted FROM users WHERE uid = ? AND password = ?;");
		statement.setLong(1, uid);
		statement.setString(2, password);
		ResultSet rs = statement.executeQuery();
		if(!rs.next())
			throw new UIDOrSecretNotValidException();
		return rs.getInt("deleted") == 0; //true if not deleted
	}
	
	@Override
	public long[] validateUser(long gameinstanceid, String username, String password) throws SQLException, UIDOrSecretNotValidException,
			ContentDeletedException {
		PreparedStatement statement = db.getPreparedStatement("SELECT uid, deleted FROM users WHERE name = ? AND password = ?");
		statement.setString(1, username);
		statement.setString(2, password);
		ResultSet result = statement.executeQuery();
		if (!result.next())
			throw new UIDOrSecretNotValidException();

		if (result.getInt("deleted") > 0)
			throw new ContentDeletedException();

		long uid = result.getLong("uid");

		statement = db.getPreparedStatement("UPDATE " + 
							"users " + 
						"SET " + 
							"currentstate=?, " + 
							"currentgameinst = ? " + 
						"WHERE " +
							"uid = '" + uid + "';");
		statement.setInt(1, USERSTATE_PLAYING);
		statement.setLong(2, gameinstanceid);
		statement.executeUpdate();

		logger.Info("Login user #" + uid + ": " + username + " (by gameinstance #" + gameinstanceid + ")");
		return new long[] { uid, gameinstanceid };
	}

	/**
	 * Creates a new player, sets his current game and returns his id.
	 * 
	 * @return id of the new player, otherwise SQLException
	 * @throws UserAlreadyExistsException
	 */
	@Override
	public long[] createUser(long gameInstanceId, String username, String password, int visibility) throws SQLException, UserAlreadyExistsException {
		PreparedStatement statement = db.getPreparedStatement("SELECT uid FROM users WHERE name = ?");
		statement.setString(1, username);
		ResultSet exist = statement.executeQuery();
		if (exist.next())
			throw new UserAlreadyExistsException(username);

		statement = db.getPreparedStatement("INSERT INTO " +
								"users (" + 
									"name, " + 
									"password, " + 
									"currentstate, " + 
									"currentgameinst, " + 
									"visibility, " + 
									"isadmin) "	+ 
							"VALUES (?, ?, ?, ?, ?, false);");
		statement.setString(1,username);
		statement.setString(2, password);
		statement.setInt(3, USERSTATE_PLAYING);
		statement.setLong(4, gameInstanceId);
		statement.setInt(5, visibility);
		
		statement.executeUpdate();
		statement = db.getPreparedStatement("SELECT IDENTITY() FROM users WHERE name = ?;");
		statement.setString(1, username);
		ResultSet result = statement.executeQuery();
		if (!result.next())
			throw new SQLException("Not created");

		logger.Info("New user #" + result.getLong(1) + ": " + username + " (by gameinstance #" + gameInstanceId + ")");
		return new long[] { result.getLong(1), gameInstanceId };
	}

	@Override
	public boolean deleteUser(long uid, String password) throws SQLException, SocomException {
		PreparedStatement statement = db.getPreparedStatement("SELECT * FROM users WHERE uid = ? AND password = ?;");
		statement.setLong(1,uid);
		statement.setString(2, password);
		ResultSet rs = statement.executeQuery();

		if (!rs.next())
			throw new IllegalAccessException();

		statement = db.getPreparedStatement("UPDATE users SET deleted = 1 WHERE uid = ?;");
		statement.setLong(1, uid);
		int rows = statement.executeUpdate();
		return rows == 1;
	}

	private User getUser(long uid, long userToFetch) throws ContentDeletedException, IllegalAccessException {
		User result = null;
		List<SocialNetworkUser> snus = new ArrayList<SocialNetworkUser>();
		try {
			PreparedStatement statement = db.getPreparedStatement("SELECT " + 
								"usersnaccounts.username, " + 
								"games.name AS gamename, " +
								"socialnetworks.name AS snname, " + 
								"socialnetworks.urlprofile "+ 
							"FROM " + 
								"usersnaccounts INNER JOIN socialnetworks ON usersnaccounts.snid = socialnetworks.id " +
									"INNER JOIN games ON games.gameid = usersnaccounts.gameid " + 
							"WHERE " + 
								"usersnaccounts.uid = ? " + 
							"ORDER BY " + 
								"name ASC;");
			statement.setLong(1, userToFetch);
			ResultSet queryResult = statement.executeQuery();

			while (queryResult.next()) {
				snus.add(new SocialNetworkUser(queryResult.getString("username"), queryResult.getString("gamename"), 
						queryResult.getString("snname"), queryResult.getString("urlprofile")));
			}

			statement = db.getPreparedStatement("SELECT " + 
							"users.uid, " + 
							"users.name, " + 
							"users.deleted, " + 
							"users.currentGameInst, " + 
							"users.visibility, " + 
							"users.currentGameInst IS NULL AS isNotPlaying, " + 
							"currgameinst.id AS currentGameId, " + 
							"currgameinst.name AS currentGameName, " + 
							"currgameinst.version AS currentGameVersion, " + 
							"userstates.name AS currentStateName, "	+ 
							"visiblegames.uid IS NOT NULL AS gamevisible, " + 
							"fim.friendid IS NOT NULL AS isfriend, " + 
							"COUNT(DISTINCT gamecontent.id) AS contentcount, " + 
							"COUNT(DISTINCT contentcomments.id) AS commentcount, " + 
							"COUNT(DISTINCT contentratings.contentid) AS ratingscount " + 
					"FROM "	+ 
							"(((((users INNER JOIN userstates ON users.currentState = userstates.id) " + 
									"LEFT JOIN (" + 
										"SELECT " + 
											"gameinstances.id, " + 
											"gameinstances.version, " + 
											"games.name " + 
										"FROM " + 
											"gameinstances " + 
									"INNER JOIN games ON gameinstances.gameid = games.gameid) AS currgameinst " + 
											"ON users.currentGameInst = currgameinst.id) " + 
									"LEFT JOIN (" + 
										"SELECT " + 
											"userprogress.uid, " + 
											"gameinstances.id " + 
										"FROM "	+ 
											"(gameinstances INNER JOIN gamecontexts ON gameinstances.id = gamecontexts.gameinstid) " + 
										"INNER JOIN userprogress ON gamecontexts.id = userprogress.scnid " + 
											"WHERE " + 
												"userprogress.uid = ?) " +
										"AS visiblegames " + "ON users.currentGameInst = visiblegames.id " + 
										"LEFT JOIN (" + 
											"SELECT " + 
											"uid, " + 
											"friendid "	+
										"FROM "	+
											"usersnfriends "+ 
										"WHERE " + 
											"uid = ? " + 
											"AND friendid = ?) AS fim ON users.uid = fim.uid) "+ 
									"LEFT JOIN gamecontent ON gamecontent.owner = users.uid) " + 
									"LEFT JOIN contentcomments ON contentcomments.uid = users.uid) " + 
									"LEFT JOIN contentratings ON contentratings.uid = users.uid " + 
										"WHERE " + 
											"users.uid = ? " + 
										"GROUP BY "	+ 
											"users.uid, " + 
											"users.deleted, " + 
											"users.name, " + 
											"users.currentGameInst, " + 
											"users.visibility, " + 
											"currgameinst.id, " + 
											"currgameinst.name, " + 
											"currgameinst.version, " + 
											"userstates.name, " + 
											"visiblegames.uid, " + 
											"fim.friendid");
			statement.setLong(1, uid);
			statement.setLong(2, userToFetch);
			statement.setLong(3, uid);
			statement.setLong(4, userToFetch);
			queryResult = statement.executeQuery();

			if (!queryResult.next())
				return null;

			if (queryResult.getInt("deleted") > 0)
				throw new ContentDeletedException();

			if (uid == userToFetch || queryResult.getInt("visibility") == 2 || (queryResult.getInt("visibility") == 1 && isFriendOf(userToFetch, uid)) 
					|| userIsAdmin(uid)) {
				result = new User(queryResult.getLong("uid"), queryResult.getString("name"), true, queryResult.getInt("visibility"),
				/*
				 * (!queryResult.getBoolean("isNotPlaying") ?
				 * (queryResult.getBoolean("gamevisible") ?
				 * queryResult.getLong("currentGameInst") : -2) : -1),
				 */
				queryResult.getLong("currentGameId"), queryResult.getString("currentGameName") + " " + queryResult.getString("currentGameVersion"),
						queryResult.getString("currentStateName"), snus, queryResult.getInt("contentCount"), queryResult.getInt("commentCount"),
						queryResult.getInt("ratingsCount"));
			} else
				throw new IllegalAccessException();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * returns the player with given id
	 * 
	 * @throws UserNotFoundException
	 * @throws ContentDeletedException
	 * @throws IllegalAccessException
	 */
	@Override
	public User fetchUser(long uidSelf, long uid) throws SQLException, UserNotFoundException, ContentDeletedException, IllegalAccessException {
		User result = getUser(uidSelf, uid);
		if (result == null)
			throw new UserNotFoundException(uid);

		return result;
	}

	@Override
	public String getUsersSecretEncrypted(long uid) throws SQLException, UserNotFoundException {
		PreparedStatement statement = db.getPreparedStatement("SELECT password FROM users WHERE uid = ?;");
		statement.setLong(1, uid);
		ResultSet rs = statement.executeQuery();
		if (!rs.next())
			throw new UserNotFoundException(uid);

		String secret = rs.getString("password");
		byte[] secretDecrypted = EasyEncrypter.getInstance().encryptString(secret);
		secret = "";
		for (byte be : secretDecrypted) {
			String hex = String.format("%h", be);
			secret += hex.substring(hex.length() > 1 ? hex.length() - 2 : 0) + "-";
		}

		return secret;
	}

	@Override
	public void becomeAdmin(long uid, String password) throws SQLException, UIDOrSecretNotValidException {
		PreparedStatement statement = db.getPreparedStatement("UPDATE users SET isadmin = true WHERE uid = ? AND password = ?;");
		statement.setLong(1, uid);
		statement.setString(2, password);
		int affectedRows = statement.executeUpdate();
		if (affectedRows < 1)
			throw new UIDOrSecretNotValidException();
	}

	@Override
	public void updateUsersGame(long uid, long gameInstanceId) throws SQLException {
		PreparedStatement statement = db.getPreparedStatement("UPDATE usergames SET timestamp = NOW WHERE uid = ? AND gameinstanceid = ?;");
		statement.setLong(1, uid);
		statement.setLong(2, gameInstanceId);
		if (statement.executeUpdate() == 0) { // no entry
			statement = db.getPreparedStatement("INSERT INTO usergames VALUES(?, ?, NOW);");
			statement.setLong(1, uid);
			statement.setLong(2, gameInstanceId);
			statement.executeUpdate();
		}
	}

	@Override
	public List<GameInstance> getUsersGames(long uid) throws SQLException {
		List<GameInstance> result = new LinkedList<GameInstance>();
		PreparedStatement statement = db.getPreparedStatement("SELECT " + 
						"usergames.timestamp, gameinstances.id, gameinstances.version, gameinstances.description, games.name " + 
				"FROM "	+ 
						"(usergames INNER JOIN gameinstances ON usergames.gameinstanceid = gameinstances.id) " + 
							"INNER JOIN games ON gameinstances.gameid = games.gameid " + 
				"WHERE " + 
						"usergames.uid = ?;");
		statement.setLong(1, uid);
		ResultSet rs = statement.executeQuery();
		while (rs.next()) {
			GameInstance instance = new GameInstance(rs.getString("name"), rs.getString("version"), rs.getString("description"));
			instance.setLastUsed(rs.getTimestamp("timestamp"));
			result.add(instance);
		}
		return result;
	}

	/**
	 * compute if another social network user is also using socom
	 * 
	 * @return the players id
	 * @throws UserNotFoundException
	 */
	@Override
	public int getIDOf(String networkName, String snuid) throws SQLException, UserNotFoundException, SocialNetworkUnsupportedException {
		long snId = getSocialNetworkId(networkName);

		PreparedStatement statement = db.getPreparedStatement("SELECT uid FROM usersnaccounts WHERE username = ? AND snid = ?;");
		statement.setString(1, snuid);
		statement.setLong(2, snId);
		ResultSet result = statement.executeQuery();

		if (!result.next())
			throw new UserNotFoundException();

		return result.getInt("uid");
	}

	@Override
	public void addNetworkIdentification(long uid, String networkName, long gameinstid, String snuid, String networkToken) throws SQLException,
			SocialNetworkUnsupportedException {
		long snId = getSocialNetworkId(networkName);

		PreparedStatement statement = db.getPreparedStatement("SELECT uid, gameid FROM usersnaccounts WHERE uid = ? AND snid = ? " +
				"AND gameid = (SELECT gameid FROM gameinstances WHERE id = ?);");
		statement.setLong(1, uid);
		statement.setLong(2, snId);
		statement.setLong(3, gameinstid);
		ResultSet queryResult = statement.executeQuery();

		if (!queryResult.next()) {
			// Insert new entry
			statement = db.getPreparedStatement("INSERT INTO usersnaccounts (UID, SNID, USERNAME, TOKEN, GAMEID) " +
					"VALUES (?, ?, ?, ?, (SELECT gameid FROM gameinstances WHERE id = ?));");
			statement.setLong(1, uid);
			statement.setLong(2, snId);
			statement.setString(3, snuid);
			statement.setString(4, networkToken);
			statement.setLong(5, gameinstid);
			statement.executeUpdate();
		} else {
			// Update old entry
			long gameid = queryResult.getLong("gameid");
			statement = db.getPreparedStatement("UPDATE usersnaccounts SET username = ?, token = ? WHERE uid = ? " +
					"AND snid = ? AND gameid = ?;");
			statement.setString(1, snuid);
			statement.setString(2, networkToken);
			statement.setLong(3, uid);
			statement.setLong(4, snId);
			statement.setLong(5, gameid);
			statement.executeUpdate();
		}
	}

	@Override
	public void removeNetworkToken(long uid, long gameinstid, String networkName) throws SQLException, SocialNetworkUnsupportedException {
		long snId = getSocialNetworkId(networkName);

		PreparedStatement statement = db.getPreparedStatement("UPDATE usersnaccounts SET token = NULL WHERE uid = ? AND snid = ? " +
				"AND gameid = (SELECT gameid FROM gameinstances WHERE id = ?);");
		statement.setLong(1, uid);
		statement.setLong(2, snId);
		statement.setLong(3, gameinstid);
		statement.executeUpdate();
	}

	private long getSocialNetworkId(String networkName) throws SQLException, SocialNetworkUnsupportedException {
		PreparedStatement statement = db.getPreparedStatement("SELECT id FROM socialnetworks WHERE name = ?;");
		statement.setString(1, networkName);
		ResultSet result = statement.executeQuery();

		if (!result.next())
			throw new SocialNetworkUnsupportedException(networkName);

		return result.getLong("id");
	}
	
	@Override
	public String getSNToken(long uid, long gameinstid, String networkname) throws SQLException {
		PreparedStatement statement = db.getPreparedStatement("SELECT token FROM usersnaccounts " +
				"WHERE uid = ? AND gameid = (SELECT gameid FROM gameinstances WHERE id = ?) " +
					"AND snid = (SELECT id FROM socialnetworks WHERE UPPER(name) = UPPER(?))");
		statement.setLong(1, uid);
		statement.setLong(2, gameinstid);
		statement.setString(3, networkname);
		ResultSet rs = statement.executeQuery();
		if(!rs.next()) return null;
		
		return rs.getString(1);
	}

	@Override
	public void setUserOnline(long uid) throws SQLException {
		PreparedStatement statement = db.getPreparedStatement("UPDATE users SET currentstate = ? WHERE uid = ? AND currentstate = ?;");
		statement.setInt(1, USERSTATE_PLAYING);
		statement.setLong(2, uid);
		statement.setInt(3, USERSTATE_OFFLINE);
		statement.executeUpdate();
	}

	@Override
	public void setUserOffline(long uid) throws SQLException {
		PreparedStatement statement = db.getPreparedStatement("UPDATE users SET currentstate=? WHERE uid = ?;");
		statement.setInt(1, USERSTATE_OFFLINE);
		statement.setLong(2, uid);
		statement.executeUpdate();

		logger.Info("User #" + uid + " offline");
	}

	public boolean userIsAdmin(long userId) {
		boolean result = false;
		try {
			PreparedStatement statement = db.getPreparedStatement("SELECT isadmin FROM users WHERE uid = ?;");
			statement.setLong(1, userId);
			ResultSet queryResult = statement.executeQuery();

			if (!queryResult.next())
				return false;

			result = queryResult.getBoolean("isadmin");
		} catch (Exception e) {
			return false;
		}
		return result;
	}

	public void setAllUsersOffline() throws SQLException {
		PreparedStatement statement = db.getPreparedStatement("UPDATE users SET currentstate = ?;");
		statement.setInt(1, USERSTATE_OFFLINE);
		statement.executeUpdate();
	}

	@Override
	public void createMetadata(long uid, String key, String value, int visibility) throws SQLException, ContentAlreadyExistsException {
		PreparedStatement statement = db.getPreparedStatement("SELECT * FROM userdata WHERE uid = ? AND key = ? AND deleted = 0;");
		statement.setLong(1, uid);
		statement.setString(2, key);
		if(statement.executeQuery().next()){
			throw new ContentAlreadyExistsException();
		}
		String insertQuery = "INSERT INTO " +
								"userdata (uid , updated, key, value, visibility)" +
							" VALUES (?,NOW(),?,?,?);";
		PreparedStatement query = db.getPreparedStatement(insertQuery);
		query.setLong(1, uid);
		query.setString(2, key);
		query.setString(3, value);
		query.setInt(4, visibility);
		query.execute();
	}

	@Override
	public void updateMetadata(long uid, String key, String value, int visibility) throws SQLException, ContentNotFoundException {
		String visibilityChange = (visibility == -1) ? "" : ", visibility = " + visibility + " ";
		PreparedStatement statement = db.getPreparedStatement("UPDATE userdata " +
							"SET updated = NOW(), value = ? " + visibilityChange + 
							"WHERE uid = ? AND key = ?;");
		statement.setString(1, value);
		statement.setLong(2, uid);
		statement.setString(3, key);
		int rows = statement.executeUpdate();
		if(rows == 0)
			throw new ContentNotFoundException();
	}

	@Override
	public void deleteMetadata(long uid, String key, int deletedId) throws SQLException, ContentNotFoundException {
		PreparedStatement statement = db.getPreparedStatement("UPDATE userdata SET deleted = ? WHERE uid = ? AND key = ?;");
		statement.setInt(1, deletedId);
		statement.setLong(2, uid);
		statement.setString(3, key);
		int rows = statement.executeUpdate();
		if(rows == 0)
			throw new ContentNotFoundException();
	}

	@Override
	public List<UserMetadata> fetchMetadata(long uid, long ofUid) throws SQLException {
		List<UserMetadata> result = new LinkedList<UserMetadata>();
		PreparedStatement statement = db.getPreparedStatement("SELECT updated, key, value, visibility FROM userdata WHERE uid = ? AND deleted = 0;");
		statement.setLong(1, ofUid);
		ResultSet rs = statement.executeQuery();
		boolean askedForAdmin = false, isAdmin = false;
		boolean askedForFriendship = false, isFriend = false;
		while(rs.next()){
			int visibility = rs.getInt("visibility");
			if((visibility == GlobalConfig.VISIBILITY_PUBLIC) ||
					(visibility == GlobalConfig.VISIBILITY_SOCOM) ||
					(visibility == GlobalConfig.VISIBILITY_PRIVATE && uid == ofUid) ||
					(askedForAdmin ? isAdmin : //if already asked for admin lookup saved value
						//otherwise look if admin (and set askedForAdmin = true)
						((isAdmin = userIsAdmin(uid)) && (askedForAdmin = true))) ||
					(visibility == GlobalConfig.VISIBILITY_FRIENDS && 
						(askedForFriendship ? isFriend :
						((isFriend = isFriendOf(ofUid, uid)) && (askedForFriendship = true)))))
//					(visibility == Visibility.GAME_INTERN FIXME: no sense since metadata are not yet game-specific..
				result.add(new UserMetadata(rs.getTimestamp("updated"), rs.getString("key"), rs.getString("value")));
		}
		
		return result;
	}

	@Override
	public String getNextGeneratableUserName() throws SQLException {
		String predefinedName = "GeneratedUser";
		PreparedStatement statement = db.getPreparedStatement("SELECT COUNT(name) FROM users WHERE name LIKE ?;");
		statement.setString(1, predefinedName + "%");
		ResultSet rs = statement.executeQuery();
		
		if(rs.next()){
			long nameCount = rs.getLong(1);
			return predefinedName + nameCount;
		}
		return null;
	}

	@Override
	//TODO FIXME use prepared statement instead of simple sql string execution
	public void addSNFriends(long uid, String network, List<Profile> friends, boolean twoway) throws SQLException {
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT uid FROM usersnaccounts WHERE snid = (SELECT id FROM socialnetworks WHERE name = '").
		append(network).append("') AND (false ");
		for(Profile friend : friends) {
			sb.append("OR username = '").append(friend.getNetworkID()).append("' ");
		}
		sb.append(");");
		ResultSet rs = db.execQueryWithResult(sb.toString());
		sb = new StringBuffer();
		while(rs.next()){
			long friendid = rs.getLong(1);
			sb.append("MERGE INTO usersnfriends USING (VALUES(").append(uid).append(",").append(friendid).
			append(")) AS newvals(uid, friendid) ON usersnfriends.uid = newvals.uid AND usersnfriends.friendid = newvals.friendid ").
			append("WHEN NOT MATCHED THEN INSERT VALUES newvals.uid, newvals.friendid; ");
			if(twoway) {
				sb.append("MERGE INTO usersnfriends USING (VALUES(").append(friendid).append(",").append(uid).
				append(")) AS newvals(uid, friendid) ON usersnfriends.uid = newvals.uid AND usersnfriends.friendid = newvals.friendid ").
				append("WHEN NOT MATCHED THEN INSERT VALUES newvals.uid, newvals.friendid; ");
			}
		}
		if(sb.length() > 0)
			db.execQuery(sb.toString());
	}

	@Override
	public boolean isFriendOf(long uid, long friendid) throws SQLException {
		PreparedStatement statement = db.getPreparedStatement("SELECT uid FROM usersnfriends WHERE uid = ? AND friendid = ?;");
		statement.setLong(1, uid);
		statement.setLong(2, friendid);
		ResultSet rs = statement.executeQuery();
		return rs.next();
	}

	@Override
	public void changeUsername(long uid, String password, String newUsername) throws SQLException, UserAlreadyExistsException, UIDOrSecretNotValidException{
		PreparedStatement statement = db.getPreparedStatement("SELECT uid FROM users WHERE name = ?;");
		statement.setString(1, newUsername);
		ResultSet exist = statement.executeQuery();
		if (exist.next()){
			//username not changed - ok.
			if(exist.getLong(1) == uid)
				return;
			//username already in use
			throw new UserAlreadyExistsException(newUsername);
		}
		statement = db.getPreparedStatement("UPDATE users SET name = ? WHERE uid = ? AND password = ?;");
		statement.setString(1, newUsername);
		statement.setLong(2, uid);
		statement.setString(3, password);
		
		int rows = statement.executeUpdate();
		if(rows < 1) throw new UIDOrSecretNotValidException();
	}

	@Override
	public void changePassword(long uid, String password, String newPassword) throws SQLException, UIDOrSecretNotValidException{
		PreparedStatement statement = db.getPreparedStatement("UPDATE users SET password = ? WHERE uid = ? AND password = ?;");
		statement.setString(1, newPassword);
		statement.setLong(2, uid);
		statement.setString(3, password);
		int rows = statement.executeUpdate();
		if(rows < 1) throw new UIDOrSecretNotValidException();
	}

	@Override
	public void changeVisibility(long uid, String password, int visibility) throws SQLException, UIDOrSecretNotValidException {
		PreparedStatement statement = db.getPreparedStatement("UPDATE users SET visiblity = ? WHERE uid = ? AND password = ?;");
		statement.setInt(1, visibility);
		statement.setLong(2, uid);
		statement.setString(3, password);
		int rows = statement.executeUpdate();
		if(rows < 1) throw new UIDOrSecretNotValidException();
	}
}
