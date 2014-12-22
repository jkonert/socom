package de.tud.kom.socom.components.user;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.tud.kom.socom.GlobalConfig;
import de.tud.kom.socom.SocomComponent;
import de.tud.kom.socom.components.game.GameInstance;
import de.tud.kom.socom.components.social.SNConnection;
import de.tud.kom.socom.components.social.SocialNetworkManager;
import de.tud.kom.socom.database.HSQLAccess;
import de.tud.kom.socom.database.game.GameDatabase;
import de.tud.kom.socom.database.game.HSQLGameDatabase;
import de.tud.kom.socom.database.user.HSQLUserDatabase;
import de.tud.kom.socom.database.user.HSQLUserGameInfoDatabase;
import de.tud.kom.socom.database.user.UserDatabase;
import de.tud.kom.socom.database.user.UserGameInfoDatabase;
import de.tud.kom.socom.util.EasyEncrypter;
import de.tud.kom.socom.util.JSONUtils;
import de.tud.kom.socom.util.ResourceLoader;
import de.tud.kom.socom.util.SocomRequest;
import de.tud.kom.socom.util.datatypes.JournalEntry;
import de.tud.kom.socom.util.datatypes.SimpleGameContext;
import de.tud.kom.socom.util.datatypes.User;
import de.tud.kom.socom.util.datatypes.UserMetadata;
import de.tud.kom.socom.util.exceptions.ContextNotFoundException;
import de.tud.kom.socom.util.exceptions.CurrentContextNotFoundException;
import de.tud.kom.socom.util.exceptions.CurrentGameInstanceNotIncludedException;
import de.tud.kom.socom.util.exceptions.SocomException;
import de.tud.kom.socom.util.exceptions.IllegalParameterException;
import de.tud.kom.socom.util.exceptions.ParseException;
import de.tud.kom.socom.util.exceptions.UIDNotIncludedException;
import de.tud.kom.socom.util.exceptions.UserNotFoundException;
import de.tud.kom.socom.util.playerstate.ObservedUIDs;

/**
 * 
 * @author rhaban
 * 
 */
public class UserManager extends SocomComponent {
	/* 	
	 * first character letter
	 * min 4 characters, max 15
	 * only letters, numbers, underscore
	 */
	private static final String PASSWORD_REGEX = "^[a-zA-Z]\\w{3,14}$";
	
	private static final String URL_PATTERN = "user";
	private static UserManager instance = new UserManager();
	private UserDatabase udb;
	private UserGameInfoDatabase uidb;
	private GameDatabase gamedb;

	private UserManager() {
		this.udb = HSQLUserDatabase.getInstance();
		this.uidb = HSQLUserGameInfoDatabase.getInstance();
		this.gamedb = HSQLGameDatabase.getInstance();
	}

	public static UserManager getInstance() {
		return instance;
	}

	/**
	 * Shows information about the current user or the user with the given id
	 * (iff the profile is visible)
	 * 
	 * @param uid
	 *            in session
	 * @param Optional
	 *            : id (long) socom id of the user to show
	 * @return Userinformation
	 * @throws SQLException
	 * @throws JSONException
	 * @throws SocomException
	 */
	public int getUser(SocomRequest req) throws SQLException, JSONException, SocomException {
		long uid = req.getUid();
		long profileId;
		try {
			profileId = Long.parseLong(req.getParam("id"));
		} catch (IllegalParameterException e) {
			profileId = uid;
		}
		User user = udb.fetchUser(uid, profileId);
		if (user == null)
			throw new UserNotFoundException(profileId);
		
		req.addOutput(user.toJSONString());
		return 0;
	}

