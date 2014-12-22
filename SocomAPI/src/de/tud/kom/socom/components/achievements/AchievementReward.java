package de.tud.kom.socom.components.achievements;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

import de.tud.kom.socom.util.JSONUtils;
import de.tud.kom.socom.util.LoggerFactory;

public class AchievementReward implements JSONString {
	private long rewardID, value;
	private String name, description;
	
	public AchievementReward(long rewardID, long value, String name, String description) {
		super();
		this.rewardID = rewardID;
		this.value = value;
		this.name = name;
		this.description = description;
	}
	
	public long getRewardID() {
		return rewardID;
	}

	public long getValue() {
		return value;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public String toJSONString() {
		JSONObject json = new JSONObject();
		try {
			json.put("rewardID", rewardID);
			json.put("name", name);
			json.put("description", description);
			json.put("value", value);

			return JSONUtils.JSONToString(json);
		} catch (JSONException e) {
			LoggerFactory.getLogger().Error(e);
		}
		return JSONUtils.JSONToString(json);
	}

}
