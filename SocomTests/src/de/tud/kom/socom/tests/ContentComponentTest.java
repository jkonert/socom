package de.tud.kom.socom.tests;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import de.tud.kom.socom.tests.util.ConnectionHandler;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ContentComponentTest extends ComponentTest {

	/**
	 * 
	 * de.tud.kom.socom.components.content.ContentManager.createUserContent(SocomRequest)
de.tud.kom.socom.components.content.ContentManager.createGameContent(SocomRequest)
de.tud.kom.socom.components.content.ContentManager.uploadContent(SocomRequest)
	 * 
	 */
	
	
	// CREA & UPLOAD CONTENT TEST
	
	@Test
	public void aa_createContentTest() {
		String contextid = makeParameter("contextid", "airport");
		String title = makeParameter("title", "Example Content");
		String description = makeParameter("description", "Eclipse Project XML-File");
		String type = makeParameter("type", "binary");
		String visibility = makeParameter("visibility", "2");
		String category = makeParameter("category", "information");

		String params = appendUrlParameter(contextid, title, description, type, visibility, category);
		String method = "createUserContent";
		testMethod(method, params, getSuccessJSON());
		// contentident saved in cookies
	}

	@Test
	public void ab_uploadGameContentTest() throws JSONException {
		String method = "uploadContent";
		JSONObject expected = new JSONObject().put("contentid", 0);
		File content = new File(".project");
		if (!content.exists()) {
			fail(content.getAbsolutePath() + " does not exist");
		}
		testPOSTMethod(method, "", content, expected);

		String cookie = ConnectionHandler.getCookie();
		cookie = cookie.split(",")[0];
		ConnectionHandler.setCookie(cookie);
	}

	@Test
	public void ac_createContentTest() {
		String contextid = makeParameter("contextid", "meeting");
		String title = makeParameter("title", "Creative Commons Bild");
		String description = makeParameter("description", "Beschreibung des Beispielbildes welches natürlich frei benutzt werden darf.");
		String type = makeParameter("type", "image");
		String visibility = makeParameter("visibility", "2");
		String category = makeParameter("category", "information");

		String params = appendUrlParameter(contextid, title, description, type, visibility, category);
		String method = "createUserContent";
		testMethod(method, params, getSuccessJSON());
		// contentident saved in cookies
	}

	@Test
	public void ad_uploadGameContentTest() throws JSONException {
		String method = "uploadContent";
		JSONObject expected = new JSONObject().put("contentid", 1);
		File content = new File("creative_commons.jpg");
		if (!content.exists()) {
			fail(content.getAbsolutePath() + " does not exist");
		}
		testPOSTMethod(method, "", content, expected);

		String cookie = ConnectionHandler.getCookie();
		cookie = cookie.split(",")[0];
		ConnectionHandler.setCookie(cookie);
	}

	@Test
	public void ae_createContentTest() {
		String contextid = makeParameter("contextid", "meeting");
		String title = makeParameter("title", "Nicht-so-toller Song");
		String description = makeParameter("description", "Beschreibung des Beispielliedes welches ich noch nicht gehört habe. Dafür war es frei.");
		String type = makeParameter("type", "audio");
		String visibility = makeParameter("visibility", "2");
		String category = makeParameter("category", "information");

		String params = appendUrlParameter(contextid, title, description, type, visibility, category, "interpret=aer006&songtitle=the%20great%20alain");
		String method = "createUserContent";
		testMethod(method, params, getSuccessJSON());
		// contentident saved in cookies
	}

	@Test
	public void af_uploadGameContentTest() throws JSONException {
		String method = "uploadContent";
		JSONObject expected = new JSONObject().put("contentid", 2);
		File content = new File("creativecommonslied1.mp3");
		if (!content.exists()) {
			fail(content.getAbsolutePath() + " does not exist");
		}
		testPOSTMethod(method, "", content, expected);

		String cookie = ConnectionHandler.getCookie();
		cookie = cookie.split(",")[0];
		ConnectionHandler.setCookie(cookie);
	}
	
	@Test
	public void ag_createGameContentTest() {
		String contextid = makeParameter("contextid", "archive");
		String title = makeParameter("title", "Ipsum");
		String description = makeParameter("description", getIpsum(30));
		String type = makeParameter("type", "text");
		String category = makeParameter("category", "information");

		String params = appendUrlParameter(contextid, title, description, type, category);
		String method = "createGameContent";
		testMethod(method, params, getSuccessJSON());
		// contentident saved in cookies
	}

	@Test
	public void ah_uploadGameContentTest() throws JSONException, UnsupportedEncodingException {
		String method = "uploadContent";
		JSONObject expected = new JSONObject().put("contentid", 3);
		
		byte[] ipsumBytes = getIpsum(1000).getBytes("UTF-8");
		testPOSTMethod(method, "", new ByteArrayInputStream(ipsumBytes), expected);
		
		String cookie = ConnectionHandler.getCookie();
		cookie = cookie.split(",")[0];
		ConnectionHandler.setCookie(cookie);
	}
	
	/**
	 * 
de.tud.kom.socom.components.content.ContentManager.getContentInfoForContext(SocomRequest)
de.tud.kom.socom.components.content.ContentManager.getContentInfo(SocomRequest)
de.tud.kom.socom.components.content.ContentManager.downloadContent(SocomRequest)
	 */
	
	//GET CONTENT INFO & DOWNLOAD TEST
	
	@Test
	public void ba_getContentInfoForContextTest() throws JSONException {
		String contextid = makeParameter("context", "airport");

		String method = "getContentInfoForContext";
		JSONArray contentArray = new JSONArray().put(new JSONObject().put("category", "INFORMATION").put("comments", new JSONArray()).put("contentid", 0)
				.put("contextid", 0).put("description", "Eclipse Project XML-File").put("hits", 0).put("metadata", new JSONObject()).put("owner", "TestUser01")
				.put("ownerid", 0).put("rating", -1).put("ratingCount", 0).put("title", "Example Content").put("type", "binary").put("usersRating", -1));
		// no timestamp added
		JSONObject expected = new JSONObject().put("content", contentArray);
		testMethod(method, contextid, expected);
	}

	@Test
	public void bb_downloadContentTest() {
		String id = makeParameter("contentid", "0");
		String method = "downloadContent";

		String answer = ConnectionHandler.sendGETRequestGetAnswer(generateUrl(method, id));
		assertTrue(answer.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?><projectDescription>"));
	}
	
	@Test
	public void bc_getContentInfoTest() throws JSONException {
		String method = "getContentInfo";
		JSONObject binary = new JSONObject().put("category", "INFORMATION").put("comments", new JSONArray()).put("contentid", 0)
				.put("contextid", 0).put("description", "Eclipse Project XML-File").put("hits", 1).put("metadata", new JSONObject()).put("owner", "TestUser01")
				.put("ownerid", 0).put("rating", -1).put("ratingCount", 0).put("title", "Example Content").put("type", "binary").put("usersRating", -1);
		
		JSONObject text = new JSONObject().put("category", "INFORMATION").put("comments", new JSONArray()).put("contentid", 3)
				.put("contextid", 4).put("description", getIpsum(30)).put("hits", 0).put("metadata", new JSONObject()).put("owner", "TestUser01")
				.put("ownerid", 0).put("rating", -1).put("ratingCount", 0).put("title", "Ipsum").put("type", "text").put("usersRating", -1);
		
		JSONObject audio = new JSONObject().put("category", "INFORMATION").put("comments", new JSONArray()).put("contentid", 2)
				.put("contextid", 1).put("description", "Beschreibung des Beispielliedes welches ich noch nicht gehört habe. Dafür war es frei.").put("hits", 0).put("metadata", new JSONObject()).put("owner", "TestUser01")
				.put("ownerid", 0).put("rating", -1).put("ratingCount", 0).put("title", "Nicht-so-toller Song").put("type", "audio").put("usersRating", -1);
		
		JSONObject image = new JSONObject().put("category", "INFORMATION").put("comments", new JSONArray()).put("contentid", 1)
				.put("contextid", 1).put("description", "Beschreibung des Beispielbildes welches natürlich frei benutzt werden darf.").put("hits", 0).put("metadata", new JSONObject()).put("owner", "TestUser01")
				.put("ownerid", 0).put("rating", -1).put("ratingCount", 0).put("title", "Creative Commons Bild").put("type", "image").put("usersRating", -1);
		
		JSONArray contentArray = new JSONArray().put(binary).put(text).put(image).put(audio);
		// no timestamp added
		JSONObject expected = new JSONObject().put("content", contentArray);
		testMethod(method, "", expected);
	}
	
	@Test
	public void bd_downloadContentTest() throws UnsupportedEncodingException {
		String id = makeParameter("contentid", "3");
		String method = "downloadContent";

		byte[] answer = ConnectionHandler.sendGETRequestGetAnswerAsBytes(generateUrl(method, id));
		byte[] expected = getIpsum(1000).getBytes();
		assertEquals(expected.length, answer.length);
		
		for(int i = 0; i < expected.length; i++) {
			assertEquals(expected[i], answer[i]);
		}
		
		assertEquals(new String(expected, "UTF-8"), new String(answer, "UTF-8"));
	}

	
	/**
	 * de.tud.kom.socom.components.content.ContentManager.rateContent(SocomRequest)
de.tud.kom.socom.components.content.ContentManager.addComment(SocomRequest)
de.tud.kom.socom.components.content.ContentManager.deleteComment(SocomRequest)
	 * @throws JSONException 
	 */
	
	@Test
	public void ca_rateContentTest() throws JSONException{
		String method = "rateContent";
		String params = makeParams("contentid", "0", "rating", "0.5");
		testMethod(method, params, getSuccessJSON());
		
		params = makeParams("contentid", "0", "rating", "0.25");
		testMethod(method, params, getSuccessJSON());
		
		params = makeParams("contentid", "1");
		params += "&rating=2,75";
		JSONObject expected = new JSONObject().put("code", 4).put("error", "UNEXPECTED_OR_MISSING_PARAMETER").put("message", "Parameter rating (of type double between 0 and 1) has wrong format or is missing.");
		testMethod(method, params, expected);
		
		params = makeParams("contentid", "1");
		params += "&rating=0,75";
		testMethod(method, params, getSuccessJSON());
	}
	
	@Test
	public void cb_getContentInfoWithRatingTest() throws JSONException {
		String method = "getContentInfo";
		String params = makeParams("contexts", "airport,meeting");
		JSONObject binary = new JSONObject().put("category", "INFORMATION").put("comments", new JSONArray()).put("contentid", 0)
				.put("contextid", 0).put("description", "Eclipse Project XML-File").put("hits", 1).put("metadata", new JSONObject()).put("owner", "TestUser01")
				.put("ownerid", 0).put("rating", 0.25).put("ratingCount", 1).put("title", "Example Content").put("type", "binary").put("usersRating", 0.25);
		
		JSONObject image = new JSONObject().put("category", "INFORMATION").put("comments", new JSONArray()).put("contentid", 1)
				.put("contextid", 1).put("description", "Beschreibung des Beispielbildes welches natürlich frei benutzt werden darf.").put("hits", 0).put("metadata", new JSONObject()).put("owner", "TestUser01")
				.put("ownerid", 0).put("rating", 0.75).put("ratingCount", 1).put("title", "Creative Commons Bild").put("type", "image").put("usersRating", 0.75);
		
		JSONObject audio = new JSONObject().put("category", "INFORMATION").put("comments", new JSONArray()).put("contentid", 2)
				.put("contextid", 1).put("description", "Beschreibung des Beispielliedes welches ich noch nicht gehört habe. Dafür war es frei.").put("hits", 0).put("metadata", new JSONObject()).put("owner", "TestUser01")
				.put("ownerid", 0).put("rating", -1).put("ratingCount", 0).put("title", "Nicht-so-toller Song").put("type", "audio").put("usersRating", -1);
		
		
		JSONArray contentArray = new JSONArray().put(binary).put(image).put(audio);
		// no timestamp added
		JSONObject expected = new JSONObject().put("content", contentArray);
		testMethod(method, params, expected);
	}
	
	
	@Override
	public String getComponentUrl() {
		return "content";
	}
}
