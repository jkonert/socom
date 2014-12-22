package de.tud.kom.socom.database.achievements;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import de.tud.kom.socom.components.achievements.Achievement;
import de.tud.kom.socom.components.achievements.AchievementLevel;
import de.tud.kom.socom.components.achievements.AchievementProgress;
import de.tud.kom.socom.components.achievements.AchievementProgressMessage;
import de.tud.kom.socom.components.achievements.AchievementReward;
import de.tud.kom.socom.database.HSQLDatabase;
import de.tud.kom.socom.util.exceptions.AchievementAlreadyExistException;
import de.tud.kom.socom.util.exceptions.AchievementCategoryNotFoundException;
import de.tud.kom.socom.util.exceptions.AchievementLevelCountermaxInvalidException;
import de.tud.kom.socom.util.exceptions.AchievementLevelNotFoundException;
import de.tud.kom.socom.util.exceptions.AchievementNotFoundException;
import de.tud.kom.socom.util.exceptions.RewardAlreadyExistException;
import de.tud.kom.socom.util.exceptions.RewardNotFoundException;

public class HSQLAchievementDatabase extends HSQLDatabase implements AchievementDatabase {

	private static AchievementDatabase instance = new HSQLAchievementDatabase();
	
	private HSQLAchievementDatabase() {
		super();
	}

	public static AchievementDatabase getInstance() {
		return instance;
	}
	
	@Override
	public void addAchievement(Achievement achievement) 
			throws SQLException, AchievementAlreadyExistException, AchievementNotFoundException {		
		PreparedStatement selectQuery = db
				.getPreparedStatement("SELECT * FROM achievement WHERE name = ? AND gameid = ?;");

		selectQuery.setString(1, achievement.getName());
		selectQuery.setLong(2, achievement.getGameID());
		
		if(selectQuery.executeQuery().next())
			throw new AchievementAlreadyExistException();
		
		long categoryID = getAchievementCategoryID(
				achievement.getGameID(), achievement.getCategoryName());
		
		PreparedStatement insertAchievementQuery = 
				db.getPreparedStatement("INSERT INTO " +
										"achievement " +
											"(gameid, " +
											"name, " +
											"image, " +
											"categoryid, " +
											"description) " +
										"VALUES " +
											"(?, " +
											"?, " +
											"?, " +
											"?, " +
											"?);");
		
		insertAchievementQuery.setLong(1, achievement.getGameID());
		insertAchievementQuery.setString(2, achievement.getName());
		insertAchievementQuery.setString(3, achievement.getImage());
		insertAchievementQuery.setLong(4, categoryID);
		insertAchievementQuery.setString(5, achievement.getDescription());
		insertAchievementQuery.execute();
		
		long achievementID = getAchievementID(achievement.getName(), achievement.getGameID());		
		insertAchievementLevelQuery(
				achievementID,
				achievement.getLevels().get(0).getLevel(),
				achievement.getLevels().get(0).getCounterMax(),
				achievement.getLevels().get(0).getRewardPoints());
	}
	
	@Override
	public void addAchievementLevel(Achievement achievement)
			throws SQLException, AchievementLevelCountermaxInvalidException, AchievementNotFoundException, AchievementLevelNotFoundException {
		long achievementID = getAchievementID(achievement.getName(), achievement.getGameID());
		List<AchievementLevel> levels = getAchievementLevels(achievementID);
		AchievementLevel maxlevel = levels.get(levels.size() - 1);
		
		if(maxlevel.getCounterMax() >= achievement.getLevels().get(0).getCounterMax())
			throw new AchievementLevelCountermaxInvalidException();
		
		insertAchievementLevelQuery(
				achievementID,
				maxlevel.getLevel() + 1,
				achievement.getLevels().get(0).getCounterMax(),
				achievement.getLevels().get(0).getRewardPoints());
	}

