package de.tud.kom.socom.facebook.predef;

import java.util.HashMap;
import java.util.Map;

/**
 * Identifies a user connection with facebook by any unique identity string
 * 
 * @author rhaban
 */
public class FBIdentities {
	
	private String access_token;
	private static Map<String, FBIdentities> idents;

	static {
		idents = new HashMap<String, FBIdentities>();
	}
	
	public FBIdentities(String access_token) {
		this.access_token = access_token;
	}

	public static void addFBIdent(String id, String access_token){
		idents.put(id, new FBIdentities(access_token));
	}
	
	public static FBIdentities getFBIdent(String id) {
		return idents.get(id);
	}
	
	public static FBIdentities removeFBIdent(String id) {
		return idents.remove(id);
	}
	
	public String getAccessToken() {
		return access_token;
	}
}