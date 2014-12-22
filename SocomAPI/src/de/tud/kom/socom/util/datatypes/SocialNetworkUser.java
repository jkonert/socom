package de.tud.kom.socom.util.datatypes;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

import de.tud.kom.socom.util.JSONUtils;
import de.tud.kom.socom.util.LoggerFactory;


/**
 * 
 * @author rhaban
 * 
 */
public class SocialNetworkUser implements JSONString {

	private String userName, snName, urlProfile, game;

	public SocialNetworkUser() {
	}

	public SocialNetworkUser(String userName, String gameName, String snName, String urlProfile) {
		this.userName = userName;
		this.game = gameName;
		this.snName = snName;
		this.urlProfile = urlProfile;
	}

	/**
	 * @return the users name
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @return the networks name
	 */
	public String getSnName() {
		return snName;
	}
	
	public String getGame() {
		return game;
	}

	/**
	 * @return the url pattern for profiles
	 */
	public String getUrlProfile() {
		return urlProfile;
	}

	@Override
	public String toJSONString() {
		JSONObject json = new JSONObject();
		try {
			json.put("userName", userName);
			json.put("game", game);
			json.put("snName", snName);
			json.put("urlProfile", urlProfile);
		} catch (JSONException e) {
			LoggerFactory.getLogger().Error(e.getMessage());
		}
		return JSONUtils.JSONToString(json);
	}
}
