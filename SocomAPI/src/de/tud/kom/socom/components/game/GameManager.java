package de.tud.kom.socom.components.game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import de.tud.kom.socom.SocomComponent;
import de.tud.kom.socom.GlobalConfig;
import de.tud.kom.socom.database.game.GameDatabase;
import de.tud.kom.socom.database.game.HSQLGameDatabase;
import de.tud.kom.socom.util.SocomRequest;
import de.tud.kom.socom.util.JSONUtils;
import de.tud.kom.socom.util.ResourceLoader;
import de.tud.kom.socom.util.exceptions.GameNotAuthenticatedException;
import de.tud.kom.socom.util.exceptions.SocomException;
import de.tud.kom.socom.util.exceptions.IllegalAccessException;
import de.tud.kom.socom.util.media.MediaHandler;

public class GameManager extends SocomComponent implements GlobalConfig {

	private static final String URL_PATTERN = "game";
	private static GameManager instance = new GameManager();
	private static GameDatabase db;

	private GameManager() {
		db = HSQLGameDatabase.getInstance();
	}

	public static GameManager getInstance() {
		return instance;
	}

	/**
	 * URL PATTERN IS "game"
	 */
	@Override
	public String getUrlPattern() {
		return URL_PATTERN;
	}

	/**
	 * Create a new Game.
	 * 
	 * Can be called without a user being logged in.
	 * 
	 * @param game
	 *            The name of the new game
	 * @param genre
	 * @param password
	 * @param mastersecret
	 * @return error code
	 * @throws SQLException
	 * @throws JSONException
	 */
	public int addGame(SocomRequest req) throws SQLException, JSONException, SocomException {
		String name = req.getParam("game");
		String genre = req.getParam("genre");
		String password = req.getParam("password");
		String mastersecret = req.getParam("mastersecret");
		if (!mastersecret.equals(ResourceLoader.getResource("mastersecret")))
			throw new IllegalAccessException();

		Game game = new Game(-1L, name, genre, password, new ArrayList<GameInstance>());
		db.addGame(game);
		req.addOutput(JSONUtils.getSuccessJsonString());
		return 0;
	}

	/**
	 * Remove a game.
	 * 
	 * Can be called without a user being logged in.
	 * 
	 * @param game
	 * @param mastersecret
	 * @return success boolean
	 * @throws SQLException
	 * @throws JSONException
	 */
	public int removeGame(SocomRequest req) throws SQLException, JSONException, SocomException {
		String mastersecret = req.getParam("mastersecret");
		String game = req.getParam("game");
		if (!mastersecret.equals(ResourceLoader.getResource("mastersecret")))
			throw new IllegalAccessException();

		db.removeGame(game);
		req.addOutput(JSONUtils.getSuccessJsonString());
		return 0;
	}

	/**
	 * Shows information about an existing game, including all instances.
	 * 
	 * Can be called without a user being logged in.
	 * 
	 * @param game
	 * @param password
	 * @return Game Object including its instances
	 * @throws SQLException
	 * @throws JSONException
	 */
	public int getGame(SocomRequest req) throws SQLException, JSONException, SocomException {
		String password = req.getParam("password");
		String gamename = req.getParam("game");
		long gameId = db.authenticateGame(gamename, password);

		Game game = db.getGame(gameId);
		if (game == null)
			throw new GameNotAuthenticatedException("Game not found");
		req.addOutput(game.toJSONString());
		return 0;
	}

	/**
	 * Create a new Game Instance.
	 * 
	 * Can be called without a user being logged in.
	 * 
	 * @param game
	 * @param password
	 * @param version
	 *            (String)
	 * @param description
	 * @return success boolean
	 * @throws SQLException
	 * @throws JSONException
	 */
	public int addGameInstance(SocomRequest req) throws SQLException, JSONException, SocomException {
		String password = req.getParam("password");
		String game = req.getParam("game");
		String version = req.getParam("version");
		String description = req.containsParam("description") ? req.getParam("description") : null;
		long gameId = db.authenticateGame(game, password);

		GameInstance gameinstance = new GameInstance(game, version, description);
		db.addInstance(gameId, gameinstance);
		req.addOutput(JSONUtils.getSuccessJsonString());
		return 0;
	}
	
