package de.tud.kom.socom.util.attributemapping;

import org.json.JSONObject;

public class UniqueAttribute implements Attribute{
	
	private String value;

	public UniqueAttribute(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toJSONString() {
		return JSONObject.quote(value);
	}
}