	/**
	 * Loggs a user in. Starts a new session with his id.
	 * 
	 * @param username
	 *            Name of the user
	 * @param password
	 *            User's password
	 * @param game
	 *            Name of the current game
	 * @param version
	 *            Current game's version
	 * @param gamepassword
	 *            Game's Password
	 * @return The users id.
	 * @throws SQLException
	 * @throws JSONException
	 */
	public int loginUser(SocomRequest req) throws SQLException, JSONException, SocomException {
		// User already logged id?
		try {
			req.getUid();
			return 11;
		} catch (UIDNotIncludedException e) {
		}

		String gamename = req.getParam("game");
		String gameversion = req.getParam("version");
		String gamepassword = req.getParam("gamepassword");
		String username = req.getParam("username");
		String password = req.getParam("password");

		long gameinstanceid = gamedb.authenticateGameInstance(gamename, gameversion, gamepassword);
		password = EasyEncrypter.getSHA(password);
		long[] ids = udb.validateUser(gameinstanceid, username, password);
		udb.updateUsersGame(ids[0], gameinstanceid);
		ObservedUIDs.getInstance().setOnline(ids[0]);
		req.setUid(ids[0]);
		req.setCurrentGameInst(ids[1]);
		req.addOutput(JSONUtils.JSONToString(new JSONObject().put("uid", ids[0])));

		return 0;
	}

	/**
	 * Logs the current user out. Does not delete anything except the session..
	 * 
	 * @param uid
	 *            in session
	 * @return success boolean
	 * @throws JSONException
	 */
	public int logout(SocomRequest req) throws SQLException, JSONException, SocomException {
		udb.setUserOffline(req.getUid());
		udb.updateUsersGame(req.getUid(), req.getCurrentGameInst());
		req.removeUid();
		req.removeGameInst();
		req.addOutput(JSONUtils.getSuccessJsonString());
		return 0;
	}

	/**
	 * Creates a new user. After this the user is logged in and bound to the
	 * current game instance.
	 * 
	 * @param username
	 * @param password
	 * @param game
	 * @param version
	 *            Game's version
	 * @param gamepassword
	 * @param visibility
	 *            (int, 0 = private, 1 = friends, 2 = public)
	 * @return uid of the new user
	 * @throws SQLException
	 * @throws JSONException
	 */
	public int createUser(SocomRequest req) throws SQLException, JSONException, SocomException {
		// User already logged id?
		try {
			req.getUid();
			return 11;
		} catch (UIDNotIncludedException e) {
		}

		String username = req.getParam("username");
		if (username.startsWith("GeneratedUser")) // forbidden username
			throw new IllegalParameterException("username must not start with GeneratedUser..");
		String password = req.getParam("password");
		if(!password.matches(PASSWORD_REGEX)) 
			throw new IllegalParameterException("password", 
					"Password must start with letter, have at least 4 " +
					"and at maximum 14 characters " +
					"and contain at least one number");
		
		password = EasyEncrypter.getSHA(password);
		String gamename = req.getParam("game");
		String gameversion = req.getParam("version");
		String gamepassword = req.getParam("gamepassword");
		int visibility = Integer.parseInt(req.getParam("visibility"));

		long gameinstanceid = gamedb.authenticateGameInstance(gamename, gameversion, gamepassword);

		long[] ids = udb.createUser(gameinstanceid, username, password, visibility);
		udb.updateUsersGame(ids[0], gameinstanceid);
		req.setUid(ids[0]);
		req.setCurrentGameInst(ids[1]);
		req.addOutput(JSONUtils.JSONToString(new JSONObject().put("uid", ids[0])));
		return 0;
	}
	
	public int changeUser(SocomRequest req) throws SQLException, JSONException, SocomException {
		// User already logged id?
		long uid = req.getUid();
		String username = req.getParam("username", null);
		if (username != null && username.startsWith("GeneratedUser")) // forbidden username
			throw new IllegalParameterException("username must not start with GeneratedUser..");
		String password = req.getParam("password");
		int visibility = req.getParam("visibility", -1);
		if(username != null)
		{
			udb.changeUsername(uid, password, username);
		}
		if(visibility != -1)
		{
			udb.changeVisibility(uid, password, visibility);
		}
		req.addOutput(JSONUtils.getSuccessJsonString());
		return 0;
	}

