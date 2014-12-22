package de.tud.kom.socom.web.client.sharedmodels;

import java.sql.Timestamp;

import com.google.gwt.user.client.rpc.IsSerializable;

public class AchievementProgress implements IsSerializable {
	long userID, achievementID, counter, counterMax;
	int currentLevel, maxLevel;
	Timestamp timeCompleted;
	boolean isCompleted;
	
	public AchievementProgress() {
		
	}
	
	public AchievementProgress(long userID, long achievementID, long counter, long counterMax, 
			Timestamp timeCompleted, boolean isCompleted, int currentLevel, 
			int maxLevel) {
		this.userID = userID;
		this.achievementID = achievementID;
		this.counter = counter;
		this.counterMax = counterMax;
		this.timeCompleted = timeCompleted;
		this.isCompleted = isCompleted;
		this.currentLevel = currentLevel;
		this.maxLevel = maxLevel;
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
}
