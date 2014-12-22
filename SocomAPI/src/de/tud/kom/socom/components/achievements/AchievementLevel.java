package de.tud.kom.socom.components.achievements;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

import de.tud.kom.socom.util.JSONUtils;
import de.tud.kom.socom.util.LoggerFactory;

public class AchievementLevel implements JSONString {
	private long levelID, achievementID, counterMax;
	private int level, rewardPoints;
	private List<AchievementReward> rewards;
	
	public AchievementLevel(long levelID, long achievementID, long counterMax, int level, int rewardPoints) {
		this.levelID = levelID;
		this.achievementID = achievementID;
		this.counterMax = counterMax;
		this.level = level;
		this.rewardPoints = rewardPoints;
		this.rewards = new ArrayList<AchievementReward>();
	}
	
	
	
	public long getLevelID() {
		return levelID;
	}



	public long getAchievementID() {
		return achievementID;
	}



	public long getCounterMax() {
		return counterMax;
	}



	public int getLevel() {
		return level;
	}



	public int getRewardPoints() {
		return rewardPoints;
	}



	public List<AchievementReward> getRewards() {
		return rewards;
	}

	public void addReward(AchievementReward reward) {
		rewards.add(reward);
	}

	@Override
	public String toJSONString() {
		JSONObject json = new JSONObject();
		try {
			json.put("levelid", levelID);
			json.put("achievementid", achievementID);
			json.put("countermax", counterMax);
			json.put("level", level);
			json.put("rewardPoints", rewardPoints);
			json.put("rewards", rewards);

			return JSONUtils.JSONToString(json);
		} catch (JSONException e) {
			LoggerFactory.getLogger().Error(e);
		}
		return JSONUtils.JSONToString(json);
	}

}
