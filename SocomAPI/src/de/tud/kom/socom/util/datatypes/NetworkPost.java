package de.tud.kom.socom.util.datatypes;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

import de.tud.kom.socom.util.JSONUtils;
import de.tud.kom.socom.util.LoggerFactory;

public class NetworkPost implements JSONString {
	private String name;
	private String message;
	private String imageUrl;
	private long likes;
	private String fromId;

	public NetworkPost(String fromID, String name, String message) {
		this.name = name;
		this.fromId = fromID;
		this.message = message;
		this.likes = -1;
	}
	
	public NetworkPost(String fromID, String name, String imageUrl, String message) {
		this.name = name;
		this.fromId = fromID;
		this.message = message;
		this.imageUrl = imageUrl;
		this.likes = -1;
	}
	
	public NetworkPost(String fromId, String name, String message, long likes) {
		this.name = name;
		this.fromId = fromId;
		this.message = message;
		this.likes = likes;
	}
	
	public NetworkPost(String fromId, String name, String message, String imageUrl, long likes) {
		this.name = name;
		this.fromId = fromId;
		this.message = message;
		this.imageUrl = imageUrl;
		this.likes = likes;
	}
	

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
	
	public long getLikes() {
		return likes;
	}

	public String getFromId() {
		return fromId;
	}

	@Override
	public String toJSONString(){
		JSONObject json = new JSONObject();
		try {
			json.put("name", name);
			json.put("fromid", fromId);
			if(message != null)
			{
				json.put("message", message);				
			}
			if(imageUrl != null)
			{
				json.put("image", imageUrl);
			}
			if(likes != -1)
			{
				json.put("likes", likes);
			}
		} catch (JSONException e) {
			LoggerFactory.getLogger().Error(e);
		}
		return JSONUtils.JSONToString(json);
	}	
}
