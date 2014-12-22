package de.tud.kom.socom.web.client.sharedmodels;

import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class AchievementLevel implements IsSerializable {
	private long levelID, achievementID, counterMax;
	private int level, rewardPoints;
	private List<AchievementReward> rewards;
	
	public AchievementLevel() {
		
	}
	
	public AchievementLevel(long levelID, long achievementID, long counterMax,
			int level, int rewardPoints, List<AchievementReward> rewards) {
		this.levelID = levelID;
		this.achievementID = achievementID;
		this.counterMax = counterMax;
		this.level = level;
		this.rewardPoints = rewardPoints;
		this.rewards = rewards;
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
}
