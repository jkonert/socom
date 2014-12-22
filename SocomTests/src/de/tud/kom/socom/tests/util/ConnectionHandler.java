package de.tud.kom.socom.tests.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class ConnectionHandler {

	private static String cookie = "";

	public static String getCookie() {
		return cookie;
	}

	public static void setCookie(String c) {
		cookie = c;
	}

	public static JSONObject sendGETRequest(String urlS) {
		String answer = sendGETRequestGetAnswer(urlS);
		try {
			return new JSONObject(answer);
		} catch (JSONException e) {
			System.err.println("Error parsing: " + answer);
			return null;
		}
	}

	public static String sendGETRequestGetAnswer(String urlS) {
		try {
			String encodedUrl = StaticConfiguration.SOCOM_API_URL + urlS;
			URL url = new URL(encodedUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			if (!cookie.isEmpty())
				conn.addRequestProperty("cookie", cookie);
			conn.connect();

			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new Exception("Wrong Repsonse-Code: " + conn.getResponseCode() + "\nMessage: " + conn.getResponseMessage());
			}

			if (conn.getHeaderFields().keySet().contains("Set-Cookie")) {
				String setcookie = conn.getHeaderField("Set-Cookie");
				cookie += (cookie.isEmpty() ? "" : ",") + setcookie.split(";")[0];
			}

			String answer = "";
			BufferedReader read = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			while (read.ready()) {
				String line = read.readLine();
				answer += line;
			}
			conn.disconnect();
			return answer;
		} catch (Exception e) {
			return null;
		}
	}
	public static byte[] sendGETRequestGetAnswerAsBytes(String urlS) {
		try {
			String encodedUrl = StaticConfiguration.SOCOM_API_URL + urlS;
			URL url = new URL(encodedUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			if (!cookie.isEmpty())
				conn.addRequestProperty("cookie", cookie);
			conn.connect();

			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new Exception("Wrong Repsonse-Code: " + conn.getResponseCode() + "\nMessage: " + conn.getResponseMessage());
			}

			if (conn.getHeaderFields().keySet().contains("Set-Cookie")) {
				String setcookie = conn.getHeaderField("Set-Cookie");
				cookie += (cookie.isEmpty() ? "" : ",") + setcookie.split(";")[0];
			}
			byte[] result = IOUtils.toByteArray(conn.getInputStream());
		
			conn.disconnect();
			return result;
		} catch (Exception e) {
			return null;
		}
	}

	public static JSONObject sendMessagePOSTRequest(String urlS, String cookieParams, InputStream is) {
		try {
			String encodedUrl = StaticConfiguration.SOCOM_API_URL + urlS;
			URL url = new URL(encodedUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.addRequestProperty("cookie", cookie + "," + cookieParams);
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.connect();
			
			OutputStream out = conn.getOutputStream();
			while (is.available() > 0)
				out.write(is.read());
			out.flush();
			out.close();
			is.close();
			
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new Exception("Wrong Repsonse-Code: " + conn.getResponseCode() + "\nMessage: " + conn.getResponseMessage());
			}

			if (conn.getHeaderFields().keySet().contains("Set-Cookie")) {
				String setcookie = conn.getHeaderField("Set-Cookie");
				cookie += (cookie.isEmpty() ? "" : ",") + setcookie.split(";")[0];
			}

			String json = "";
			BufferedReader read = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			while (read.ready()) {
				String line = read.readLine();
				json += line;
			}
			conn.disconnect();
			return new JSONObject(json);
		} catch (Exception e) {
			return null;
		}
	}
}
