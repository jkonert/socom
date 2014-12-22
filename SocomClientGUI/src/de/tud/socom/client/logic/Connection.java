package de.tud.socom.client.logic;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.json.JSONException;
import org.json.JSONObject;

public class Connection extends Observable {

	public static final String GET_REQUEST = "GET";
	public static final String POST_REQUEST = "POST";

	private static Connection instance = new Connection();
	private List<String> urlHistory = new LinkedList<String>();
	private Map<String, String> urlHistoryRequests = new HashMap<String, String>();

	private Connection() {
	}

	public static Connection get() {
		return instance;
	}

	public JSONObject sendGETRequest(String urlS) {
		String json = "";
		try {
			URL url = new URL((urlS).replaceAll(" ", "%20")
					.replace("\n", "%0A"));
			HttpURLConnection conn = null;
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setRequestMethod(GET_REQUEST);
			conn.addRequestProperty("cookie", Cookies.getCookieString());
			conn.connect();

			if (conn.getHeaderFields().keySet().contains("Set-Cookie")) {
				String setcookie = conn.getHeaderField("Set-Cookie");
				String cookie = setcookie.split(";")[0];
				Cookies.addCookie(cookie);
			}

			BufferedReader read = new BufferedReader(new InputStreamReader(
					conn.getInputStream())); // maybe add CharsetFor("UFT-8")
			String line;
			while ((line = read.readLine()) != null) {
				json += line;
			}
			read.close();
			conn.disconnect();

			urlHistory.add(urlS);
			urlHistoryRequests.put(urlS, GET_REQUEST);
			JSONObject jsonO = null;
			try {
				jsonO = new JSONObject(json);
			} catch (JSONException e) {
				System.err.println(e);
				System.out.println(json);
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
			setChanged();
			notifyObservers(new ServerAnswer(urlS,jsonO != null ? jsonO.toString(1) : json));
			return jsonO;
		} catch (MalformedURLException e) {
			JOptionPane.showMessageDialog(null,
					"Invalid URL :" + e.getMessage());
		} catch (ConnectException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public JSONObject sendPOSTRequest(String urlS) {
		return sendPOSTRequest(urlS, null, null);
	}

	public JSONObject sendPOSTRequest(String urlS, File f, String cookieParams) {
		String json = "";
		try {
			URL url = new URL((urlS).replaceAll(" ", "%20").replace("\n", "%0A"));

			HttpURLConnection.setFollowRedirects(true);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.addRequestProperty("cookie", Cookies.getCookieString()
					+ ((cookieParams != null) ? "," + cookieParams : ""));
			conn.setDoOutput(f != null);
			conn.setRequestMethod(POST_REQUEST);

			conn.connect();
			delay();
			
			
			if (f != null) {
				OutputStream out = conn.getOutputStream();
				InputStream fis = new FileInputStream(f);

				byte[] buffer = new byte[1024];
				while (fis.read(buffer, 0, buffer.length) != -1) {
					out.write(buffer);
				}

				out.flush();
				out.close();
				fis.close();
			}
			

			if (conn.getHeaderFields().keySet().contains("Set-Cookie")) {
				String setcookie = conn.getHeaderField("Set-Cookie");
				String cookie = setcookie.split(";")[0];
				Cookies.addCookie(cookie);
			}

			BufferedReader read = new BufferedReader(new InputStreamReader(
					conn.getInputStream())); // maybe add
												// Charset.forName("UTF-8")
			while (read.ready()) {
				String line = read.readLine();
				json += line;
			}
			conn.disconnect();
			String saveableURL = urlS + ";PARAMETER:"
					+ ((cookieParams != null) ? cookieParams : "") + ";FILE:"
					+ ((f != null) ? f.getPath() : "");
			urlHistory.add(saveableURL);
			urlHistoryRequests.put(saveableURL, Connection.POST_REQUEST);
			JSONObject jsonO = null;
			try {
				jsonO = new JSONObject(json);
			} catch (JSONException e) {
				System.err.println(e);
				System.out.println(json);
			}
			setChanged();
			notifyObservers(new ServerAnswer(urlS, jsonO == null ? json
					: jsonO.toString(1)));

			return jsonO;
		} catch (MalformedURLException e) {
			JOptionPane.showMessageDialog(null,
					"Invalid URL :" + e.getMessage());
		} catch (ConnectException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			System.err.println(e);
			System.out.println(json);
		}
		return null;
	}

	/**
	 * works with facebook HTTPS JSON urls
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 * @throws JSONException
	 */

	public JSONObject readJsonFromUrl(String url) throws IOException,
			JSONException {
		return new JSONObject(readFromUrl(url));
	}

	/**
	 * works with facebook HTTPS urls
	 * 
	 * @param url
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws JSONException
	 */
	public String readFromUrl(String url) throws MalformedURLException,
			IOException {
		InputStream is = new URL(url.replaceAll(" ", "%20")
				.replace("\n", "%0A")).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is,
					Charset.forName("UTF-8")));
			String jsonText = readAll(rd);

			urlHistory.add(url);
			urlHistoryRequests.put(url, GET_REQUEST);
			setChanged();
			notifyObservers(new ServerAnswer(url, jsonText));
			return jsonText;
		} catch (MalformedURLException e) {
			JOptionPane.showMessageDialog(null,
					"Invalid URL :" + e.getMessage());
		} finally {
			is.close();
		}
		return null;
	}

	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	public List<String> getUrlHistory() {
		return urlHistory;
	}

	public Map<String, String> getUrlHistoryRequests() {
		return urlHistoryRequests;
	}

	public void sendDownloadRequest(String urlS) {
		try {
			Status.get().setIsDownload(false);
			File f;
			JFileChooser jfc = new JFileChooser();
			jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int re = jfc.showSaveDialog(null);
			if (re == JFileChooser.APPROVE_OPTION) {
				f = jfc.getSelectedFile();
			} else
				return;

			if (f == null)
				return;

			URL url = new URL((urlS).replaceAll(" ", "%20")
					.replace("\n", "%0A"));
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.addRequestProperty("cookie", Cookies.getCookieString());
			conn.connect();

			if (conn.getHeaderFields().keySet().contains("Set-Cookie")) {
				String setcookie = conn.getHeaderField("Set-Cookie");
				String cookie = setcookie.split(";")[0];
				Cookies.addCookie(cookie);
			}

			BufferedInputStream is = new BufferedInputStream(
					conn.getInputStream());
			FileOutputStream fos = new FileOutputStream(f);
			while (is.available() > 0) {
				fos.write(is.read());
			}
			fos.flush();
			fos.close();
			is.close();

			conn.disconnect();

			setChanged();
			notifyObservers(new ServerAnswer(urlS, "File saved: " + f.getPath()));
		} catch (MalformedURLException e) {
			JOptionPane.showMessageDialog(null,
					"Invalid URL :" + e.getMessage());
		} catch (ConnectException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void delay() {
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
