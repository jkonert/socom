package de.tud.socom.client.logic;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

import javax.swing.JOptionPane;

public class Status extends Observable {

	public static String SERVER_URL = "http://localhost:8080/servlet/";
	private static List<String> currentURLs = new LinkedList<String>();
	private File f;
	private boolean isPost;
	private String additionalCookies;
	private boolean isDownload;

	private static Status instance = new Status();

	private Status() {
	}

	public static Status get() {
		return instance;
	}

	public static void setHost(String host) {
		String _host = "";
		if (!host.startsWith("http://"))
			_host += "http://";

		_host += host;

		if (!host.endsWith("/"))
			_host += "/";
		SERVER_URL = _host + "servlet/";
	}

	public void setCurrentURL(String url) {
		currentURLs.add(url);

		setChanged();
		notifyObservers(modifyLastSend(currentURLs));
	}

	private List<String> modifyLastSend(List<String> currentURLs) {
		if (!isPost)
			return currentURLs;
		List<String> modified = new LinkedList<String>(currentURLs.subList(0, currentURLs.size() - 1));
		String currentURL = currentURLs.get(currentURLs.size() - 1);
		currentURL = "POST: " + currentURL + " (Parameter: " + additionalCookies + ") (File: " + f.getAbsolutePath() + ")";
		modified.add(currentURL);
		return modified;
	}

	public void setFile(File uploadFile) {
		f = uploadFile;
	}

	public File getFile() {
		if (f != null) {
			return f;
		}
		JOptionPane.showMessageDialog(null, "No File selected.");
		return null;
	}

	public void setCurrentMethod(String requestNethod) {
		if (requestNethod.equals(Connection.GET_REQUEST))
			isPost = false;
		else if (requestNethod.equals(Connection.POST_REQUEST))
			isPost = true;
		else
			throw new RuntimeException("Forbidden HTTP-Method");
	}

	public boolean isPost() {
		return isPost;
	}

	public void setCookieParams(String params) {
		additionalCookies = params;
	}

	public String getCookieParams() {
		return additionalCookies;
	}

	public boolean isDownload() {
		return isDownload;
	}
	
	public void setIsDownload(boolean isD){
		isDownload = isD;
	}
}
