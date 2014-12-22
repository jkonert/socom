package de.tud.kom.socom.web.server.database.achievement;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import de.tud.kom.socom.web.client.sharedmodels.Achievement;
import de.tud.kom.socom.web.client.sharedmodels.AchievementGame;
import de.tud.kom.socom.web.client.sharedmodels.AchievementLevel;
import de.tud.kom.socom.web.client.sharedmodels.AchievementProgress;
import de.tud.kom.socom.web.client.sharedmodels.AchievementReward;
import de.tud.kom.socom.web.client.sharedmodels.GlobalConfig;
import de.tud.kom.socom.web.server.database.HSQLAccess;

public class HSQLAchievementDatabaseAccess implements
		AchievementDatabaseAccess, GlobalConfig {

	private static AchievementDatabaseAccess instance = new HSQLAchievementDatabaseAccess();
	private static HSQLAccess db;

	private HSQLAchievementDatabaseAccess() {
		db = HSQLAccess.getInstance();
	}

	public static AchievementDatabaseAccess getInstance() {
		return instance;
	}
	
	@Override
	public List<AchievementGame> getGames(long userID) throws SQLException {
		List<AchievementGame> games = new ArrayList<AchievementGame>();
		
		PreparedStatement selectGamesQuery = 
				db.getPreparedStatement("SELECT games.gameid, games.name " +
										"FROM games, achievement " +
										"WHERE games.gameid = achievement.gameid " +
										"GROUP BY games.gameid;");
		ResultSet result = selectGamesQuery.executeQuery();
		
		while(result.next()) {
			long gameID = result.getLong("gameid");
			List<Achievement> achievements = getAchievements(userID, gameID);

			AchievementGame game = new AchievementGame(
					gameID,
					result.getString("name"),
					achievements);
			games.add(game);
		}			
		
		return games;
	}

	
	private List<Achievement> getAchievements(long userID, long gameID) throws SQLException {
		PreparedStatement selectAllAchievementsQuery = 
				db.getPreparedStatement("SELECT * " +
										"FROM achievement " +
										"WHERE gameid = ?;");
		selectAllAchievementsQuery.setLong(1, gameID);
		ResultSet result = selectAllAchievementsQuery.executeQuery();

		List<Achievement> achievements = new ArrayList<Achievement>();
		while(result.next()) {
			long achievementID = result.getLong("achievementid");
			String categoryName = getAchievementCategoryName(result.getLong("categoryid"));
			List<AchievementLevel> levels = getAchievementLevels(achievementID);
			AchievementProgress progress = getAchievementProgress(userID, achievementID, levels);
			int currentRewardPoints = getCurrentRewardPoints(levels, progress.getCurrentLevel());
			
			Achievement achievement = new Achievement(
					achievementID,
					gameID,
					levels.get(levels.size() - 1).getCounterMax(),
					result.getString("name"),
					result.getString("description"),
					result.getString("image"),
					categoryName,
					currentRewardPoints,
					levels,
					progress);
			
			achievements.add(achievement);
		}

		return achievements;
	}

	private String getAchievementCategoryName(long categoryID) throws SQLException {
		PreparedStatement selectQuery = 
				db.getPreparedStatement("SELECT * " +
										"FROM achievementcategory " +
										"WHERE categoryid = ? ");
		selectQuery.setLong(1, categoryID);
		ResultSet result = selectQuery.executeQuery();
		
		if(!result.next()) {
			return "";
		}
		
		return result.getString("name");
	}
	
	private List<AchievementLevel> getAchievementLevels(long achievementID) 
			throws SQLException {
		List<AchievementLevel> levels = new ArrayList<AchievementLevel>();
		
		PreparedStatement selectQuery = 
				db.getPreparedStatement("SELECT * " +
										"FROM achievementlevel " +
										"WHERE achievementid = ? " +
										"ORDER BY level ASC;");
		selectQuery.setLong(1, achievementID);
		ResultSet result = selectQuery.executeQuery();
		
		while(result.next()) {
			long levelID = result.getLong("levelid");
			List<AchievementReward> rewards = getRewards(levelID);
			
			AchievementLevel level = new AchievementLevel(
					result.getLong("levelid"),
					achievementID,
					result.getLong("countermax"),
					result.getInt("level"),
					result.getInt("rewardpoints"),
					rewards);
			levels.add(level);
		}
		
		return levels;
	}
	
	private List<AchievementReward> getRewards(long levelID) 
			throws SQLException {
		List<AchievementReward> rewards = new ArrayList<AchievementReward>();
		
		PreparedStatement selectQuery = 
				db.getPreparedStatement("SELECT achievementreward.* " +
										"FROM achievementreward, achievementrewardrelations " +
										"WHERE achievementrewardrelations.rewardid = achievementreward.rewardid " +
										"AND achievementrewardrelations.levelid = ?;");
		selectQuery.setLong(1, levelID);
		ResultSet result = selectQuery.executeQuery();
		
		while(result.next()) {
			AchievementReward reward = new AchievementReward(
					result.getLong("rewardid"),
					result.getLong("value"),
					result.getString("name"),
					result.getString("description"));
			rewards.add(reward);
		}			
		
		return rewards;
	}
	
	private AchievementProgress getAchievementProgress(long userID, long achievementID,
			List<AchievementLevel> levels) throws SQLException {
		PreparedStatement selectQuery = db
				.getPreparedStatement("SELECT * " +
						"FROM achievementprogress " +
						"WHERE userid = ? " +
						"AND achievementid = ?;");
		selectQuery.setLong(1, userID);
		selectQuery.setLong(2, achievementID);		
		ResultSet result = selectQuery.executeQuery();
		
		AchievementLevel maxLevel = levels.get(levels.size() - 1);		
		AchievementProgress progress;
		if(result.next()) {
			long counter = result.getLong("counter");
			AchievementLevel currentLevel = getCurrentAchievementLevel(levels, counter);
						
			progress = new AchievementProgress(
					userID,
					achievementID,
					counter,
					currentLevel.getCounterMax(),
					result.getTimestamp("timeCompleted"),
					result.getBoolean("isCompleted"),
					currentLevel.getLevel(),
					maxLevel.getLevel());
		}
		else {
			AchievementLevel firstLevel = levels.get(0);	
			
			progress = new AchievementProgress(
					userID,
					achievementID,
					0,
					firstLevel.getCounterMax(),
					null,
					false,
					1,
					maxLevel.getLevel());
		}		
		
		return progress;
	}
	
	private AchievementLevel getCurrentAchievementLevel(List<AchievementLevel> levels, long counter)
			throws SQLException {		
		for(AchievementLevel level : levels) {
			if(counter < level.getCounterMax()) {
				return level;
			}
		}
		
		return levels.get(levels.size() - 1);
	}
	
	private int getCurrentRewardPoints(List<AchievementLevel> levels, int currentLevel) {
		int currentRewardPoints = 0;
		for(int i = 0; i < (currentLevel - 1); i++) {
			currentRewardPoints += levels.get(i).getRewardPoints();
		}
		
		return currentRewardPoints;
	}
}
