package de.tud.kom.socom.components.game;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

import de.tud.kom.socom.util.JSONUtils;
import de.tud.kom.socom.util.LoggerFactory;

public class GameInstance implements JSONString {
	private String game, version, description;
	private Date lastUsed;

	public GameInstance(String game, String version, String description) {
		this.game = game;
		this.version = version;
		this.description = description;
	}

	public String getGame() {
		return game;
	}

	public String getVersion() {
		return version;
	}

	public String getDescription() {
		return description;
	}

	public void setLastUsed(Date d) {
		this.lastUsed = d;
	}

	@Override
	public String toJSONString() {
		JSONObject json = new JSONObject();
		try {
			json.put("game", game);
			json.put("version", version);
			json.put("description", description);
			if (lastUsed != null) {
				json.put("lastPlayed", lastUsed.toString());
			}
		} catch (JSONException e) {
			LoggerFactory.getLogger().Error(e);
		}
		return JSONUtils.JSONToString(json);
	}
}
