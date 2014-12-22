package de.tud.kom.socom.util;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONUtils {

	public static String JSONToString(JSONObject json) {
//		return json.toString(1);
		return json.toString();
	}

	public static String getSuccessJsonString(boolean success) {
		try {
			return JSONToString(new JSONObject().put("success", success));
		} catch (JSONException e) {
			LoggerFactory.getLogger().Error(e);
			return null;
		}
	}

	public static String getSuccessJsonString(){
		return getSuccessJsonString(true);
	}
}
