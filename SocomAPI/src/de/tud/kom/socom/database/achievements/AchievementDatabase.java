package de.tud.kom.socom.database.achievements;

import java.sql.SQLException;

import de.tud.kom.socom.components.achievements.Achievement;
import de.tud.kom.socom.components.achievements.AchievementProgress;
import de.tud.kom.socom.components.achievements.AchievementProgressMessage;
import de.tud.kom.socom.components.achievements.AchievementReward;
import de.tud.kom.socom.util.exceptions.AchievementAlreadyExistException;
import de.tud.kom.socom.util.exceptions.AchievementCategoryNotFoundException;
import de.tud.kom.socom.util.exceptions.AchievementLevelCountermaxInvalidException;
import de.tud.kom.socom.util.exceptions.AchievementLevelNotFoundException;
import de.tud.kom.socom.util.exceptions.AchievementNotFoundException;
import de.tud.kom.socom.util.exceptions.RewardAlreadyExistException;
import de.tud.kom.socom.util.exceptions.RewardNotFoundException;

/**
 * 
 * @author ngerwien
 * 
 */
public interface AchievementDatabase {
	public void addAchievement(Achievement achievement) throws SQLException, AchievementAlreadyExistException, AchievementNotFoundException;
	
	public void addAchievementLevel(Achievement achievement) throws SQLException, AchievementLevelCountermaxInvalidException, AchievementNotFoundException, AchievementLevelNotFoundException;
	
	public AchievementProgressMessage updateAchievementProgress(String achievementname, long gameID, long counter, long userID) throws SQLException, AchievementNotFoundException, AchievementLevelNotFoundException;
	
	public void resetAchievementProgress(String achievementname, long gameID, long userID) throws SQLException, AchievementNotFoundException, AchievementLevelNotFoundException;
	
	public void removeAchievement(String name, long gameID) throws SQLException, AchievementNotFoundException, AchievementCategoryNotFoundException, AchievementLevelNotFoundException;
	
	public void addReward(AchievementReward reward) throws SQLException, RewardAlreadyExistException;
	
	public void setAchievementReward(Achievement achievement, AchievementReward reward, int achievementlevel) throws SQLException, AchievementNotFoundException, RewardNotFoundException, AchievementLevelNotFoundException;
	
	public Achievement getAchievement(String name, long gameID) throws SQLException, AchievementNotFoundException, AchievementCategoryNotFoundException, AchievementLevelNotFoundException;
	
	public AchievementProgress getAchievementProgress(String name, long gameID, long userID) throws SQLException, AchievementNotFoundException, AchievementLevelNotFoundException;
}
