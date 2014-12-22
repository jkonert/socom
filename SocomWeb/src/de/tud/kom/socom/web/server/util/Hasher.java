package de.tud.kom.socom.web.server.util;

import java.security.MessageDigest;

public class Hasher {
	
	public static String getSHA(String input){
		try {
			MessageDigest md = MessageDigest.getInstance("SHA");
			byte[] digest = md.digest(input.getBytes());
			StringBuilder sb = new StringBuilder();
			for (byte b : digest)
				sb.append(String.format("%02x", b));
			return sb.toString();
		} catch (Exception e) {
			LoggerFactory.getLogger().Error(e);
		}
		return null;
	}
}