	@Override
	public AchievementProgressMessage updateAchievementProgress(String achievementName, long gameID,
			long counter, long userID) throws SQLException, AchievementNotFoundException, AchievementLevelNotFoundException {
		if(counter == 0) {
			return new AchievementProgressMessage(false, achievementName, "");
		}
		
		long achievementID = getAchievementID(achievementName, gameID);
		AchievementProgress oldProgress = getAchievementProgress(userID, achievementID);
		
		if(oldProgress.doesExistInDB() && oldProgress.isCompleted()) {
				return new AchievementProgressMessage(false, achievementName, "");
		}
		
		List<AchievementLevel> levels = getAchievementLevels(achievementID);
		AchievementLevel maxLevel = levels.get(levels.size() - 1);
		long minCounter = getMinReversibleAchievementCounter(oldProgress);
		long newCounter = oldProgress.getCounter() + counter;		
		
		if(newCounter > maxLevel.getCounterMax()) {
			newCounter = maxLevel.getCounterMax();
		}
		else if(newCounter < minCounter) {
			newCounter = minCounter;
		}
		
		boolean isnewProgressCompleted = isAchievementCompleted(newCounter, maxLevel.getCounterMax());
		
		if(oldProgress.doesExistInDB()) {
			updateNewAchievementProgress(userID, achievementID, newCounter, isnewProgressCompleted);
		}
		else {
			insertNewAchievementProgress(userID, achievementID, newCounter, isnewProgressCompleted);
		}
		
		if(isnewProgressCompleted) {
			return new AchievementProgressMessage(true, achievementName, "Completed.");
		}
		
		AchievementLevel oldLevel = getCurrentAchievementLevel(levels, oldProgress.getCounter());
		AchievementLevel newLevel = getCurrentAchievementLevel(levels, newCounter);
		
		if(oldLevel.getLevel() != newLevel.getLevel()) {
			return new AchievementProgressMessage(true, achievementName, "Level " + oldLevel.getLevel() + " completed.");
		}
		else {
			return new AchievementProgressMessage(false, achievementName, "");
		}
	}
	
	@Override
	public void resetAchievementProgress(String achievementname, long gameID, long userID)
			throws SQLException, AchievementNotFoundException, AchievementLevelNotFoundException {
		long achievementID = getAchievementID(achievementname, gameID);
		resetAchievementProgress(userID, achievementID);
	}
	
	
	@Override
	public AchievementProgress getAchievementProgress(String name, long gameID, long userID)
			throws SQLException, AchievementNotFoundException, AchievementLevelNotFoundException {
		long achievementID = getAchievementID(name, gameID);		
		return getAchievementProgress(userID, achievementID);
	}
	
	@Override
	public void addReward(AchievementReward reward) throws SQLException, RewardAlreadyExistException {
		PreparedStatement selectQuery = db
				.getPreparedStatement("SELECT * FROM achievementreward WHERE name = ?;");

		selectQuery.setString(1, reward.getName());
		
		if(selectQuery.executeQuery().next())
			throw new RewardAlreadyExistException();
		
		PreparedStatement insertQuery = 
				db.getPreparedStatement("INSERT INTO " +
										"achievementreward " +
											"(name, " +
											"description, " +
											"value) " +
										"VALUES " +
											"(?, " +
											"?, " +
											"?);");
		
		insertQuery.setString(1, reward.getName());
		insertQuery.setString(2, reward.getDescription());
		insertQuery.setLong(3, reward.getValue());
		insertQuery.execute();
	}

	@Override
	public void setAchievementReward(Achievement achievement, AchievementReward reward, int achievementLevel)
			throws SQLException, AchievementNotFoundException, RewardNotFoundException, AchievementLevelNotFoundException {
		long achievementID = getAchievementID(achievement.getName(), achievement.getGameID());
		long rewardID = getRewardID(reward);
		List<AchievementLevel> levels = getAchievementLevels(achievementID);
		long levelID = levels.get(achievementLevel - 1).getLevelID();
		
		PreparedStatement insertQuery = 
				db.getPreparedStatement("INSERT INTO " +
										"achievementrewardrelations " +
											"(levelid, " +
											"rewardid) " +
										"VALUES " +
											"(?, " +
											"?);");
		
		insertQuery.setLong(1, levelID);
		insertQuery.setLong(2, rewardID);
		insertQuery.execute();
	}
	
