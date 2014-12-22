package de.tud.kom.socom.database.statistics;

import java.sql.SQLException;

import de.tud.kom.socom.components.statistics.GameInstanceStatistic;
import de.tud.kom.socom.components.statistics.GameStatistic;
import de.tud.kom.socom.components.statistics.SoComStatistic;
import de.tud.kom.socom.util.exceptions.GameNotAuthenticatedException;
import de.tud.kom.socom.util.exceptions.SocomException;

public interface StatisticDatabase {

	public SoComStatistic getSoComStats() throws SQLException;

	public GameStatistic getGameStats(String game, String password) throws SQLException, GameNotAuthenticatedException;

	public GameInstanceStatistic getInstanceStats(String game, String password, String version) throws SocomException, SQLException;
	
}
