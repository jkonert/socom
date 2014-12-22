package de.tud.kom.socom.web.server.database.game;

import java.util.List;

import de.tud.kom.socom.web.client.sharedmodels.GameContext;
import de.tud.kom.socom.web.client.sharedmodels.GameInstance;

public interface GameDatabaseAccess {

	public String getGameName(long gid);

	public List<GameInstance> getGameInstances(long uid, int page);
	
	public List<GameInstance> getGameInstances(long uid);

	public int getGameInstancesPages(long userId);

	public GameContext getGameContextNames(long sid);

	public List<GameContext> getGameContexts(long userId, long gameId, int page);

	public int getGameContextsPages(long userId, long gameId);

	public boolean registerGameHit(long gid);

	public boolean isUserPlayingGame(long userId, long gameId);

	public String[][] getAllGames();

	public boolean isGameIdentValid(String gameident);

}
