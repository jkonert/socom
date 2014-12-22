package de.tud.kom.socom.web.server;

import java.util.ArrayList;
import java.util.List;

import de.tud.kom.socom.web.client.services.achievements.SoComAchievementService;
import de.tud.kom.socom.web.client.sharedmodels.AchievementGame;
import de.tud.kom.socom.web.server.database.achievement.AchievementDatabaseAccess;
import de.tud.kom.socom.web.server.database.achievement.HSQLAchievementDatabaseAccess;
import de.tud.kom.socom.web.server.util.LoggerFactory;

@SuppressWarnings("serial")
public class SoComAchievementServiceImpl extends SoComService implements
		SoComAchievementService {

	private final AchievementDatabaseAccess db = HSQLAchievementDatabaseAccess.getInstance();
	
	@Override
	public List<AchievementGame> getGames(long userID) {
		List<AchievementGame> games = new ArrayList<AchievementGame>();
		try {
			games = db.getGames(userID);
		} catch (Exception e) {
			LoggerFactory.getLogger().Error(e);
		}
		return games;
	}

}
