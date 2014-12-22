package de.tud.kom.socom.util.datatypes;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

import de.tud.kom.socom.SocomCore;
import de.tud.kom.socom.util.JSONUtils;
import de.tud.kom.socom.util.LoggerFactory;

public class UserMetadata implements JSONString {

	private Date updated;
	private String key, value;

	public UserMetadata(Date updated, String key, String value) {
		this.updated = updated;
		this.key = key;
		this.value = value;
	}

	@Override
	public String toJSONString() {
		try {
			JSONObject json = new JSONObject();
			json.put("key", key);
			json.put("value", value);
			json.put("updated", SocomCore.getDateFormat().format(updated));
			return JSONUtils.JSONToString(json);
		} catch (JSONException e) {
			LoggerFactory.getLogger().Error(e);
		}
		return null;
	}
}
