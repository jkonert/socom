package de.tud.kom.socom.web.client.services.achievements;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import de.tud.kom.socom.web.client.sharedmodels.AchievementGame;

public interface SoComAchievementServiceAsync {
	void getGames(long userID, AsyncCallback<List<AchievementGame>> callback);
}
