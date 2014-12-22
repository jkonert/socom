package de.tud.kom.socom.util.attributemapping;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import de.tud.kom.socom.util.JSONUtils;

public class ObjectAttribute implements Attribute {
	
	private Map<String, Attribute> object;

	public ObjectAttribute() {
		this.object = new HashMap<String, Attribute>();
	}
	
	public void addAttribute(String key, Attribute value){
		this.object.put(key, value);
	}
	
	public void addAllAttributes(ObjectAttribute object){
		this.object.putAll(object.getObject());
	}

	public Map<String, Attribute> getObject() {
		return object;
	}

	@Override
	public String toJSONString() {
		return JSONUtils.JSONToString(new JSONObject(object));
	}
}
