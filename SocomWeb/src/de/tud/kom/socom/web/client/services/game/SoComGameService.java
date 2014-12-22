package de.tud.kom.socom.web.client.services.game;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import de.tud.kom.socom.web.client.sharedmodels.GameContext;
import de.tud.kom.socom.web.client.sharedmodels.GameInstance;

@RemoteServiceRelativePath("game")
public interface SoComGameService extends RemoteService {

	public String getGameName(long gid);

	public List<GameInstance> getGameInstances(long uid, int page);

	public int getGameInstancesPages(long userId);

	public GameContext getGameContextNames(long sid);

	public List<GameContext> getGameContexts(long userId, long gameId, int page);

	public int getGameContextsPages(long userId, long gameId);

	public List<GameContext> getContextHistory(long userId, long gameInstId);

	public boolean registerGameHit(long gid);

	public boolean isUserPlayingGame(long userId, long gameId);

	public String[][] getAllGames();
	
	public boolean isGameValid(String gameident);
}
