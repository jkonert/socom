package de.tud.kom.socom.web.server;

import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;

import de.tud.kom.socom.web.client.services.statistic.SoComStatisticService;
import de.tud.kom.socom.web.client.sharedmodels.LoginResult;
import de.tud.kom.socom.web.server.database.statistics.HSQLStatisticDatabase;
import de.tud.kom.socom.web.server.database.statistics.StatisticDatabase;
import de.tud.kom.socom.web.server.util.Logger;
import de.tud.kom.socom.web.server.util.LoggerFactory;

@SuppressWarnings("serial")
public class SoComStatisticServiceImpl extends SoComService implements
		SoComStatisticService {

	private StatisticDatabase db = HSQLStatisticDatabase.getInstance();
	private Logger logger = LoggerFactory.getLogger();
	
	@Override
	public String getGraph(long instanceid, String sid) {
		LoginResult user = getCurrentUser(sid);
		if(!user.isAdmin()) return null;
		
		try {
			JSONObject graph = db.getGameGraphJSON(instanceid);
			return graph == null ? null : graph.toString();
		} catch (JSONException e) {
			logger.Error(e);
		} catch (SQLException e) {
			logger.Error(e);
		}
		return null;
	}
}
