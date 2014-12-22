package de.tud.kom.socom.components.achievements;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

import de.tud.kom.socom.util.JSONUtils;
import de.tud.kom.socom.util.LoggerFactory;

public class AchievementProgressMessage implements JSONString {
	private boolean hasChanged;
	private String achievementName;
	private String message;

	public AchievementProgressMessage(boolean hasChanged,
			String achievementName, String message) {
		this.hasChanged = hasChanged;
		this.achievementName = achievementName;
		this.message = message;
	}
	
	public boolean isHasChanged() {
		return hasChanged;
	}

	public String getAchievementName() {
		return achievementName;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public String toJSONString() {
		JSONObject json = new JSONObject();
		try {
			json.put("hasChanged", hasChanged);
			json.put("achievementName", achievementName);
			json.put("message", message);

			return JSONUtils.JSONToString(json);
		} catch (JSONException e) {
			LoggerFactory.getLogger().Error(e);
		}
		return JSONUtils.JSONToString(json);
	}

}