	/**
	 * Generates a user and returns a url to login via facebook.
	 * 
	 * @param game
	 * @param version
	 * @param gamepassword
	 * @param visibility
	 *            (int, 0 = private, 1 = friends, 2 = public)
	 * @param network
	 *            (Facebook, VZNet, Google+) where the user wants to login with
	 * @return uid of the new user, his username and password, a loginUrl
	 *         leading to the social network login
	 * @throws SQLException
	 * @throws JSONException
	 */
	public int createUserWithSocialNetwork(SocomRequest req) throws SQLException, JSONException, SocomException {
		try {
			req.getUid();
			return 11;
		} catch (UIDNotIncludedException e) {
		}

		String gamename = req.getParam("game");
		String version = req.getParam("version");
		String gamepassword = req.getParam("gamepassword");
		long gameinstanceid = gamedb.authenticateGameInstance(gamename, version, gamepassword);

		int visibility = Integer.parseInt(req.getParam("visibility"));
		String network = req.getParam("network");

		SNConnection nwconn = SocialNetworkManager.getInstance().getConnection(network, gameinstanceid);
		if (nwconn == null)
			return 4;

		String username = udb.getNextGeneratableUserName();
		String password = EasyEncrypter.getInstance().getRandomPassword();
		String passwordHash = EasyEncrypter.getSHA(password);
		long[] ids = udb.createUser(gameinstanceid, username, passwordHash, visibility);
		long uid = ids[0];

		String loginUrl = nwconn.getLoginURL(uid, gameinstanceid);

		udb.updateUsersGame(uid, gameinstanceid);
		req.setUid(uid);
		req.setCurrentGameInst(ids[1]);
		req.addOutput(JSONUtils.JSONToString(new JSONObject().put("uid", uid).put("username", username).put("password", password).put("loginUrl", loginUrl)));
		return 0;
	}

	/**
	 * Deletes a user. (Can be re-activated in the web-application)
	 * 
	 * @param password User's password
	 * @return success boolean
	 * @throws SQLException
	 * @throws JSONException
	 */
	public int deleteUser(SocomRequest req) throws SQLException, JSONException, SocomException {
		long uid = req.getUid();
		String password = req.getParam("password");
		password = EasyEncrypter.getSHA(password);
		boolean success = udb.deleteUser(uid, password);
		logout(req);
		req.setOutput(JSONUtils.getSuccessJsonString(success));
		return 0;
	}

	/**
	 * Sets the current user as admin
	 * @param password User's password
	 * @param mastersecret 
	 * @return success boolean
	 * @throws SQLException
	 * @throws JSONException
	 */
	public int becomeAdmin(SocomRequest req) throws SQLException, JSONException, SocomException {
		if (req.getParam("mastersecret").equals(ResourceLoader.getResource("mastersecret"))) {
			long uid = req.getUid();
			String password = req.getParam("password");
			password = EasyEncrypter.getSHA(password);
			udb.becomeAdmin(uid, password);

			req.addOutput(JSONUtils.getSuccessJsonString());
		} else
			return 19; // illegal access
		return 0;
	}

	/**
	 * Shows a list of the users game instances he is/was playing
	 * 
	 * @throws SQLException
	 * @throws JSONException
	 */
	public int getUsersGames(SocomRequest req) throws SQLException, JSONException, SocomException {
		long uid = req.getUid();
		List<GameInstance> result = udb.getUsersGames(uid);

		req.addOutput(JSONUtils.JSONToString(new JSONObject().put("games", new JSONArray(result))));

		return 0;
	}

	/**
	 * Sets the current context for the player (contexts may be: scenes, level, mapparts, ..)
	 * 
	 * If the context does not exist it will be created, furthermore a relation between two contexts will be created
	 * 
	 * @param context External ID of the current context
	 * @param isNewGame true if the game is currently restarted to 
	 * 					prevent autogenerated relations to beginning scene (default: false)
	 * @return success boolean
	 * @throws SQLException
	 * @throws JSONException
	 */
	public int setCurrentContext(SocomRequest req) throws SQLException, JSONException, SocomException {
		long uid = req.getUid();
		String context = req.getParam("context");
		boolean isNewGame = req.getParam("isNewGame", false);
		long gameInstance = req.getCurrentGameInst();
		long contextId = -1L;
		long lastContextId = -1L;

		while (true) {
			try {
				// get new context
				contextId = gamedb.getGameContextId(context, gameInstance);
				// save last context
				lastContextId = uidb.getCurrentContext(uid, gameInstance);
				// autogenerate relation (if it doesn't already exist, and its not game restart)
				if (lastContextId != contextId && !isNewGame)
					gamedb.addContextRelation(gameInstance, lastContextId, contextId, true);
				// go further
				break;
			} catch (ContextNotFoundException e) {
				// if the new context does not exist -> autogenerate it and use given id
				context = gamedb.autogenerateContext(gameInstance, context);
				// try again
				continue;
			} catch (CurrentContextNotFoundException e) {
				// current context not present -> skip autogen relation
				break;
			}
		}
		uidb.setCurrentContext(uid, lastContextId, contextId);
		req.addOutput(JSONUtils.getSuccessJsonString());
		return 0;
	}

