package de.tud.socom.client.logic;

import java.util.LinkedList;
import java.util.List;

public class Cookies {

	private static List<String> cookies = new LinkedList<String>();

	public static void addCookie(String cookie) {
		cookies.add(cookie);
	}

	public static void removeCookie(String cookie) {
		cookies.remove(cookie);
	}

	public static void removeAllCookies() {
		cookies.clear();
	}

	public static List<String> getCookies() {
		return cookies;
	}

	public static String getCookieString() {
		StringBuffer sb = new StringBuffer();
		for (String key : cookies) {
			sb.append(key).append(",");
		}
		return sb.toString();
	}
}