	@Override
	public Achievement getAchievement(String name, long gameID)
			throws SQLException, AchievementNotFoundException, AchievementCategoryNotFoundException, AchievementLevelNotFoundException {
		PreparedStatement selectQuery = 
				db.getPreparedStatement("SELECT * " +
										"FROM achievement " +
										"WHERE gameid = ? " +
										"AND name = ?;");
		selectQuery.setLong(1, gameID);
		selectQuery.setString(2, name);
		ResultSet result = selectQuery.executeQuery();
		
		if (!result.next())
			throw new AchievementNotFoundException();
		
		Achievement achievement = new Achievement(
				result.getLong("achievementid"),
				result.getLong("gameid"),
				result.getString("name"),
				result.getString("description"),
				result.getString("image"),
				getAchievementCategoryName(result.getLong("categoryid")));
		
		List<AchievementLevel> levels = getAchievementLevels(achievement.getAchievementID());
		for(AchievementLevel level : levels) {
			List<AchievementReward> rewards = getRewards(level);
			for(AchievementReward reward : rewards) {
				level.addReward(reward);
			}
		}
				
		for(AchievementLevel level : levels) {
			achievement.addLevel(level);
		}

		return achievement;
	}
	
	@Override
	public void removeAchievement(String name, long gameID)
			throws SQLException, AchievementNotFoundException, AchievementCategoryNotFoundException, AchievementLevelNotFoundException {
		Achievement achievement = getAchievement(name, gameID);
		long categoryID = getAchievementCategoryID(gameID, achievement.getCategoryName());
		
		removeAchievementLevels(achievement.getAchievementID());
		
		PreparedStatement deleteAchievementQuery = 
				db.getPreparedStatement("DELETE FROM achievement " +
										"WHERE achievementID = ?;");
		deleteAchievementQuery.setLong(1, achievement.getAchievementID());
		deleteAchievementQuery.execute();
		
		removeAchievementCategory(categoryID);
	}
	
	private void removeAchievementReward(long levelID, long rewardID)
			throws SQLException {
		PreparedStatement deleteRelationQuery = 
				db.getPreparedStatement("DELETE FROM achievementrewardrelations " +
										"WHERE rewardid = ? " +
										"AND levelid = ?;");
		deleteRelationQuery.setLong(1, rewardID);
		deleteRelationQuery.setLong(2, levelID);
		deleteRelationQuery.execute();
		
		PreparedStatement rewardRelationQuery = 
				db.getPreparedStatement("SELECT * " +
										"FROM achievementrewardrelations " +
										"WHERE rewardid = ?;");
		rewardRelationQuery.setLong(1, rewardID);
		ResultSet resultRewardRelation = rewardRelationQuery.executeQuery();

		//if reward is related to other achievements then don't remove
		if(resultRewardRelation.next()) {
			return;
		}
		
		PreparedStatement deleteRewardQuery = 
				db.getPreparedStatement("DELETE FROM achievementreward " +
										"WHERE rewardid = ?;");
		deleteRewardQuery.setLong(1, rewardID);
		deleteRewardQuery.execute();
	}
	
	private void removeAchievementCategory(long categoryID)
			throws SQLException {
		PreparedStatement selectQuery = 
				db.getPreparedStatement("SELECT * " +
										"FROM achievement " +
										"WHERE categoryid = ?;");
		selectQuery.setLong(1, categoryID);
		ResultSet result = selectQuery.executeQuery();

		//if category is still related to other achievements don't remove
		if(result.next()) {
			return;
		}
		
		PreparedStatement deleteQuery = 
				db.getPreparedStatement("DELETE FROM achievementcategory " +
										"WHERE categoryid = ?;");
		deleteQuery.setLong(1, categoryID);
		deleteQuery.execute();
	}
	
	private void removeAchievementLevels(long achievementID)
			throws SQLException, AchievementLevelNotFoundException {
		List<AchievementLevel> levels = getAchievementLevels(achievementID);
		
		for(AchievementLevel level : levels) {
			PreparedStatement rewardRelationQuery = 
					db.getPreparedStatement("SELECT * " +
											"FROM achievementrewardrelations " +
											"WHERE levelid = ?;");
			rewardRelationQuery.setLong(1, level.getLevelID());
			ResultSet result = rewardRelationQuery.executeQuery();
			
			while(result.next()) {
				long rewardID = result.getLong("rewardid");
				removeAchievementReward(level.getLevelID(), rewardID);
			}
			
			PreparedStatement deleteAchievementLevelQuery = 
					db.getPreparedStatement("DELETE FROM achievementlevel " +
											"WHERE levelID = ?;");
			deleteAchievementLevelQuery.setLong(1, level.getLevelID());
			deleteAchievementLevelQuery.execute();
		}
		
	}

