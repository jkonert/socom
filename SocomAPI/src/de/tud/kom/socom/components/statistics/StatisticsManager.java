package de.tud.kom.socom.components.statistics;

import java.sql.SQLException;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import de.tud.kom.socom.SocomComponent;
import de.tud.kom.socom.SocomCore;
import de.tud.kom.socom.database.statistics.HSQLStatisticDatabase;
import de.tud.kom.socom.database.statistics.StatisticDatabase;
import de.tud.kom.socom.util.SocomRequest;
import de.tud.kom.socom.util.JSONUtils;
import de.tud.kom.socom.util.exceptions.SocomException;

/**
 * 
 * @author rhaban
 *
 */
public class StatisticsManager extends SocomComponent {

	private static final String URL_PATTERN = "stats";
	private static StatisticsManager instance = new StatisticsManager();
	private static StatisticDatabase db;
	
	private StatisticsManager(){
		db = HSQLStatisticDatabase.getInstance();
	}
	
	public static StatisticsManager getInstance() {
		return instance;
	}

	public int getSocomStatistic(SocomRequest req) throws JSONException, SQLException {
		SoComStatistic stat = db.getSoComStats();
		JSONObject json = new JSONObject();
		json.put("timestamp", SocomCore.getDateFormat().format(new Date()));
		json.put("statistic", stat);
		req.addOutput(JSONUtils.JSONToString(json));
		return 0;
	}
	
	public int getGameStatistic(SocomRequest req) throws SocomException, SQLException, JSONException  {
		String game = req.getParam("game");
		String password = req.getParam("password");
		GameStatistic stats = db.getGameStats(game, password);
		JSONObject json = new JSONObject();
		json.put("timestamp", SocomCore.getDateFormat().format(new Date()));
		json.put("statistic", stats);
		req.addOutput(JSONUtils.JSONToString(json));
		return 0;
	}
	
	public int getGameInstanceStatistic(SocomRequest req) throws SocomException, SQLException, JSONException {
		String game = req.getParam("game");
		String password = req.getParam("password");
		String version = req.getParam("version");
		
		GameInstanceStatistic stat = db.getInstanceStats(game, password, version);
		
		JSONObject json = new JSONObject();
		json.put("timestamp", SocomCore.getDateFormat().format(new Date()));
		json.put("statistic", stat);
		req.addOutput(JSONUtils.JSONToString(json));
		return 0;
	}
	
	
	@Override
	public String getUrlPattern() {
		return URL_PATTERN;
	}

}
