package de.tud.kom.socom.tests.util;

public class StaticConfiguration {
	
	private static final String SERVER_URL = "http://localhost";
//	private static final String SERVER_URL = "http://ktx-software.com";
	private static final String SERVER_PORT = "7999";
	private static final String SERVLET_PART = "servlet";
	private static final String WEB_PART = "web";
	
	public static final String SOCOM_API_URL = SERVER_URL + ":" + SERVER_PORT + "/" + SERVLET_PART + "/";
	public static final String SOCOM_WEB_URL = SERVER_URL + ":" + SERVER_PORT + "/" + WEB_PART;
	
	public static final String MASTER_SECRET = "123a45b6";
}