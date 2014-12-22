package de.tud.kom.socom.web.server;

import java.util.ArrayList;
import java.util.List;

import de.tud.kom.socom.web.client.services.game.SoComGameService;
import de.tud.kom.socom.web.client.sharedmodels.GameContext;
import de.tud.kom.socom.web.client.sharedmodels.GameInstance;
import de.tud.kom.socom.web.server.database.game.GameDatabaseAccess;
import de.tud.kom.socom.web.server.database.game.HSQLGameDatabaseAccess;
import de.tud.kom.socom.web.server.database.user.HSQLUserDatabaseAccess;
import de.tud.kom.socom.web.server.util.Logger;
import de.tud.kom.socom.web.server.util.LoggerFactory;

@SuppressWarnings("serial")
public class SoComGameServiceImpl extends SoComService implements SoComGameService {

	private final GameDatabaseAccess db = HSQLGameDatabaseAccess.getInstance();
	private Logger logger = LoggerFactory.getLogger();

	@Override
	public String[][] getAllGames() {
		return db.getAllGames();
	}
	
	@Override
	public String getGameName(long gid) {
		String result = "";
		try {
			result = db.getGameName(gid);
		} catch (Exception e) {
			logger.Error(e);
		}
		return result;
	}

	@Override
	public List<GameInstance> getGameInstances(long uid, int page) {

		List<GameInstance> result = new ArrayList<GameInstance>();
		try {
			if(page >= 0) {
				result = db.getGameInstances(uid, page);
			} else {
				result = db.getGameInstances(uid);
			}
		} catch (Exception e) {
			logger.Error(e);
		}
		return result;
	}

	@Override
	public int getGameInstancesPages(long userId) {
		int result = 0;
		try {
			result = db.getGameInstancesPages(userId);
		} catch (Exception e) {
			logger.Error(e);
		}
		return result;
	}

	@Override
	public GameContext getGameContextNames(long sid) {
		GameContext result = null;
		try {
			result = db.getGameContextNames(sid);
		} catch (Exception e) {
			logger.Error(e);
		}
		return result;
	}

	@Override
	public List<GameContext> getGameContexts(long userId, long gameId, int page) {

		List<GameContext> result = new ArrayList<GameContext>();
		try {
			result = db.getGameContexts(userId, gameId, page);
		} catch (Exception e) {
			logger.Error(e);
		}
		return result;
	}

	@Override
	public int getGameContextsPages(long userId, long gameId) {
		int result = 0;
		try {
			result = db.getGameContextsPages(userId, gameId);
		} catch (Exception e) {
			logger.Error(e);
		}
		return result;
	}

	@Override
	public List<GameContext> getContextHistory(long userId, long gameInstId) {
		return HSQLUserDatabaseAccess.getInstance().getUserHistory(userId, gameInstId);
	}
	
	@Override
	public boolean registerGameHit(long gid) {
		return db.registerGameHit(gid);
	}

	@Override
	public boolean isUserPlayingGame(long userId, long gameId) {
		return db.isUserPlayingGame(userId, gameId);
	}

	@Override
	public boolean isGameValid(String gameident) {
		boolean valid = db.isGameIdentValid(gameident);
		return valid;
	}
}
