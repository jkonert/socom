package de.tud.kom.socom.database.statistics;

import java.sql.ResultSet;
import java.sql.SQLException;

import de.tud.kom.socom.components.statistics.GameInstanceStatistic;
import de.tud.kom.socom.components.statistics.GameStatistic;
import de.tud.kom.socom.components.statistics.SoComStatistic;
import de.tud.kom.socom.components.statistics.GameInstanceStatistic.GameContextStatistic;
import de.tud.kom.socom.components.statistics.GameStatistic.ShortGameInstanceStatistic;
import de.tud.kom.socom.database.HSQLDatabase;
import de.tud.kom.socom.util.exceptions.GameInstanceNotFoundException;
import de.tud.kom.socom.util.exceptions.GameNotAuthenticatedException;
import de.tud.kom.socom.util.exceptions.SocomException;

public class HSQLStatisticDatabase extends HSQLDatabase implements StatisticDatabase {

	private static StatisticDatabase instance = new HSQLStatisticDatabase();

	private HSQLStatisticDatabase() {
		super();
	}

	public static StatisticDatabase getInstance() {
		return instance;
	}

	@Override
	public SoComStatistic getSoComStats() throws SQLException {
		String query = "SELECT " +
					"(SELECT COUNT(uid) FROM users) AS usercount, " +
					"(SELECT COUNT(uid) FROM users WHERE currentState > 0) AS usersOnline, " +
					"(SELECT COUNT(gameid) FROM games) AS gameCount, " +
					"(SELECT COUNT(id) FROM gameinstances) AS instanceCount, " +
					"(SELECT COUNT(id) FROM gamecontexts) AS contextCount, " +
					"(SELECT COUNT(id) FROM gamecontent WHERE content IS NOT NULL AND secretident IS NULL) AS contentCount, " +
					"(SELECT COUNT(id) FROM influence WHERE timeout > '1970-01-01 00:00:00.1') AS influenceCount, " +
					"(SELECT COUNT(userid) FROM achievementprogress WHERE iscompleted) AS achievementsUnlockedTotal, " +
					"(SELECT SUM(playtime) FROM userprogress) AS totalTimePlayed " +
				"FROM games";

		ResultSet rs = db.execQueryWithResult(query);
		if(!rs.next())
			return null;
		
		long userCount = rs.getLong("usercount");
		long userOnlineCount = rs.getLong("usersOnline");
		long gameCount = rs.getLong("gameCount");
		long gameInstanceCount = rs.getLong("instanceCount");
		long gameContextsCount = rs.getLong("contextCount");
		long contentCount = rs.getLong("contentCount");
		long influenceCount = rs.getLong("influenceCount");
		long achievementsUnlockedCount = rs.getLong("achievementsUnlockedTotal");
		long totalTimePlayed = rs.getLong("totalTimePlayed");
		long averageTimePlayedPerUser = totalTimePlayed / userCount;
		
		SoComStatistic stats = new SoComStatistic(userCount, userOnlineCount, gameCount, gameInstanceCount, gameContextsCount, 
				contentCount, influenceCount, achievementsUnlockedCount, totalTimePlayed, averageTimePlayedPerUser);
		return stats;
	}

	@Override
	public GameStatistic getGameStats(String game, String password) throws SQLException, GameNotAuthenticatedException {
		String query;
		ResultSet rs;
		long gameid = findGame(game, password);
		
		query = "SELECT	" +
					"SUM(CASE WHEN users.currentstate = 2 THEN 1 ELSE 0 END) AS userscurrentlyplaying, " +
					"COUNT(*) AS usersplaying, " +
					"(SELECT SUM(CASE WHEN games.gameid = " + gameid + " THEN 1 ELSE 0 END) FROM " +
						"((gamecontent INNER JOIN gamecontexts ON gamecontent.contextid = gamecontexts.id) " +
							"INNER JOIN gameinstances ON gamecontexts.gameinstid = gameinstances.id) " +
								"INNER JOIN games ON gameinstances.gameid = games.gameid " +
							") AS contentcount " +
				"FROM " +
					"(usergames INNER JOIN gameinstances ON usergames.gameinstanceid = gameinstances.id) " +
					"INNER JOIN users ON usergames.uid = users.uid " +
				"WHERE " +
					"gameid = " + gameid + ";";
		
		rs = db.execQueryWithResult(query);
		if(!rs.next())
			throw new GameNotAuthenticatedException(game);
		long userPlaying = rs.getLong("usersplaying");
		long usersCurrentlyPlaying = rs.getLong("userscurrentlyplaying");
		long contentCount = rs.getLong("contentcount");
		
		GameStatistic stats = new GameStatistic(game, gameid, -1, userPlaying, usersCurrentlyPlaying, contentCount);
		
		query = "SELECT " +
					"gameinstances.id, " +
					"gameinstances.version, " +
					"gameinstances.description, " +
					"COUNT(usergames.uid) AS usersplaying, " +
					"SUM(CASE WHEN users.currentstate = 2 THEN 1 ELSE 0 END) AS userscurrentlyplaying " +
				"FROM " +
					"((gameinstances INNER JOIN games ON gameinstances.gameid = games.gameid) " +
						"INNER JOIN usergames ON gameinstances.id = usergames.gameinstanceid) " +
							"INNER JOIN users ON users.uid = usergames.uid " +
				"WHERE " +
					"games.gameid = 0 " +
				"GROUP BY " +
					"id, " +
					"version, " +
					"description;";
		rs = db.execQueryWithResult(query);
		int instanceCount = 0;
		
		while(rs.next()){
			String description = rs.getString("description");
			String version = rs.getString("version");
			long id = rs.getLong("id");
			long currentlyPlaying = rs.getLong("userscurrentlyplaying");
			ShortGameInstanceStatistic instStat = stats.new ShortGameInstanceStatistic(description, id, version, currentlyPlaying);
			stats.addGameInstanceStatistic(instStat);
			instanceCount++;
		}
		
		stats.setInstanceCount(instanceCount);
		return stats;
	}

