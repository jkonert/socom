package de.tud.kom.socom.tests;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import org.skyscreamer.jsonassert.JSONAssert;

import de.tud.kom.socom.tests.util.ConnectionHandler;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public abstract class ComponentTest {

	void testMethod(String method, String params, JSONObject expected) {
		String url = generateUrl(method, params);
		JSONObject result = ConnectionHandler.sendGETRequest(url);
		try {
			System.out.println(method + " ("+params+"): \n" + (result == null ? "null" : result.toString(1)) + " \n");
//			System.out.println("compared to: \n" + expected.toString(1));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		assertJSON(expected, result);
	}
	
	void testPOSTMethod(String method, String params, File file, JSONObject expected) {
		String url = generateUrl(method, "");
		JSONObject result;
		try {
			result = ConnectionHandler.sendMessagePOSTRequest(url, params, new FileInputStream(file));
			assertJSON(expected, result);
			System.out.println(method + " ("+params+"): \n" + (result == null ? "null" : result.toString(1)) + " \n");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JSONException e) {
		e.printStackTrace();
		}
	}
	
	void testPOSTMethod(String method, String params, InputStream is, JSONObject expected) {
		String url = generateUrl(method, "");
		JSONObject result = ConnectionHandler.sendMessagePOSTRequest(url, params, is);
		assertJSON(expected, result);
	}
	
	public static void assertJSON(JSONObject expected, JSONObject actual) {
		try {
			JSONAssert.assertEquals(expected, actual, false);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	static String makeParameter(String key, String value){
		try {
			return key + "=" + URLEncoder.encode(value, "UTF-8") + "&";
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}
	
	static String makeParams(String ... params){
		try {
			String r = "";
			for(int i = 0; i < params.length; i+=2) {
				r += params[i] + "=" + URLEncoder.encode(params[i+1], "UTF-8") + "&";
			}
			return r;
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return null;
		}
	}
	
	static String makePostParameter(String key, String value) {
		try {
			return key + "=" + URLEncoder.encode(value, "UTF-8") + ",";
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}
	
	static String appendUrlParameter(String ... params){
		StringBuffer sb =  new StringBuffer();
		for(String param : params){
			sb.append(param);
		}
		return sb.toString();
	}
	

	JSONObject getSuccessJSON() {
		try {
			return new JSONObject().put("success", true);
		} catch (JSONException e) {
			fail(e.getMessage());
			return null;
		}
	}
	
	String generateUrl(String method, String params){
			return getComponentUrl() + "/" + method + "?" + params;
	}

	public abstract String getComponentUrl();
	
	
	static String getIpsum(int length) {
		String ipsum = "Consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet." +
				"Duis autem vel eum iriure dolor in hendrerit in vulputate velit esse molestie consequat, vel illum dolore eu feugiat nulla facilisis at vero eros et accumsan et iusto " +
				"Consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. ";
		return ipsum.substring(0, Math.min(ipsum.length(), length));
	}
}

