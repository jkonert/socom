package de.tud.kom.socom.tests;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserComponentTest extends ComponentTest {

	// A:
	// de.tud.kom.socom.components.user.UserManager.createUser(SocomRequest)
	// {de.tud.kom.socom.components.user.UserManager.createUserWithSocialNetwork(SocomRequest)}
	//
	// de.tud.kom.socom.components.user.UserManager.loginUser(SocomRequest)
	// de.tud.kom.socom.components.user.UserManager.logout(SocomRequest)
	//
	// de.tud.kom.socom.components.user.UserManager.changeUsername(SocomRequest)
	// de.tud.kom.socom.components.user.UserManager.changeUserPassword(SocomRequest)
	// de.tud.kom.socom.components.user.UserManager.getUser(SocomRequest)
	// de.tud.kom.socom.components.user.UserManager.deleteUser(SocomRequest)
	@Test
	public void aa_createUserTest() throws JSONException {
		String username = makeParameter("username", "TestUser01");
		String password = makeParameter("password", "test12");
		String game = makeParameter("game", "Portal");
		String gamepassword = makeParameter("gamepassword", "portalpw");
		String version = makeParameter("version", "1.0");
		String visibility = makeParameter("visibility", "2");

		String params = appendUrlParameter(username, password, game, gamepassword, version, visibility);
		String method = "createUser";
		JSONObject expected = new JSONObject().put("uid", 0);
		testMethod(method, params, expected);
	}

	@Test
	public void ab_logoutTest() {
		String method = "logout";
		testMethod(method, "", getSuccessJSON());
	}

	@Test
	public void ac_createUserTest2() throws JSONException {
		String params = makeParams("username", "TestUser02", "password", "test12", "game", "Portal", "gamepassword",
				"portalpw", "version", "1.0", "visibility", "2");
		String method = "createUser";
		JSONObject expected = new JSONObject().put("uid", 1);
		testMethod(method, params, expected);

		ab_logoutTest();

		params = makeParams("username", "Somebody", "password", "some", "game", "Portal", "gamepassword", "portalpw",
				"version", "1.0", "visibility", "2");
		method = "createUser";
		expected = new JSONObject().put("uid", 2);
		testMethod(method, params, expected);

		ab_logoutTest();

		params = makeParams("username", "TestUser03", "password", "test13", "game", "Woodment", "gamepassword",
				"woodmentpw", "version", "1", "visibility", "0");
		method = "createUser";
		expected = new JSONObject().put("uid", 3);
		testMethod(method, params, expected);

		ab_logoutTest();
	}

	@Test
	public void ad_loginUserTest() throws JSONException {
		String params = makeParams("username", "Somebody", "password", "some", "game", "Woodment", "gamepassword",
				"woodmentpw", "version", "1");
		String method = "loginUser";
		JSONObject expected = new JSONObject().put("uid", 2);
		testMethod(method, params, expected);
	}

	@Test
	public void ae_getNotExistingUserTest() throws JSONException {
		String params = makeParameter("id", "99");
		String method = "getUser";
		JSONObject expected = new JSONObject().put("error", "USER_NOT_FOUND").put("code", 7)
				.put("message", "User with ID=99 not found.");
		testMethod(method, params, expected);
	}

	@Test
	public void af_changeUserNameAndPasswordTest() throws JSONException {
		String params = makeParams("username", "Somebodyelse", "password", "some");
		String method = "changeUsername";
		testMethod(method, params, getSuccessJSON());

		params = makeParams("password", "some", "newpassword", "someother");
		method = "changeUserPassword";
		testMethod(method, params, getSuccessJSON());

		ab_logoutTest();

		params = makeParams("username", "Somebody", "password", "some", "game", "Woodment", "gamepassword",
				"woodmentpw", "version", "1");
		method = "loginUser";
		JSONObject expected = new JSONObject().put("code", 5).put("error", "USER_NOT_VALID")
				.put("message", "User not authenticated.");
		testMethod(method, params, expected);

		params = makeParams("username", "Somebodyelse", "password", "someother", "game", "Woodment", "gamepassword",
				"woodmentpw", "version", "1");
		method = "loginUser";
		expected = new JSONObject().put("uid", 2);
		testMethod(method, params, expected);
	}

	@Test
	public void ag_deleteUserTest() throws JSONException {
		String params = makeParameter("password", "someother");
		String method = "deleteUser";
		testMethod(method, params, getSuccessJSON());

		params = makeParams("username", "Somebodyelse", "password", "someother", "game", "Woodment", "gamepassword",
				"woodmentpw", "version", "1");
		method = "loginUser";
		JSONObject expected = new JSONObject().put("code", 33).put("error", "USER_OR_CONTENT_DELETED");
		testMethod(method, params, expected);
	}

	@Test
	public void ah_getUserTest() throws JSONException {
		String params = makeParams("username", "TestUser02", "password", "test12", "game", "Portal", "gamepassword",
				"portalpw", "version", "1.0");
		String method = "loginUser";
		JSONObject expected = new JSONObject().put("uid", 1);
		testMethod(method, params, expected);

		params = "";
		method = "getUser";
		expected = new JSONObject().put("commentCount", 0).put("contentCount", 0).put("game", "Portal 1.0")
				.put("gameid", 2).put("isVisible", true).put("name", "TestUser02").put("ratingsCount", 0)
				.put("state", "Spielt").put("uid", 1);
		testMethod(method, params, expected);
		
		params = "id=0";
		expected = new JSONObject().put("commentCount", 0).put("contentCount", 0).put("game", "Portal 1.0")
				.put("gameid", 2).put("isVisible", true).put("name", "TestUser01").put("ratingsCount", 0)
				.put("state", "Offline").put("uid", 0);
		testMethod(method, params, expected);
		
		params = "id=3";
		expected = new JSONObject().put("code", 19).put("error", "ILLEGAL_ACCESS");
		testMethod(method, params, expected);

		ab_logoutTest();

		params = makeParams("username", "TestUser01", "password", "test12", "game", "Woodment", "gamepassword",
				"woodmentpw", "version", "1");
		method = "loginUser";
		expected = new JSONObject().put("uid", 0);
		testMethod(method, params, expected);
		
		
	}

	// B :
	// de.tud.kom.socom.components.user.UserManager.becomeAdmin(SocomRequest)

	@Test
	public void ba_becomeAdminTest() {
		String params = makeParams("password", "test12", "mastersecret", "123a45b6");
		String method = "becomeAdmin";
		testMethod(method, params, getSuccessJSON());
	}

	// C:
	// de.tud.kom.socom.components.user.UserManager.getUsersGames(SocomRequest)
	// de.tud.kom.socom.components.user.UserManager.setCurrentContext(SocomRequest)
	// de.tud.kom.socom.components.user.UserManager.getVisitedContexts(SocomRequest)
	//
	// de.tud.kom.socom.components.user.UserManager.addTimePlayed(SocomRequest)
	// de.tud.kom.socom.components.user.UserManager.resetTimePlayed(SocomRequest)
	// de.tud.kom.socom.components.user.UserManager.setTimePlayed(SocomRequest)
	// de.tud.kom.socom.components.user.UserManager.getTimePlayed(SocomRequest)

	@Test
	public void ca_getUsersGamesTest() throws JSONException {
		String params = "";
		String method = "getUsersGames";
		JSONObject portal = new JSONObject().put("game", "Portal").put("description", "Get the job!")
				.put("version", "1.0");
		JSONObject woodment = new JSONObject().put("game", "Woodment").put("description", getIpsum(3000))
				.put("version", "1");
		JSONObject expected = new JSONObject().put("games", new JSONArray().put(portal).put(woodment));
		testMethod(method, params, expected);
	}

	@Test
	public void cb_setCurrentContextTest() {
		String params = "context=scene1";
		String method = "setCurrentContext";
		testMethod(method, params, getSuccessJSON());
	}

	@Test
	public void cc_addTimePlayedTest() throws JSONException {
		String params = "time=1000";
		String method = "addTimePlayed";
		testMethod(method, params, getSuccessJSON());

		params = "";
		method = "getTimePlayed";
		JSONObject expected = new JSONObject().put("timeplayed", 1000);
		testMethod(method, params, expected);
	}

	@Test
	public void cd_setCurrentContextAndChangeTimePlayedTest() throws JSONException {
		testMethod("setCurrentContext", "context=scene2", getSuccessJSON());
		testMethod("addTimePlayed", "time=800", getSuccessJSON());
		testMethod("setCurrentContext", "context=scene4", getSuccessJSON());
		testMethod("addTimePlayed", "time=1600", getSuccessJSON());
		testMethod("setCurrentContext", "context=finish", getSuccessJSON());
		testMethod("addTimePlayed", "time=1000", getSuccessJSON());

		testMethod("getTimePlayed", "", new JSONObject().put("timeplayed", 1000));
		testMethod("setTimePlayed", "time=2000", getSuccessJSON());
		testMethod("getTimePlayed", "", new JSONObject().put("timeplayed", 2000));
		testMethod("resetTimePlayed", "", getSuccessJSON());
		testMethod("getTimePlayed", "", new JSONObject().put("timeplayed", 0));
		testMethod("addTimePlayed", "time=1000", getSuccessJSON());
		testMethod("addTimePlayed", "time=50", getSuccessJSON());
		testMethod("getTimePlayed", "", new JSONObject().put("timeplayed", 1050));
	}

	@Test
	public void ce_setCurrentContextAndChangeTimePlayedTest2() throws JSONException {
		ab_logoutTest();
		String params = makeParams("username", "TestUser02", "password", "test12", "game", "Portal", "gamepassword",
				"portalpw", "version", "1.0");
		String method = "loginUser";
		JSONObject expected = new JSONObject().put("uid", 1);
		testMethod(method, params, expected);

		testMethod("setCurrentContext", "context=airport", getSuccessJSON());
		testMethod("addTimePlayed", "time=123", getSuccessJSON());
		testMethod("setCurrentContext", "context=meeting", getSuccessJSON());
		testMethod("addTimePlayed", "time=456", getSuccessJSON());
		testMethod("setCurrentContext", "context=cafeteria", getSuccessJSON());
		testMethod("addTimePlayed", "time=1263", getSuccessJSON());
		testMethod("setCurrentContext", "context=archive", getSuccessJSON());
		testMethod("addTimePlayed", "time=1212", getSuccessJSON());
	}

	@Test
	public void cf_getVisitedContextsTest() throws JSONException {
		String params = "";
		String method = "getVisitedContexts";
		JSONObject airport = new JSONObject().put("externalid", "airport").put("name", "Am Flughafen").put("time", 123);
		JSONObject meeting = new JSONObject().put("externalid", "meeting").put("name", "Besprechung").put("time", 456);
		JSONObject cafeteria = new JSONObject().put("externalid", "cafeteria").put("name", "Cafeteria")
				.put("time", 1263);
		JSONObject archive = new JSONObject().put("externalid", "archive").put("name", "Archiv").put("time", 1212);
		JSONObject expected = new JSONObject().put("contexts", new JSONArray().put(airport).put(meeting).put(cafeteria)
				.put(archive));
		testMethod(method, params, expected);
	}

	@Test
	public void cg_setCurrentContextAndChangeTimePlayedTest3() throws JSONException {
		ab_logoutTest();
		String params = makeParams("username", "TestUser01", "password", "test12", "game", "Portal", "gamepassword",
				"portalpw", "version", "1.0");
		String method = "loginUser";
		JSONObject expected = new JSONObject().put("uid", 0);
		testMethod(method, params, expected);

		testMethod("setCurrentContext", "context=airport", getSuccessJSON());
		testMethod("addTimePlayed", "time=800", getSuccessJSON());
		testMethod("setCurrentContext", "context=meeting", getSuccessJSON());
		testMethod("addTimePlayed", "time=1600", getSuccessJSON());
		testMethod("setCurrentContext", "context=boss", getSuccessJSON());
		testMethod("addTimePlayed", "time=6456", getSuccessJSON());
		testMethod("setCurrentContext", "context=cafeteria", getSuccessJSON());
		testMethod("addTimePlayed", "time=3126", getSuccessJSON());
		testMethod("setCurrentContext", "context=archive", getSuccessJSON());
		testMethod("addTimePlayed", "time=212", getSuccessJSON());
		testMethod("setCurrentContext", "context=outro", getSuccessJSON());
		testMethod("addTimePlayed", "time=1060", getSuccessJSON());
	}

	@Test
	public void ch_getVisitedContextsTest2() throws JSONException {
		String params = "";
		String method = "getVisitedContexts";
		JSONObject airport = new JSONObject().put("externalid", "airport").put("name", "Am Flughafen").put("time", 800);
		JSONObject meeting = new JSONObject().put("externalid", "meeting").put("name", "Besprechung").put("time", 1600);
		JSONObject boss = new JSONObject().put("externalid", "boss").put("name", "Chef vorstellen").put("time", 6456);
		JSONObject cafeteria = new JSONObject().put("externalid", "cafeteria").put("name", "Cafeteria")
				.put("time", 3126);
		JSONObject archive = new JSONObject().put("externalid", "archive").put("name", "Archiv").put("time", 212);
		JSONObject outro = new JSONObject().put("externalid", "outro").put("name", "Schlussszene").put("time", 1060);
		JSONObject expected = new JSONObject().put("contexts",
				new JSONArray().put(airport).put(meeting).put(boss).put(cafeteria).put(archive).put(outro));
		testMethod(method, params, expected);
	}

	// D :
	// de.tud.kom.socom.components.user.UserManager.addJournalEntry(SocomRequest)
//	de.tud.kom.socom.components.user.UserManager.addLog(SocomRequest)
	// de.tud.kom.socom.components.user.UserManager.getJournalEntries(SocomRequest)
	//	
	// de.tud.kom.socom.components.user.UserManager.createMetadata(SocomRequest)
	// de.tud.kom.socom.components.user.UserManager.updateMetadata(SocomRequest)
	// de.tud.kom.socom.components.user.UserManager.deleteMetadata(SocomRequest)
	// de.tud.kom.socom.components.user.UserManager.getMetadata(SocomRequest)

	@Test
	public void da_addLogTest(){
		String method = "addLog";
		String params = makeParams("type","INFO","message","Das Spiel wurde gestartet");
		testMethod(method, params, getSuccessJSON());
		params = makeParams("type","INFO","message","Der Spieler erreicht das zweite Level");
		testMethod(method, params, getSuccessJSON());
		params = makeParams("type","INFO","message","Das Spieler wurde befördert");
		testMethod(method, params, getSuccessJSON());
		params = makeParams("type","error","message","Das Spiel ist abgestürzt");
		testMethod(method, params, getSuccessJSON());
	}
	
	@Test
	public void db_addJournalEntriesTest(){
		String method = "addJournalEntry";
		String params = makeParams("type","HELP_REQUEST","message","Der Spieler weiß nicht wo das Archiv ist");
		testMethod(method, params, getSuccessJSON());
		params = makeParams("type","finish_level","message","Der Spieler hat das zweite Level geschafft");
		testMethod(method, params, getSuccessJSON());
	}
	
	@Test
	public void dc_getLogsTest() throws JSONException{
		String method = "getLogs";
		String params = "gamepassword=portalpw";
		JSONObject l1 = new JSONObject().put("message", "Das Spiel wurde gestartet").put("type", "INFO");
		JSONObject l2 = new JSONObject().put("message", "Der Spieler erreicht das zweite Level").put("type", "INFO");
		JSONObject l3 = new JSONObject().put("message", "Das Spieler wurde befördert").put("type", "INFO");
		JSONObject l4 = new JSONObject().put("message", "Das Spiel ist abgestürzt").put("type", "ERROR");
		JSONObject expected = new JSONObject().put("logs", new JSONArray().put(l1).put(l2).put(l3).put(l4));
		testMethod(method, params, expected);
	}
	
	@Test
	public void dd_getJournalEntriesTest() throws JSONException{
		String method = "getJournalEntries";
		String params = "";
		JSONObject l1 = new JSONObject().put("message", "Der Spieler weiß nicht wo das Archiv ist").put("type", "HELP_REQUEST");
		JSONObject l2 = new JSONObject().put("message", "Der Spieler hat das zweite Level geschafft").put("type", "FINISH_LEVEL");
		JSONObject expected = new JSONObject().put("logs", new JSONArray().put(l1).put(l2));
		testMethod(method, params, expected);
	}
	
	//TODO filter-tests
	
	//
	// @Test
	// public void g_addJournalEntryTest() {
	// String type = makeParameter("type", "Error");
	// String message = makeParameter("message", "foo bar");
	//
	// String params = appendUrlParameter(type, message);
	// String method = "addJournalEntry";
	// testMethod(method, params, getSuccessJSON());
	// }

	@Override
	public String getComponentUrl() {
		return "user";
	}
}
