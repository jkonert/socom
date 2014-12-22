package de.tud.kom.socom.web.client.sharedmodels;

import com.google.gwt.user.client.rpc.IsSerializable;

public class AchievementReward implements IsSerializable {
	private long rewardID, value;
	private String name, description;
	
	public AchievementReward() {
		
	}
	
	public AchievementReward(long rewardID, long value, String name, String description) {
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
}
