package de.tud.kom.socom.web.client.services.game;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import de.tud.kom.socom.web.client.sharedmodels.GameContext;
import de.tud.kom.socom.web.client.sharedmodels.GameInstance;

public interface SoComGameServiceAsync {

	void getGameName(long gid, AsyncCallback<String> callback);

	void getGameInstances(long uid, int page, AsyncCallback<List<GameInstance>> callback);

	void getGameInstancesPages(long userId, AsyncCallback<Integer> asyncCallback);

	void getGameContextNames(long sid, AsyncCallback<GameContext> asyncCallback);

	void getGameContexts(long userId, long gameId, int page, AsyncCallback<List<GameContext>> callback);

	void getGameContextsPages(long userId, long gameId, AsyncCallback<Integer> callback);

	void getContextHistory(long userId, long gameInstId, AsyncCallback<List<GameContext>> asyncCallback);

	void registerGameHit(long gid, AsyncCallback<Boolean> callback);

	void isUserPlayingGame(long userId, long gameId, AsyncCallback<Boolean> asyncCallback);

	/**
	 * @param callback String array consist of game descriptions. First dimension contains different games, 
	 * second dimension has 5 elements: 1: Name, 2: identifier (for url), 3: url to image, 4: description, 
	 * 5: genre
	 */
	void getAllGames(AsyncCallback<String[][]> callback);

	void isGameValid(String gameident, AsyncCallback<Boolean> callback);

}