	@Override
	public GameInstanceStatistic getInstanceStats(String gamename, String password, String version) throws SQLException, SocomException {
		long gameid = findGame(gamename, password);
		String query = "SELECT " +
				"id, " +
				"description " +
			"FROM " +
				"gameinstances " +
			"WHERE " +
				"gameid = " + gameid + " AND version = '" + version+ "';";
		ResultSet rs = db.execQueryWithResult(query);
		if(!rs.next())
			throw new GameInstanceNotFoundException(gamename, version);
		String description = rs.getString("description");
		long instanceid = rs.getLong("id");
		
		query = "SELECT " +
					"DISTINCT userprogress.uid, " +
					"users.currentstate " +
				"FROM " +
					"(userprogress LEFT JOIN gamecontexts ON gamecontexts.id = userprogress.scnid) " +
						"LEFT JOIN users ON users.uid = userprogress.uid " +
				"WHERE " +
					"gameinstid = " + instanceid + ";";
		rs = db.execQueryWithResult(query);
		long usersplaying = 0L, userscurrentlyplaying = 0L;
		
		while(rs.next()) {
			usersplaying++;
			if(rs.getInt("currentstate") == 2)
				userscurrentlyplaying++;
		}
		
		GameInstanceStatistic stat = new GameInstanceStatistic(gamename, version, description, instanceid, usersplaying, userscurrentlyplaying);
		addContexts(instanceid, stat);
		return stat;
	}

	private void addContexts(long instanceid, GameInstanceStatistic stat) throws SQLException {
		String query = "SELECT " +
					"gamecontexts.name, " +
					"gamecontexts.externalid, " +
					"gamecontexts.autogenerated, " +
					"gamecontexts.id, " +
					"COUNT(DISTINCT gamecontent.id) AS contentcount, " +
					"SUM(DISTINCT gamecontent.hits) AS contenthitstotal, " +
					"COUNT(DISTINCT influence.id) AS influencecount, " +
					"COUNT(DISTINCT userprogress.uid) AS usersseen, " +
					"SUM(userprogress.playtime) AS timespenttotal " +
				"FROM " +
					"((gamecontexts LEFT JOIN gamecontent ON gamecontent.contextid = gamecontexts.id) " +
						"LEFT JOIN influence ON influence.contextid = gamecontexts.id) " +
							"LEFT JOIN userprogress ON userprogress.scnid = gamecontexts.id " +
				"WHERE " +
					"gamecontexts.gameinstid = " + instanceid + " " +
				"GROUP BY " +
					"name, " +
					"externalid," +
					"autogenerated, " +
					"id;";
		ResultSet rs = db.execQueryWithResult(query);
		while(rs.next()) {
			long id = rs.getLong("id");
			boolean autogenerated = rs.getBoolean("autogenerated");
			String name = rs.getString("name");
			long timeSpentTotal = rs.getLong("timespenttotal");
			long usersSeen = rs.getLong("usersseen");
			long timeSpentAvg = usersSeen > 0 ? timeSpentTotal / usersSeen : 0;
			long contentCount = rs.getLong("contentcount");
			long influenceCount = rs.getLong("influenceCount");
			long contentHits = rs.getLong("contenthitstotal");
			GameContextStatistic contextstat = stat.new GameContextStatistic(id, autogenerated, name, timeSpentTotal, timeSpentAvg, 
					usersSeen, contentCount, influenceCount, contentHits);
			addRelations(id, contextstat);			
			stat.addContextStat(contextstat);
		}
	}

	private void addRelations(long contextid, GameContextStatistic contextstat) throws SQLException {
		String query = "SELECT * FROM gamecontextrelations WHERE parentid = " + contextid;
		ResultSet rs = db.execQueryWithResult(query);
		while(rs.next()) {
			long source = contextid;
			long dest = rs.getLong("childid");
			long timesUsed = rs.getLong("timesused");
			boolean autogenerated = rs.getBoolean("autogenerated");
			contextstat.addToRelation(contextstat.new ContextRelationStatistic(source, dest, timesUsed, autogenerated));
		}
		query = "SELECT * FROM gamecontextrelations WHERE childid = " + contextid;
		rs = db.execQueryWithResult(query);
		while(rs.next()) {
			long source = rs.getLong("parentid");;
			long dest = contextid;
			long timesUsed = rs.getLong("timesused");
			boolean autogenerated = rs.getBoolean("autogenerated");
			contextstat.addFromRelation(contextstat.new ContextRelationStatistic(source, dest, timesUsed, autogenerated));
		}
	}

	private long findGame(String game, String password) throws SQLException, GameNotAuthenticatedException {
		String query = "SELECT name, gameid FROM games WHERE name = '" + game + "' AND password = '" + password + "';";
		ResultSet rs = db.execQueryWithResult(query);
		if(!rs.next())
			throw new GameNotAuthenticatedException(game);
		long gameid = rs.getLong("gameid");
		return gameid;
	}
}

















