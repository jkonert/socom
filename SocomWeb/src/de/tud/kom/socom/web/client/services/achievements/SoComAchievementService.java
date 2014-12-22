package de.tud.kom.socom.web.client.services.achievements;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import de.tud.kom.socom.web.client.sharedmodels.AchievementGame;

@RemoteServiceRelativePath("achievements")
public interface SoComAchievementService extends RemoteService {
	public List<AchievementGame> getGames(long userID);
}
