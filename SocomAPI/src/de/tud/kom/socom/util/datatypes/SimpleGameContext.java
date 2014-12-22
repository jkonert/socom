package de.tud.kom.socom.util.datatypes;

import java.text.DateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

import de.tud.kom.socom.util.JSONUtils;
import de.tud.kom.socom.util.LoggerFactory;

public class SimpleGameContext implements JSONString {

	private String externalId;
	private Date seen;
	private long time;
	private String name;

	public SimpleGameContext(String externalId, Date lastSeen, long timeSeen, String name) {
		super();
		this.externalId = externalId;
		this.seen = lastSeen;
		this.time = timeSeen;
		this.name = name;
	}

	public String getExternalId() {
		return externalId;
	}

	public Date getLastSeen() {
		return seen;
	}

	public long getTimeSeen() {
		return time;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toJSONString() {
		JSONObject json = new JSONObject();
		try {
			json.put("externalid", externalId);
			json.put("seen", DateFormat.getInstance().format(seen));
			json.put("time", time);
			json.put("name", name);
		} catch (JSONException e) {
			LoggerFactory.getLogger().Error(e);
		}
		return JSONUtils.JSONToString(json);
	}
}
