package de.tud.kom.socom.web.client.sharedmodels;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class AchievementGame implements IsSerializable {
	public static final String FirstCategory = "Summary";
	
	long gameID;
	String gameName;
	List<Achievement> achievements;
	List<String> achievementCategories;
	
	public AchievementGame() {
		
	}
	
	public AchievementGame(long gameID, String gameName, List<Achievement> achievements) {
		this.gameID = gameID;
		this.gameName = gameName;
		this.achievements = achievements;
		gatherAchievementCategories();
	}
	
	public long getGameID() {
		return gameID;
	}

	public String getGameName() {
		return gameName;
	}
	
	public List<Achievement> getAchievements() {
		return achievements;
	}
	
	public List<Achievement> getAchievements(String categoryName) {
		List<Achievement> achievementsInCategory = new ArrayList<Achievement>();
		for(Achievement achievement : achievements) {
			if(achievement.getCategoryName() == categoryName) {
				achievementsInCategory.add(achievement);
			}
		}
		
		return achievementsInCategory;
	}
	
	public List<String> getAchievementCategories() {
		return achievementCategories;
	}
	
	private void gatherAchievementCategories() {		
		achievementCategories = new ArrayList<String>();
		achievementCategories.add(FirstCategory);
		for(Achievement achievement : achievements) {
			if(!achievementCategories.contains(achievement.getCategoryName())) {
				achievementCategories.add(achievement.getCategoryName());
			}
		}
	}
}