	private List<AchievementReward> getRewards(AchievementLevel level) 
			throws SQLException {
		List<AchievementReward> rewards = new ArrayList<AchievementReward>();
		
		PreparedStatement selectQuery = 
				db.getPreparedStatement("SELECT achievementreward.* " +
										"FROM achievementreward, achievementrewardrelations " +
										"WHERE achievementrewardrelations.rewardid = achievementreward.rewardid " +
										"AND achievementrewardrelations.levelid = ?;");
		selectQuery.setLong(1, level.getLevelID());
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
	
	private List<AchievementLevel> getAchievementLevels(long achievementID) 
			throws SQLException, AchievementLevelNotFoundException {
		List<AchievementLevel> levels = new ArrayList<AchievementLevel>();
		
		PreparedStatement selectQuery = 
				db.getPreparedStatement("SELECT * " +
										"FROM achievementlevel " +
										"WHERE achievementid = ? " +
										"ORDER BY level ASC;");
		selectQuery.setLong(1, achievementID);
		ResultSet result = selectQuery.executeQuery();
		
		while(result.next()) {
			AchievementLevel level = new AchievementLevel(
					result.getLong("levelid"),
					achievementID,
					result.getLong("countermax"),
					result.getInt("level"),
					result.getInt("rewardpoints"));
			levels.add(level);
		}			
		
		if(levels.size() == 0) {
			throw new AchievementLevelNotFoundException();
		}
		
		return levels;
	}
	
	private long getAchievementID(String name, long gameID) 
			throws SQLException, AchievementNotFoundException {
		PreparedStatement selectQuery = 
				db.getPreparedStatement("SELECT achievementid " +
										"FROM achievement " +
										"WHERE gameid = ? " +
										"AND name = ?;");
		selectQuery.setLong(1, gameID);
		selectQuery.setString(2, name);
		ResultSet result = selectQuery.executeQuery();
		
		if (!result.next())
			throw new AchievementNotFoundException();
				
		return result.getLong("achievementid");
	}
	
	private long getRewardID(AchievementReward reward) 
			throws SQLException, RewardNotFoundException {
		PreparedStatement selectQuery = 
				db.getPreparedStatement("SELECT rewardid " +
										"FROM achievementreward " +
										"WHERE name = ?;");
		selectQuery.setString(1, reward.getName());
		ResultSet result = selectQuery.executeQuery();
		
		if (!result.next())
			throw new RewardNotFoundException();
			
		return result.getLong("rewardid");
	}	
	
	private void updateNewAchievementProgress(long userID, long achievementID, long newCounter,
			boolean isCompleted) throws SQLException {		
		PreparedStatement resetQuery = 
				db.getPreparedStatement("UPDATE achievementprogress " +
										"SET timecompleted = NOW, counter = ?, iscompleted = ? " +
										"WHERE userid = ? " +
										"AND achievementid = ?;");
		resetQuery.setLong(1, newCounter);
		resetQuery.setBoolean(2, isCompleted);
		resetQuery.setLong(3, userID);
		resetQuery.setLong(4, achievementID);
		resetQuery.execute();
	}
	
	private void insertNewAchievementProgress(long userID, long achievementID, long newCounter,
			boolean isCompleted) throws SQLException {		
		PreparedStatement insertQuery = 
				db.getPreparedStatement("INSERT INTO " +
										"achievementprogress " +
											"(userid, " +
											"achievementid, " +
											"counter, " +
											"iscompleted, " +
											"timecompleted) " +
										"VALUES " +
											"(?, " +
											"?, " +
											"?, " +
											"?, " +
											"NOW);");		
		insertQuery.setLong(1, userID);
		insertQuery.setLong(2, achievementID);
		insertQuery.setLong(3, newCounter);
		insertQuery.setBoolean(4, isCompleted);
		insertQuery.execute();
	}
	
	private boolean isAchievementCompleted(long newCounter, long maxCounter) {		
		return newCounter >= maxCounter;
	}
	
	private AchievementLevel getCurrentAchievementLevel(List<AchievementLevel> levels, long counter)
			throws SQLException, AchievementLevelNotFoundException {		
		for(AchievementLevel level : levels) {
			if(counter < level.getCounterMax()) {
				return level;
			}
		}
		
		return levels.get(levels.size() - 1);
	}
	
	private void insertAchievementLevelQuery(long achievementID, int level, long countermax,
			int rewardpoints) throws SQLException {
		PreparedStatement insertQuery = 
				db.getPreparedStatement("INSERT INTO " +
										"achievementlevel " +
											"(achievementid, " +
											"level, " +
											"countermax, " +
											"rewardpoints) " +
										"VALUES " +
											"(?, " +
											"?, " +
											"?, " +
											"?);");
		
		insertQuery.setLong(1, achievementID);
		insertQuery.setInt(2, level);
		insertQuery.setLong(3, countermax);
		insertQuery.setInt(4, rewardpoints);
		insertQuery.execute();
	}
	
	private AchievementProgress getAchievementProgress(long userID, long achievementID)
			throws SQLException, AchievementLevelNotFoundException {
		PreparedStatement selectQuery = db
				.getPreparedStatement("SELECT * " +
						"FROM achievementprogress " +
						"WHERE userid = ? " +
						"AND achievementid = ?;");
		selectQuery.setLong(1, userID);
		selectQuery.setLong(2, achievementID);		
		ResultSet result = selectQuery.executeQuery();
		
		List<AchievementLevel> levels = getAchievementLevels(achievementID);
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
					maxLevel.getLevel(),
					true);
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
					maxLevel.getLevel(),
					false);
		}		
		
		return progress;
	}
	
	private long getAchievementCategoryID(long gameID, String categoryName)
			throws SQLException {
		PreparedStatement selectQuery = 
				db.getPreparedStatement("SELECT * " +
										"FROM achievementcategory " +
										"WHERE gameid = ? " +
										"AND name = ?;");
		selectQuery.setLong(1, gameID);
		selectQuery.setString(2, categoryName);
		ResultSet result = selectQuery.executeQuery();
		
		if(result.next()) {
			return result.getLong("categoryid");
		}
		
		PreparedStatement insertQuery = 
				db.getPreparedStatement("INSERT INTO " +
										"achievementcategory " +
											"(gameid, " +
											"name) " +
										"VALUES " +
											"(?, " +
											"?);");
		
		insertQuery.setLong(1, gameID);
		insertQuery.setString(2, categoryName);
		insertQuery.execute();
		
		result = selectQuery.executeQuery();
		result.next();
		
		return result.getLong("categoryid");
	}
	
	private String getAchievementCategoryName(long categoryID)
			throws SQLException, AchievementCategoryNotFoundException {
		PreparedStatement selectQuery = 
				db.getPreparedStatement("SELECT * " +
										"FROM achievementcategory " +
										"WHERE categoryid = ? ");
		selectQuery.setLong(1, categoryID);
		ResultSet result = selectQuery.executeQuery();
		
		if(!result.next()) {
			throw new AchievementCategoryNotFoundException();
		}
		
		return result.getString("name");
	}
	
	private long getMinReversibleAchievementCounter(AchievementProgress progress)
			throws SQLException, AchievementLevelNotFoundException {
		if(progress.getCurrentLevel() == 1) {
			return 0;
		}
		else if(progress.isCompleted()) {
			return progress.getCounter();
		}
		
		List<AchievementLevel> levels = getAchievementLevels(progress.getAchievementID());
		AchievementLevel previousAchievementLevel = levels.get(progress.getCurrentLevel() - 2);
		
		return previousAchievementLevel.getCounterMax();
	}
	
	private void resetAchievementProgress(long userID, long achievementID)
			throws SQLException, AchievementLevelNotFoundException {
		AchievementProgress progress = getAchievementProgress(userID, achievementID);
		
		if(progress.isCompleted()) {
			return;
		}
		
		long minCounter = getMinReversibleAchievementCounter(progress);
		if(progress.doesExistInDB()) {
			updateNewAchievementProgress(userID, achievementID, minCounter, false);
		}
		else {
			insertNewAchievementProgress(userID, achievementID, minCounter, false);
		}			
	}
}
