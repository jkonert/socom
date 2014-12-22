package de.tud.kom.socom.components.statistics;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

import de.tud.kom.socom.util.JSONUtils;
import de.tud.kom.socom.util.LoggerFactory;

/**
 * 
 * @author rhaban 
 * Encapsulates statistics for Socom
 * 
 */
public class SoComStatistic implements JSONString {

	private long userCount, userOnlineCount, gameCount, gameInstanceCount, gameContextsCount, contentCount, influenceCount, achievementsUnlockedCount,
			totalTimePlayed, averageTimePlayedPerUser;

	public SoComStatistic(long userCount, long userOnlineCount, long gameCount, long gameInstanceCount, long gameContextsCount, long contentCount,
			long influenceCount, long achievementsUnlockedCount, long totalTimePlayed, long averageTimePlayedPerUser) {
		super();
		this.userCount = userCount;
		this.userOnlineCount = userOnlineCount;
		this.gameCount = gameCount;
		this.gameInstanceCount = gameInstanceCount;
		this.gameContextsCount = gameContextsCount;
		this.contentCount = contentCount;
		this.influenceCount = influenceCount;
		this.achievementsUnlockedCount = achievementsUnlockedCount;
		this.totalTimePlayed = totalTimePlayed;
		this.averageTimePlayedPerUser = averageTimePlayedPerUser;
	}

	public long getUserCount() {
		return userCount;
	}

	public long getUserOnlineCount() {
		return userOnlineCount;
	}

	public long getGameCount() {
		return gameCount;
	}

	public long getGameInstanceCount() {
		return gameInstanceCount;
	}

	public long getGameContextsCount() {
		return gameContextsCount;
	}

	public long getContentCount() {
		return contentCount;
	}

	public long getInfluenceCount() {
		return influenceCount;
	}

	public long getAchievementsUnlockedCount() {
		return achievementsUnlockedCount;
	}

	public long getTotalTimePlayed() {
		return totalTimePlayed;
	}

	public long getAverageTimePlayedPerUser() {
		return averageTimePlayedPerUser;
	}

	@Override
	public String toJSONString() {
		try {
		JSONObject json = new JSONObject();
			json.put("userCount", userCount);
			json.put("usersOnline", userOnlineCount);
			json.put("gameCount", gameCount);
			json.put("gameInstanceCount", gameInstanceCount);
			json.put("gameContextCount", gameContextsCount);
			json.put("contentCount", contentCount);
			json.put("influenceCount", influenceCount);
			json.put("achievementsUnlocked", achievementsUnlockedCount);
			json.put("totalTimePlayed", totalTimePlayed);
			json.put("averageTimePlayedPerUser", averageTimePlayedPerUser);
			return JSONUtils.JSONToString(json);
		} catch (JSONException e) {
			LoggerFactory.getLogger().Error(e);
		}
		return null;
	}
}
