package de.tud.kom.socom.web.server.database.achievement;

import java.sql.SQLException;
import java.util.List;

import de.tud.kom.socom.web.client.sharedmodels.AchievementGame;

public interface AchievementDatabaseAccess {
	public List<AchievementGame> getGames(long userID) throws SQLException;
}
