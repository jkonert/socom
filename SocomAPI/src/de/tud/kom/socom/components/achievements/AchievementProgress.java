package de.tud.kom.socom.components.achievements;

import java.sql.Timestamp;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

import de.tud.kom.socom.util.JSONUtils;
import de.tud.kom.socom.util.LoggerFactory;

/**
 * 
 * @author ngerwien
 * 
 */

public class AchievementProgress implements JSONString {

	long userID, achievementID, counter, counterMax;
	int currentLevel, maxLevel;
	Timestamp timeCompleted;
	boolean isCompleted, doesExistInDB;
	
	public AchievementProgress(long userID, long achievementID, long counter, long counterMax, 
			Timestamp timeCompleted, boolean isCompleted, int currentLevel, 
			int maxLevel, boolean doesExistInDB) {
		super();
		this.userID = userID;
		this.achievementID = achievementID;
		this.counter = counter;
		this.counterMax = counterMax;
		this.timeCompleted = timeCompleted;
		this.isCompleted = isCompleted;
		this.currentLevel = currentLevel;
		this.maxLevel = maxLevel;
		this.doesExistInDB = doesExistInDB;
	}
	
	public boolean doesExistInDB() {
		return doesExistInDB;
	}
	
	public int getMaxLevel() {
		return maxLevel;
	}
	
	public int getCurrentLevel() {
		return currentLevel;
	}
	
	public long getUserID() {
		return userID;
	}

	public long getAchievementID() {
		return achievementID;
	}

	public long getCounter() {
		return counter;
	}
	
	public long getCounterMax() {
		return counterMax;
	}

	public Timestamp getTimeCompleted() {
		return timeCompleted;
	}

	public boolean isCompleted() {
		return isCompleted;
	}

	@Override
	public String toJSONString() {
		JSONObject json = new JSONObject();
		try {
			json.put("userID", userID);
			json.put("achievementID", achievementID);
			json.put("timeCompleted", timeCompleted);
			json.put("counter", counter);
			json.put("counterMax", counterMax);
			json.put("isCompleted", isCompleted);
			json.put("currentLevel", currentLevel);
			json.put("maxLevel", maxLevel);

			return JSONUtils.JSONToString(json);
		} catch (JSONException e) {
			LoggerFactory.getLogger().Error(e);
		}
		return JSONUtils.JSONToString(json);
	}

}