	/**
	 * Show all contexts the user ever visited.
	 * 
	 * @return List of contexts
	 * @throws SQLException
	 * @throws JSONException
	 */
	public int getVisitedContexts(SocomRequest req) throws SQLException, JSONException, SocomException {
		long uid = req.getUid();
		long gameInst = req.getCurrentGameInst();
		List<SimpleGameContext> contexts = uidb.getVisitedContexts(uid, gameInst);

		req.addOutput(JSONUtils.JSONToString(new JSONObject().put("contexts", contexts)));
		return 0;
	}

	/**
	 * Adds a gamelog (mostly debug-msgs for game-developers)
	 * 
	 * @param type
	 * @param message
	 * @throws SQLException
	 * @throws JSONException
	 * @throws SocomException
	 */
	public int addLog(SocomRequest req) throws SQLException, JSONException, SocomException {
		int visibility = GlobalConfig.VISIBILITY_NON_USER;
		return createLog(req, visibility);
	}
	
	/**
	 * Adds a journal entry for the user
	 * 
	 * @param type
	 * @param message
	 * @param Optional: visibility
	 * @throws SQLException
	 * @throws JSONException
	 * @throws IllegalAccessException
	 */
	public int addJournalEntry(SocomRequest req) throws SQLException, JSONException, SocomException {
		int visibility = GlobalConfig.VISIBILITY_PUBLIC;
		if (req.getParams().containsKey("visibility")) {
			visibility = Integer.parseInt(req.getParam("visibility"));
		}

		return createLog(req, visibility);
	}

	private int createLog(SocomRequest req, int visibility) throws SocomException, SQLException, JSONException {
		long uid = req.getUid();
		long currentGameInst = req.getCurrentGameInst();
		String type = req.getParam("type").toUpperCase();
		String message = req.getParam("message");

		uidb.addJournalEntry(uid, currentGameInst, new JournalEntry(type, message, visibility));
		req.addOutput(JSONUtils.getSuccessJsonString());
		return 0;
	}

	/**
	 * Shows all Journal Entries for the current user. 
	 * 
	 * @param Optional: limit (max. entries to show)
	 * @param Optional: offset (sql-like limit & offset)
	 * @param Optional: type Which types should only be shown
	 * @return List of Journal-Entries
	 * @throws SQLException
	 * @throws JSONException
	 */
	public int getJournalEntries(SocomRequest req) throws SQLException, JSONException, SocomException {
		boolean gameLogs = false;
		return getLogs(req, gameLogs);
	}
	
	/**
	 * Shows all GameLogs for the current user (only accessible for the game itself)
	 * 
	 * @param gamepassword
	 * @param Optional: limit (max. entries to show)
	 * @param Optional: offset (sql-like limit & offset)
	 * @param Optional: type Which types should only be shown
	 * @throws SocomException
	 * @throws SQLException
	 * @throws JSONException
	 */
	public int getLogs(SocomRequest req) throws SocomException, SQLException, JSONException{
		String gamepassword = req.getParam("gamepassword");
		long gameinstid = req.getCurrentGameInst();
		HSQLGameDatabase.getInstance().authenticateGameInstance(gameinstid, gamepassword);
		boolean gameLogs = true;
		return getLogs(req, gameLogs);
	}

