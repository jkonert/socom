package de.tud.kom.socom.util.datatypes;

import java.io.Serializable;
import java.util.Date;
import java.text.DateFormat;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

import de.tud.kom.socom.util.JSONUtils;
import de.tud.kom.socom.util.LoggerFactory;

public class JournalEntry implements JSONString, Serializable {
	private static final long serialVersionUID = 7204980530813380L;
	private String type;
	private String message;
	private Date time;
	private int visibility;

	public JournalEntry(String type, String message, int visibility) {
		this.type = type.toUpperCase();
		this.message = message;
		this.visibility = visibility;
	}

	public String getType() {
		return type;
	}

	public String getMessage() {
		return message;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public Date getTime() {
		return this.time;
	}

	public int getVisibility() {
		return visibility;
	}

	@Override
	public String toJSONString() {
		try {
			JSONObject json = new JSONObject();
			json.put("type", type);
			json.put("message", message);
			if (this.time != null)
				json.put("time", DateFormat.getInstance().format(time));
			return JSONUtils.JSONToString(json);
		} catch (JSONException e) {
			LoggerFactory.getLogger().Error(e.getMessage());
		}
		return null;
	}
}