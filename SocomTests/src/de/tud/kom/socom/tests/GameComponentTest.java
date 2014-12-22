package de.tud.kom.socom.tests;

import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import de.tud.kom.socom.tests.util.StaticConfiguration;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GameComponentTest extends ComponentTest {

	private static final String GAME_URL_FRAGMENT = "game";

	@Test
	public void aa_createGameTest() {
		String game = makeParameter("game", "Portal");
		String password = makeParameter("password", "portalpw");
		String genre = makeParameter("genre", "Role-Playing");
		String mastersecret = makeParameter("mastersecret", StaticConfiguration.MASTER_SECRET);

		String method = "addGame";
		String params = appendUrlParameter(game, password, genre, mastersecret);
		testMethod(method, params, getSuccessJSON());
	}
	
	@Test
	public void ab_createGameTest() {
		String game = makeParameter("game", "Woodment");
		String password = makeParameter("password", "woodmentpw");
		String genre = makeParameter("genre", "Simulation");
		String mastersecret = makeParameter("mastersecret", StaticConfiguration.MASTER_SECRET);

		String method = "addGame";
		String params = appendUrlParameter(game, password, genre, mastersecret);
		testMethod(method, params, getSuccessJSON());
	}
	
	@Test
	public void ac_createGameTest() {
		String game = makeParameter("game", "Reallife");
		String password = makeParameter("password", "reallifepw");
		String genre = makeParameter("genre", "Adventure");
		String mastersecret = makeParameter("mastersecret", StaticConfiguration.MASTER_SECRET);

		String method = "addGame";
		String params = appendUrlParameter(game, password, genre, mastersecret);
		testMethod(method, params, getSuccessJSON());
	}
	
	@Test
	public void ad_createGameTest() throws JSONException {
		String game = makeParameter("game", "Reallife");
		String password = makeParameter("password", "reallifepw");
		String genre = makeParameter("genre", "Adventure");
		String mastersecret = makeParameter("mastersecret", StaticConfiguration.MASTER_SECRET);

		String method = "addGame";
		String params = appendUrlParameter(game, password, genre, mastersecret);
		JSONObject expected = new JSONObject().put("code", 28).put("error", "GAME_ALREADY_EXIST");
		testMethod(method, params, expected);
	}
	
	//TEST REMOVE GAME
	@Test
	public void ba_removeGameTest() {
		String game = makeParameter("game", "Reallife");
		String mastersecret = makeParameter("mastersecret", StaticConfiguration.MASTER_SECRET);
		
		String method = "removeGame";
		String params = appendUrlParameter(game, mastersecret);
		testMethod(method, params, getSuccessJSON());
	}
	
	// TEST ADD/REMOVE GAME INSTANCE
	@Test
	public void ca_addGameInstanceTest() {
		String game = makeParameter("game", "Woodment");
		String version = makeParameter("version", "1");
		String password = makeParameter("password", "woodmentpw");
		String description = makeParameter("description", "Explore our world");

		String method = "addGameInstance";
		String params = appendUrlParameter(game, version, password, description);
		testMethod(method, params, getSuccessJSON());
	}
	
	@Test
	public void cb_addGameInstanceTest() {
		String game = makeParameter("game", "Portal");
		String version = makeParameter("version", "0.1b");
		String password = makeParameter("password", "portalpw");
		String description = makeParameter("description", "Die Portal Beta");

		String method = "addGameInstance";
		String params = appendUrlParameter(game, version, password, description);
		testMethod(method, params, getSuccessJSON());
	}
	
	@Test
	public void cc_addGameInstanceTest() throws JSONException {
		String game = makeParameter("game", "Portal");
		String version = makeParameter("version", "0.1b");
		String password = makeParameter("password", "portalpw");
		String description = makeParameter("description", "Die Portal Beta");

		String method = "addGameInstance";
		JSONObject expected = new JSONObject().put("code", 16).put("error", "GAME_VERSION_ALREADY_EXIST");
		String params = appendUrlParameter(game, version, password, description);
		testMethod(method, params, expected);
	}

	@Test
	public void cd_addGameInstanceTest() {
		String game = makeParameter("game", "Portal");
		String version = makeParameter("version", "1.0");
		String password = makeParameter("password", "portalpw");
		String description = makeParameter("description", "Get the job!");

		String method = "addGameInstance";
		String params = appendUrlParameter(game, version, password, description);
		testMethod(method, params, getSuccessJSON());
	}
	
	@Test
	public void ce_addGameInstanceTest() {
		String game = makeParameter("game", "Portal");
		String version = makeParameter("version", "2.0");
		String password = makeParameter("password", "portalpw");
		String description = makeParameter("description", "Become the head!");

		String method = "addGameInstance";
		String params = appendUrlParameter(game, version, password, description);
		testMethod(method, params, getSuccessJSON());
	}
	
	@Test
	public void cf_removeGameInstanceTest() {
		String game = makeParameter("game", "Portal");
		String version = makeParameter("version", "2.0");
		String password = makeParameter("password", "portalpw");

		String method = "removeGameInstance";
		String params = appendUrlParameter(game, version, password);
		testMethod(method, params, getSuccessJSON());
	}
	
	//TEST GET GAME
	@Test
	public void da_getGameTest() throws JSONException{
		String game = makeParameter("game", "Portal");
		String password = makeParameter("password", "portalpw");

		String method = "getGame";
		String params = appendUrlParameter(game, password);
		JSONArray gameArray = new JSONArray();
		gameArray.put(new JSONObject().put("game", "Portal").put("version", "0.1b").put("description", "Die Portal Beta"))
				.put(new JSONObject().put("game", "Portal").put("version", "1.0").put("description", "Get the job!"));
		JSONObject expected = new JSONObject().put("game", "Portal").put("genre", "Role-Playing").put("id", 0).put("instances", gameArray);
		testMethod(method, params, expected);
	}
	
	@Test
	public void db_getGameTest() throws JSONException{
		String game = makeParameter("game", "Woodment");
		String password = makeParameter("password", "woodmentpw");

		String method = "getGame";
		String params = appendUrlParameter(game, password);
		JSONArray gameArray = new JSONArray();
		gameArray.put(new JSONObject().put("game", "Woodment").put("version", "1").put("description", "Explore our world"));
		JSONObject expected = new JSONObject().put("game", "Woodment").put("genre", "Simulation").put("id", 1).put("instances", gameArray);
		testMethod(method, params, expected);
	}
	
	//TEST ADDGAMECONTEXT
	@Test
	public void ea_addGameContextTest() throws UnsupportedEncodingException, JSONException {
		String game = makeParameter("game", "Portal");
		String version = makeParameter("version", "1.0");
		String password = makeParameter("password", "portalpw");
		String contextid = makeParameter("contextid", "airport");
		String name = makeParameter("name", "Am Flughafen");

		String method = "addGameContext";
		String params = appendUrlParameter(game, version, password, contextid, name);
		testMethod(method, params, getSuccessJSON());

		params = "game=Portal&version=1.0&password=portalpw&contextid=meeting&name=" + URLEncoder.encode("Besprechung", "UTF-8");
		testMethod(method, params, getSuccessJSON());
		params = "game=Portal&version=1.0&password=portalpw&contextid=cafeteria&name=" + URLEncoder.encode("Cafeteria", "UTF-8");
		testMethod(method, params, getSuccessJSON());
		params = "game=Portal&version=1.0&password=portalpw&contextid=boss&name=" + URLEncoder.encode("Chef vorstellen", "UTF-8");
		testMethod(method, params, getSuccessJSON());
		params = "game=Portal&version=1.0&password=portalpw&contextid=archive&name=" + URLEncoder.encode("Archiv", "UTF-8");
		testMethod(method, params, getSuccessJSON());
		params = "game=Portal&version=1.0&password=portalpw&contextid=outro&name=" + URLEncoder.encode("Schlussszene", "UTF-8");
		testMethod(method, params, getSuccessJSON());
		
		params = "game=Portal&version=1.0&password=portalpw&contextid=outro&name=" + URLEncoder.encode("Something else 123", "UTF-8");
		JSONObject expected = new JSONObject().put("code", 17).put("error", "SCENE_ALREADY_EXIST");
		testMethod(method, params, expected);

		params = "game=Portal&version=1.0&password=portalpw&contextid=test&name=" + URLEncoder.encode("Testszene", "UTF-8");
		testMethod(method, params, getSuccessJSON());
	}
	
	@Test
	public void eb_addGameContextTest() throws UnsupportedEncodingException, JSONException {
		String method = "addGameContext";
		String params = "game=Woodment&version=1&password=woodmentpw&contextid=start&name=" + URLEncoder.encode("Start", "UTF-8");
		testMethod(method, params, getSuccessJSON());
		params = "game=Woodment&version=1&password=woodmentpw&contextid=scene1&name=" + URLEncoder.encode("Szene 1", "UTF-8");
		testMethod(method, params, getSuccessJSON());
		params = "game=Woodment&version=1&password=woodmentpw&contextid=scene2&name=" + URLEncoder.encode("Szene 2", "UTF-8");
		testMethod(method, params, getSuccessJSON());
		params = "game=Woodment&version=1&password=woodmentpw&contextid=scene3&name=" + URLEncoder.encode("Szene 3", "UTF-8");
		testMethod(method, params, getSuccessJSON());
		params = "game=Woodment&version=1&password=woodmentpw&contextid=scene4&name=" + URLEncoder.encode("Szene 4", "UTF-8");
		testMethod(method, params, getSuccessJSON());
		params = "game=Woodment&version=1&password=woodmentpw&contextid=scene5&name=" + URLEncoder.encode("Szene 5", "UTF-8");
		testMethod(method, params, getSuccessJSON());
		params = "game=Woodment&version=1&password=woodmentpw&contextid=finish&name=" + URLEncoder.encode("Ende", "UTF-8");
		testMethod(method, params, getSuccessJSON());
	}
	
	@Test
	public void ec_removeGameContextTest() throws JSONException{
		String method = "removeGameContext";
		String params = "game=Portal&version=1.0&password=portalpw&contextid=test";
		testMethod(method, params, getSuccessJSON());
		
		params = "game=Portal&version=1.0&password=portalpw&contextid=test";
		JSONObject expected = new JSONObject().put("code", 24).put("error", "SCENE_NOT_FOUND")
				.put("message", "Context=test (gameinstance #2) not found.");
		testMethod(method, params, expected);
	}
	
	//TEST CONTEXT RELATIONS
	@Test
	public void fa_addGameContextRelationTest(){
		String method = "addGameContextRelation";
		String params = "game=Portal&version=1.0&password=portalpw&parent=airport&child=meeting";
		testMethod(method, params, getSuccessJSON());
		params = "game=Portal&version=1.0&password=portalpw&parent=meeting&child=cafeteria";
		testMethod(method, params, getSuccessJSON());
		params = "game=Portal&version=1.0&password=portalpw&parent=meeting&child=boss";
		testMethod(method, params, getSuccessJSON());
		params = "game=Portal&version=1.0&password=portalpw&parent=cafeteria&child=boss";
		testMethod(method, params, getSuccessJSON());
		params = "game=Portal&version=1.0&password=portalpw&parent=boss&child=cafeteria";
		testMethod(method, params, getSuccessJSON());
		params = "game=Portal&version=1.0&password=portalpw&parent=airport&child=meeting";
		testMethod(method, params, getSuccessJSON());
		params = "game=Portal&version=1.0&password=portalpw&parent=boss&child=archive";
		testMethod(method, params, getSuccessJSON());
		params = "game=Portal&version=1.0&password=portalpw&parent=cafeteria&child=archive";
		testMethod(method, params, getSuccessJSON());
		params = "game=Portal&version=1.0&password=portalpw&parent=archive&child=outro";
		testMethod(method, params, getSuccessJSON());
	}
	
	@Test
	public void fb_addGameContextRelationTest(){
		String method = "addGameContextRelation";
		String params = "game=Woodment&version=1&password=woodmentpw&parent=start&child=scene1";
		testMethod(method, params, getSuccessJSON());
		params = "game=Woodment&version=1&password=woodmentpw&parent=scene1&child=scene2";
		testMethod(method, params, getSuccessJSON());
		params = "game=Woodment&version=1&password=woodmentpw&parent=scene1&child=scene3";
		testMethod(method, params, getSuccessJSON());
		params = "game=Woodment&version=1&password=woodmentpw&parent=scene2&child=scene4";
		testMethod(method, params, getSuccessJSON());
		params = "game=Woodment&version=1&password=woodmentpw&parent=scene3&child=scene5";
		testMethod(method, params, getSuccessJSON());
		params = "game=Woodment&version=1&password=woodmentpw&parent=scene3&child=scene4";
		testMethod(method, params, getSuccessJSON());
		params = "game=Woodment&version=1&password=woodmentpw&parent=scene4&child=finish";
		testMethod(method, params, getSuccessJSON());
		params = "game=Woodment&version=1&password=woodmentpw&parent=scene5&child=finish";
		testMethod(method, params, getSuccessJSON());
	}
	
	@Test
	public void fc_addGameContextRelationTest() throws JSONException{
		String method = "addGameContextRelation";
		String params = "game=Woodment&version=1&password=woodmentpw&parent=start&child=scene99";
		JSONObject expected = new JSONObject().put("code", 24).put("error", "SCENE_NOT_FOUND")
				.put("message", "Context=scene99 (gameinstance #0) not found.");
		testMethod(method, params, expected);
		
		params = "game=Woodment&version=1&password=woodmentpw&parent=scene99&child=start";
		testMethod(method, params, expected);
	}
	
	//TEST REMOVE CONTEXT && REMOVE CONTEXT RELATION
	@Test
	public void ga_removeGameContextAndRelationTest() throws JSONException{
		String method = "removeGameContext";
		String params = "game=Woodment&version=1&password=woodmentpw&contextid=start";
		JSONObject expected = new JSONObject().put("code", 27).put("error", "COULD_NOT_DELETE_SCENE")
				.put("message", "Dependency-Violation.");
		testMethod(method, params, expected);
		
		method = "removeGameContextRelation";
		params = "game=Woodment&version=1&password=woodmentpw&parent=start&child=scene1";
		testMethod(method, params, getSuccessJSON());

		method = "removeGameContext";
		params = "game=Woodment&version=1&password=woodmentpw&contextid=start";
		testMethod(method, params, getSuccessJSON());
	}
	
	//TEST GET GAME CONTEXTS
	@Test
	public void gb_getGameContextsTest() throws JSONException {
		String game = makeParameter("game", "Portal");
		String version = makeParameter("version", "1.0");
		String password = makeParameter("password", "portalpw");

		String method = "getGameContexts";
		String params = appendUrlParameter(game, version, password);
		JSONArray contexts = new JSONArray().put(makeContextDescription(false, "airport", "Am Flughafen", new String[]{"meeting"}, new String[]{}))
				.put(makeContextDescription(false, "meeting", "Besprechung", new String[]{"cafeteria", "boss"}, new String[]{"airport"}))
				.put(makeContextDescription(false, "cafeteria", "Cafeteria", new String[]{"boss", "archive"}, new String[]{"meeting", "boss"}))
				.put(makeContextDescription(false, "boss", "Chef vorstellen", new String[]{"archive", "cafeteria"}, new String[]{"meeting", "cafeteria"}))
				.put(makeContextDescription(false, "archive", "Archiv", new String[]{"outro"}, new String[]{"cafeteria", "boss"}))
				.put(makeContextDescription(false, "outro", "Schlussszene", new String[]{}, new String[]{"archive"}));
		JSONObject expected = new JSONObject().put("contexts", contexts);
		testMethod(method, params, expected);
		testMethod("getGameContextRelations", params, expected);
	}
	
	@Test
	public void gc_getGameContextsTest() throws JSONException {
		String game = makeParameter("game", "Woodment");
		String version = makeParameter("version", "1");
		String password = makeParameter("password", "woodmentpw");

		String method = "getGameContexts";
		String params = appendUrlParameter(game, version, password);
		JSONArray contexts = new JSONArray().put(makeContextDescription(false, "scene1", "Szene 1", new String[]{"scene2", "scene3"}, new String[]{}))
				.put(makeContextDescription(false, "scene2", "Szene 2", new String[]{"scene4"}, new String[]{"scene1"}))
				.put(makeContextDescription(false, "scene3", "Szene 3", new String[]{"scene4", "scene5"}, new String[]{"scene1"}))
				.put(makeContextDescription(false, "scene4", "Szene 4", new String[]{"finish"}, new String[]{"scene2", "scene3"}))
				.put(makeContextDescription(false, "scene5", "Szene 5", new String[]{"finish"}, new String[]{"scene3"}))
				.put(makeContextDescription(false, "finish", "Ende", new String[]{}, new String[]{"scene4", "scene5"}));
		JSONObject expected = new JSONObject().put("contexts", contexts);
		testMethod(method, params, expected);
		testMethod("getGameContextRelations", params, expected);
	}
	
	private JSONObject makeContextDescription(boolean autogen, String ident, String name, String[] to,
			String[] from) throws JSONException {
		JSONObject json = new JSONObject().put("autogenerated", autogen).put("ident", ident).put("name",name)
				.put("next", new JSONArray(to)).put("previous", new JSONArray(from));
		return json;
	}
	
	//TEST SET GAME & GAME CONTEXT DESCRIPTION
	@Test
	public void ha_setGameInstanceDescriptionTest(){
		String method = "setGameInstanceDescription";
		String gameP = makePostParameter("game", "Woodment");
		String passwordP = makePostParameter("password", "woodmentpw");
		String versionP = makePostParameter("gameversion", "1");
		String params = gameP + "," + passwordP + "," + versionP;
		
		String veryLongDescription = getIpsum(3000);
		InputStream is = new ByteArrayInputStream(veryLongDescription.getBytes());
		testPOSTMethod(method, params, is, getSuccessJSON());
	}
	
	@Test
	public void hb_getGameTest() throws JSONException{
		String game = makeParameter("game", "Woodment");
		String password = makeParameter("password", "woodmentpw");

		String method = "getGame";
		String params = appendUrlParameter(game, password);
		JSONArray gameArray = new JSONArray();
		gameArray.put(new JSONObject().put("game", "Woodment").put("version", "1").put("description",  getIpsum(3000)));
		JSONObject expected = new JSONObject().put("game", "Woodment").put("genre", "Simulation").put("id", 1).put("instances", gameArray);
		System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		testMethod(method, params, expected);
	}
	
	@Test
	public void hc_setGameContextDescriptionTest() throws UnsupportedEncodingException{
		String method = "setGameContextDescription";
		String gameP = makePostParameter("game", "Woodment");
		String passwordP = makePostParameter("password", "woodmentpw");
		String versionP = makePostParameter("gameversion", "1");
		String contextidP = makePostParameter("contextid", "scene1");
		String params = gameP + "," + passwordP + "," + versionP + "," + contextidP;
		
		String veryLongDescription = "Dies ist eine sehr lange Beschreibung für eine szene die eigentlich gar nicht existiert, aber wenn sie existieren würde wäre sie bestimmt sehr interessant und jeder würde sie lesen wollen. Schade also dass sie nicht existiert..Nichts desto trotz muss hier eine schöne, etwas längere Testbeschreibung hin. Ein abschließendes Ipsum spare ich mir ignorierend dass die Beschreibung dann nicht überaus lang wird.";

		InputStream is = new ByteArrayInputStream(veryLongDescription.getBytes());
		testPOSTMethod(method, params, is, getSuccessJSON());
	}

	@Test
	public void hd_getGameContextsTest() throws JSONException {
		String game = makeParameter("game", "Woodment");
		String version = makeParameter("version", "1");
		String password = makeParameter("password", "woodmentpw");
		String contextid = makeParameter("contextid", "scene1");
		
		String veryLongDescription = "Dies ist eine sehr lange Beschreibung für eine szene die eigentlich gar nicht existiert, aber wenn sie existieren würde wäre sie bestimmt sehr interessant und jeder würde sie lesen wollen. Schade also dass sie nicht existiert..Nichts desto trotz muss hier eine schöne, etwas längere Testbeschreibung hin. Ein abschließendes Ipsum spare ich mir ignorierend dass die Beschreibung dann nicht überaus lang wird.";

		String method = "getGameContext";
		String params = appendUrlParameter(game, version, password, contextid);
		//{"context": {"previous":[],"ident":"scene1","autogenerated":false,"next":["scene2","scene3"],"name":"Szene 1"}}
		JSONObject context = new JSONObject().put("previous", new JSONArray()).put("ident", "scene1")
				.put("autogenerated", false).put("name", "Szene 1").put("next", new JSONArray().put("scene2").put("scene3"))
				.put("description", veryLongDescription);
		JSONObject expected = new JSONObject().put("context", context);
		testMethod(method, params, expected);
	}
	
	// SET IMAGES TEST
	@Test
	public void ia_setGameInstanceImageTest(){		
		String params = makePostParameter("password", "portalpw") + ","	+ makePostParameter("game", "Portal") + "," 
				+ makePostParameter("gameversion", "1.0") + ","	+ makePostParameter("extension", "png");
		String method = "setGameInstanceImage";
		File f = new File("data/portal.png");
		if(!f.exists()) fail(f.getAbsolutePath() + " does not exist.");
		testPOSTMethod(method, params, f, getSuccessJSON());
	}
	
	

	/*
	 * Tests:
de.tud.kom.socom.components.game.GameManager.setGameInstanceImage(SocomRequest)
de.tud.kom.socom.components.game.GameManager.setGameContextImage(SocomRequest)
	 */

	@Override
	public String getComponentUrl() {
		return GAME_URL_FRAGMENT;
	}
}