	private int getLogs(SocomRequest req, boolean gameLogs) throws UIDNotIncludedException,
			CurrentGameInstanceNotIncludedException, ParseException, SQLException, JSONException,
			IllegalParameterException {
		String debugParamString = "limit";
		try {
			long uid = req.getUid();
			long gameinstid = req.getCurrentGameInst();
			Map<String, String> params = req.getParams();
			String limitParam = params.get("limit");
			String offsetParam = params.get("offset");
			String type = params.get("type");

			int limit = limitParam == null ? 0 : Integer.parseInt(limitParam);
			debugParamString = "offset";
			int offset = offsetParam == null ? 0 : Integer.parseInt(offsetParam);
			if (limit < 0 || offset < 0)
				return 4;

			if (type == null)
				type = "all";
			else
				type = type.toUpperCase();

			List<JournalEntry> logs = uidb.getUserJournal(uid, gameinstid, limit, offset, type, gameLogs);

			req.addOutput(JSONUtils.JSONToString(new JSONObject().put("logs", logs)));
		} catch (NumberFormatException e) {
			throw new IllegalParameterException(debugParamString + " must be of type integer.");
		}
		return 0;
	}

	/**
	 * Adds time (in seconds) to the 'already played time' for the player in his current context
	 * 
	 * @param time in s
	 * @return success boolean
	 * @throws SQLException
	 * @throws JSONException
	 */
	public int addTimePlayed(SocomRequest req) throws SQLException, JSONException, SocomException {
		try {
			long uid = req.getUid();
			long currentGameInst = req.getCurrentGameInst();
			long contextId = uidb.getCurrentContext(uid, currentGameInst);
			long time = Long.parseLong(req.getParam("time"));

			uidb.addTimePlayed(uid, contextId, time);
			req.addOutput(JSONUtils.getSuccessJsonString());
		} catch (NumberFormatException e) {
			throw new IllegalParameterException("time must be of type long.");
		}
		return 0;
	}

	/**
	 * Resets the 'time already played' (=0) for the player in his current context
	 * 
	 * @return success boolean
	 * @throws SQLException
	 * @throws JSONException
	 */
	public int resetTimePlayed(SocomRequest req) throws SQLException, JSONException, SocomException {
		long uid = req.getUid();
		long currentGameInst = req.getCurrentGameInst();
		long contextId = uidb.getCurrentContext(uid, currentGameInst);

		uidb.setTimePlayed(uid, contextId, 0);
		req.addOutput(JSONUtils.getSuccessJsonString());
		return 0;
	}

	/**
	 * Sets the 'already played time' (in seconds) for the player in his current context
	 * 
	 * @param time in s
	 * @return success boolean
	 * @throws SQLException
	 * @throws JSONException
	 */
	public int setTimePlayed(SocomRequest req) throws SQLException, JSONException, SocomException {
		try {
			long uid = req.getUid();
			long currentGameInst = req.getCurrentGameInst();
			long contextId = uidb.getCurrentContext(uid, currentGameInst);
			long time = Long.parseLong(req.getParam("time"));

			uidb.setTimePlayed(uid, contextId, time);
			req.addOutput(JSONUtils.getSuccessJsonString());
		} catch (NumberFormatException e) {
			throw new IllegalParameterException("time must be of type long.");
		}
		return 0;
	}

	/**
	 * Show the time played in the current context
	 * 
	 * @return time in s
	 * @throws SQLException
	 * @throws JSONException
	 */
	public int getTimePlayed(SocomRequest req) throws SQLException, JSONException, SocomException {
		long uid = req.getUid();
		long currentGameInst = req.getCurrentGameInst();
		long contextId = uidb.getCurrentContext(uid, currentGameInst);
		long timeplayed = uidb.getTimePlayed(uid, contextId);

		req.addOutput(JSONUtils.JSONToString(new JSONObject().put("timeplayed", timeplayed)));
		return 0;
	}

	/**
	 * Creates a new Metadata key-value-Pair for the user
	 * The Metadata have public visibility if nothing else is defined
	 * 
	 * @param key
	 * @param value
	 * @param Optional: visibility (Integer) (if not specified its 0 (private)
	 * @return success boolean	
	 * @throws SQLException
	 * @throws JSONException
	 */
	public int createMetadata(SocomRequest req) throws SQLException, JSONException, SocomException {
		long uid = req.getUid();
		String key = req.getParam("key");
		String value = req.getParam("value");
		int visibility = GlobalConfig.VISIBILITY_PRIVATE;
		if(req.containsParam("visibility"))
			visibility = Integer.parseInt(req.getParam("visibility"));
		udb.createMetadata(uid, key, value, visibility);

		req.addOutput(JSONUtils.getSuccessJsonString());
		return 0;
	}