	/**
	 * POST-Method (for longer description texts)
	 * 
	 * Sets the description for a gameinstance
	 * 
	 * @param game (cookie)
	 * @param password (game's password) (cookie)
	 * @param gameversion (gameversion since version is reserved cookie) (cookie)
	 * @param description (as stream)
	 * @return success boolean
	 * @throws SQLException
	 * @throws JSONException
	 * @throws SocomException
	 * @throws IOException 
	 */
	public int setGameInstanceDescription(SocomRequest req) throws SQLException, JSONException, SocomException, IOException {
		String game = req.getCookieVal("game");
		String password = req.getCookieVal("password");
		String version = req.getCookieVal("gameversion");
		BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()));
		
		StringBuffer descriptionBuffer = new StringBuffer();
		while(reader.ready()) {
			descriptionBuffer.append(reader.readLine());
		}
		reader.close();
		
		long gameInstance = db.authenticateGameInstance(game, version, password);
		boolean success = db.setInstanceDescription(gameInstance, descriptionBuffer.toString());
		req.addOutput(JSONUtils.getSuccessJsonString(success));
		return 0;
	}

	/**
	 * Remove a game instance. Allows you to reuse the game+version combination.
	 * 
	 * Can be called without a user being logged in.
	 * 
	 * @param game
	 * @param version
	 * @param password
	 * @return success boolean
	 * @throws SQLException
	 * @throws JSONException
	 */
	public int removeGameInstance(SocomRequest req) throws SQLException, JSONException, SocomException {
		String password = req.getParam("password");
		String game = req.getParam("game");
		String version = req.getParam("version");
		long gameInstanceId = db.authenticateGameInstance(game, version, password);

		db.removeInstance(gameInstanceId);
		req.addOutput(JSONUtils.getSuccessJsonString());
		return 0;
	}

	/**
	 * Add a new Context to an existing GameInstance.
	 * 
	 * Can be called without a user being logged in.
	 * 
	 * @param game
	 * @param version
	 * @param password
	 * @param contextid
	 *            Your external id
	 * @param name
	 *            Context's name
	 * @return success boolean
	 * @throws JSONException
	 * @throws SQLException
	 */
	public int addGameContext(SocomRequest req) throws JSONException, SQLException, SocomException {
		String password = req.getParam("password");
		String game = req.getParam("game");
		String version = req.getParam("version");
		String extContextId = req.getParam("contextid");
		String name = req.getParam("name");
		long gameInstId = db.authenticateGameInstance(game, version, password);

		db.addContext(gameInstId, extContextId, name);
		req.addOutput(JSONUtils.getSuccessJsonString());
		return 0;
	}

	/**
	 * POST-Method
	 * 
	 * sets the description of a given context
	 * @param game (cookie)
	 * @param password (game's password) (cookie)
	 * @param gameversion (gameversion since version is reserved cookie) (cookie) (cookie)
	 * @param contextid (cookie)
	 * @param description (as stream)
	 * @return success boolean
	 * @throws JSONException
	 * @throws SQLException
	 * @throws SocomException
	 * @throws IOException 
	 */
	public int setGameContextDescription(SocomRequest req) throws JSONException, SQLException, SocomException, IOException {
		String game = req.getCookieVal("game");
		String password = req.getCookieVal("password");
		String version = req.getCookieVal("gameversion");
		String contextid = req.getCookieVal("contextid");
		
		long gameinstanceid = db.authenticateGameInstance(game, version, password);
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()));
		StringBuffer descriptionBuffer = new StringBuffer();
		
		while(reader.ready()) {
			descriptionBuffer.append(reader.readLine());
		}
		reader.close();
		
		boolean success = db.setContextDescription(gameinstanceid, contextid, descriptionBuffer.toString());
		req.addOutput(JSONUtils.getSuccessJsonString(success));
		return 0;
	}
	
	/**
	 * Removes a contexts.
	 * 
	 * Can be called without a user being logged in.
	 * 
	 * @param game
	 * @param version
	 * @param password
	 * @param contextid
	 * @return success boolean
	 * @throws JSONException
	 * @throws SQLException
	 */
	public int removeGameContext(SocomRequest req) throws JSONException, SQLException, SocomException {
		String password = req.getParam("password");
		String game = req.getParam("game");
		String version = req.getParam("version");
		String context = req.getParam("contextid");
		long gameInstId = db.authenticateGameInstance(game, version, password);

		db.removeContext(gameInstId, context);
		req.addOutput(JSONUtils.getSuccessJsonString());
		return 0;
	}

	/**
	 * Retrieves all contexts from a gameintance including its scenes.
	 * 
	 * Can be called without a user being logged in.
	 * 
	 * @param game
	 * @param version
	 * @param password
	 * @return error code
	 * @throws JSONException
	 * @throws SQLException
	 */
	public int getGameContexts(SocomRequest req) throws JSONException, SQLException, SocomException {
		String password = req.getParam("password");
		String game = req.getParam("game");
		String version = req.getParam("version");
		long gameInstId = db.authenticateGameInstance(game, version, password);

		List<GameContext> result = db.getGameContexts(gameInstId);
		req.addOutput(JSONUtils.JSONToString(new JSONObject().put("contexts", result)));
		return 0;
	}

	/**
	 * Retrieves the context with the given id.
	 * 
	 * Can be called without a user being logged in.
	 * 
	 * @param game
	 * @param version
	 * @param password
	 * @param contextid
	 *            Your external id of the context which should be fetched
	 * @return Game-Context Object
	 * @throws SQLException
	 * @throws JSONException
	 */

	public int getGameContext(SocomRequest req) throws SQLException, JSONException, SocomException {
		String password = req.getParam("password");
		String game = req.getParam("game");
		String version = req.getParam("version");
		String contextid = req.getParam("contextid");
		long gameinstid = db.authenticateGameInstance(game, version, password);

		GameContext gs = db.getGameContext(gameinstid, contextid);

		req.addOutput(JSONUtils.JSONToString(new JSONObject().put("context", gs)));
		return 0;
	}

	/**
	 * Add a new relation between existing contexts.
	 * 
	 * Can be called without a user being logged in.
	 * 
	 * @param game
	 * @param version
	 * @param password
	 * @param parent
	 *            External id of the parent-context-node
	 * @param child
	 *            External id of the child-context-node
	 * @return success boolean
	 * @throws JSONException
	 * @throws SQLException
	 */
	public int addGameContextRelation(SocomRequest req) throws JSONException, SQLException, SocomException {
		String password = req.getParam("password");
		String game = req.getParam("game");
		String version = req.getParam("version");
		String parent = req.getParam("parent");
		String child = req.getParam("child");
		long gameInstId = db.authenticateGameInstance(game, version, password);

		db.addContextRelation(gameInstId, parent, child, false);
		req.addOutput(JSONUtils.getSuccessJsonString());
		return 0;
	}

	/**
	 * @see(addGameContextRelation) Removes relation between existing contexts.
	 * 
	 *                              Can be called without a user being logged
	 *                              in.
	 * 
	 * @param game
	 * @param version
	 * @param password
	 * @param parent
	 * @param child
	 * @return error code
	 * @throws JSONException
	 * @throws SQLException
	 */
	public int removeGameContextRelation(SocomRequest req) throws JSONException, SQLException, SocomException {
		String password = req.getParam("password");
		String game = req.getParam("game");
		String version = req.getParam("version");
		String parent = req.getParam("parent");
		String child = req.getParam("child");
		long gameInstId = db.authenticateGameInstance(game, version, password);

		boolean success = db.removeContextRelation(gameInstId, parent, child);
		req.addOutput(JSONUtils.getSuccessJsonString(success));
		return 0;
	}

	/**
	 * Retrieves a context's relations from this gameintance.
	 * GameContexts without any connections arent listed here
	 * 
	 * Can be called without a user being logged in.
	 * 
	 * @param game
	 * @param version
	 * @param password
	 * @param contextid
	 * @return List of Contexts containing the ids of their neighbour-nodes
	 * @throws JSONException
	 * @throws SQLException
	 * @throws SocomException
	 */
	public int getGameContextRelations(SocomRequest req) throws JSONException, SQLException, SocomException {
		String password = req.getParam("password");
		String game = req.getParam("game");
		String version = req.getParam("version");
		String contextid = req.getParam("contextid");
		long gameInstId = db.authenticateGameInstance(game, version, password);

		GameContext result = db.getGameContextRelations(gameInstId, contextid);
		req.addOutput(JSONUtils.JSONToString(result.getJSONObject()));
		return 0;
	}

	/**
	 * POST-METHOD Set the image for the given gameinstance which will represent
	 * the game in the web-application
	 * 
	 * Params must be given as cookies
	 * 
	 * @param game
	 * @param password
	 * @param gameversion
	 *            (since version is reserved in cookies)
	 * @param extension
	 *            Filename extension of the uploaded image file
	 * @param The
	 *            Post-Stream should include the image (Good-Sizes: 460px ×
	 *            150px ~ 380px × 260px)
	 * @return success boolean
	 * @throws SocomException
	 * @throws IOException
	 * @throws JSONException
	 * @throws SQLException
	 */
	public int setGameInstanceImage(SocomRequest req) throws SocomException, IOException, JSONException, SQLException {
		String password = req.getCookieVal("password");
		String game = req.getCookieVal("game");
		String version = req.getCookieVal("gameversion");
		String extension = req.getCookieVal("extension");

		InputStream imageStream = req.getInputStream();

		long instanceid = db.authenticateGameInstance(game, version, password);
		String imageFile = MediaHandler.saveGameImage(game, version, extension, imageStream);

		boolean success = db.setInstanceImage(instanceid, imageFile);
		req.addOutput(JSONUtils.getSuccessJsonString(success));
		return 0;
	}

	/**
	 * POST-METHOD Set the image for the given gamecontext in the given
	 * gameinstance which will represent the context in the web-application
	 * 
	 * Params must be given as cookies
	 * 
	 * @param game
	 * @param password
	 * @param gameversion
	 *            (since version is reserved in cookies)
	 * @param contextid
	 * @param extension
	 *            Filename extension of the uploaded image file
	 * @param The
	 *            Post-Stream should include the image (Good-Sizes: 460px ×
	 *            150px ~ 380px × 260px)
	 * @return success boolean
	 * @throws SocomException
	 * @throws IOException
	 * @throws JSONException
	 * @throws SQLException
	 */
	public int setGameContextImage(SocomRequest req) throws SocomException, IOException, SQLException, JSONException {

		String password = req.getCookieVal("password");
		String game = req.getCookieVal("game");
		String version = req.getCookieVal("gameversion");
		String contextid = req.getCookieVal("contextid");
		String extension = req.getCookieVal("extension");

		InputStream imageStream = req.getInputStream();

		long instanceid = db.authenticateGameInstance(game, version, password);
		String imageFile = MediaHandler.saveContextImage(game, version, contextid, extension, imageStream);

		boolean success = db.setContextImage(instanceid, contextid, imageFile);
		req.addOutput(JSONUtils.getSuccessJsonString(success));
		return 0;
	}
}
