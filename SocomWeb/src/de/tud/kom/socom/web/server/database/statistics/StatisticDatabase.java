package de.tud.kom.socom.web.server.database.statistics;

import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;


public interface StatisticDatabase {

	public JSONObject getGameGraphJSON(long instanceid) throws SQLException, JSONException;


}