	/**
	 * Changes an existing Metadata
	 * 
	 * @param key Existing key
	 * @param value
	 * @param Optional: visibility (Integer)
	 * @return success boolean
	 * @throws SQLException
	 * @throws JSONException
	 */
	public int updateMetadata(SocomRequest req) throws SQLException, JSONException, SocomException {
		long uid = req.getUid();
		String key = req.getParam("key");
		String value = req.getParam("value");
		int visibility = -1;
		if(req.containsParam("visibility"))
			visibility = Integer.parseInt(req.getParam("visibility"));
		udb.updateMetadata(uid, key, value, visibility);

		req.addOutput(JSONUtils.getSuccessJsonString());
		return 0;
	}

	/**
	 * Delete an existing Metadata
	 * 
	 * @param key Existing metadata-key
	 * @param Optional: deleted The deleted value
	 * @return success boolean
	 * @throws SQLException
	 * @throws JSONException
	 */
	public int deleteMetadata(SocomRequest req) throws SQLException, JSONException, SocomException {
		long uid = req.getUid();
		String key = req.getParam("key");
		int deleted = 1;
		if (req.getParams().containsKey("deleted"))
			deleted = Integer.parseInt(req.getParam("deleted"));
		udb.deleteMetadata(uid, key, deleted);

		req.addOutput(JSONUtils.getSuccessJsonString());
		return 0;
	}

	/**
	 * Shows existing, visible Metadata
	 * 
	 * @return The metadata
	 * @throws SQLException
	 * @throws JSONException
	 */
	public int getMetadata(SocomRequest req) throws SQLException, JSONException, SocomException {
		long currentUid = req.getUid();
		long ofUid = req.containsParam("of") ? Long.parseLong(req.getParam("of")) : currentUid;
		List<UserMetadata> result = udb.fetchMetadata(currentUid, ofUid);

		req.addOutput(JSONUtils.JSONToString(new JSONObject().put("data", result)));
		return 0;
	}
	
	/**
	 * Changes the username
	 * @param username The new usersname the user wishes to have
	 * @param password the actual password to authenticate again
	 * @return success boolean 
	 * @throws JSONException 
	 */
	public int changeUsername(SocomRequest req) throws SocomException, SQLException, JSONException {
		long uid = req.getUid();
		String newUsername = req.getParam("username");
		String password = req.getParam("password");
		password = EasyEncrypter.getSHA(password);
		udb.changeUsername(uid, password, newUsername);
		req.addOutput(JSONUtils.getSuccessJsonString());
		return 0;
	}
	
	/**
	 * Changes the users password
	 * @param password the actual password to authenticate again
	 * @param newPassword the new password the users wishes to have
	 * @return success boolean 
	 * @throws JSONException 
	 */
	public int changeUserPassword(SocomRequest req) throws SocomException, SQLException, JSONException {
		long uid = req.getUid();
		String password = req.getParam("password");
		String newPassword = req.getParam("newpassword");
		if(!newPassword.matches(PASSWORD_REGEX)) 
			throw new IllegalParameterException("newpassword", 
					"Password must start with letter, have at least 4 " +
					"and at maximum 14 characters " +
					"and contain at least one number");
		
		password = EasyEncrypter.getSHA(password);
		newPassword = EasyEncrypter.getSHA(newPassword);
		
		udb.changePassword(uid, password, newPassword);
		req.addOutput(JSONUtils.getSuccessJsonString());
		return 0;
	}
	
	/*
	 * hacked in method to stop socom (when someone forgets to shutdown the server ;) )
	 */
	public int exit(SocomRequest req) throws SocomException, SQLException {
		if (req.getParam("mastersecret").equals(ResourceLoader.getResource("mastersecret")))
		{
			HSQLAccess.getInstance().execQuery("SHUTDOWN");
			System.exit(0);
			return 0;
		}
		return 19; // illegal access
	}

	/**
	 * URL PATTERN IS "user"
	 */
	@Override
	public String getUrlPattern() {
		return URL_PATTERN;
	}
}
