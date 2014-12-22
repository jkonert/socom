package de.tud.kom.socom.web.client;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;

public class Helper {

	public static JSONObject findContextData(JSONArray contexts, Long current) {
		for(int i = 0; i < contexts.size(); i++) {
			if(((long)contexts.get(i).isObject().get("id").isNumber().doubleValue()) == current)
				return contexts.get(i).isObject().get("data").isObject();
		}
		return null;
	}
	
	public static long[] asArray(JSONArray array) {
		long[] result = new long[array.size()];
		for(int i = 0; i < array.size(); i++)
			result[i] = (long)array.get(i).isNumber().doubleValue();
		return result;
	}
}
