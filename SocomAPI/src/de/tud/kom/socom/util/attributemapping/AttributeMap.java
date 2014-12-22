package de.tud.kom.socom.util.attributemapping;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.json.JSONString;

import de.tud.kom.socom.util.JSONUtils;

public class AttributeMap implements JSONString{

	private Map<String, Attribute> attributes;

	public AttributeMap() {
		attributes = new HashMap<String, Attribute>();
		fillMap();
	}

	private void fillMap() {
		attributes.put("name", null);
		attributes.put("first_name", null);
		attributes.put("last_name", null);
		attributes.put("birthday", null);
		attributes.put("about", null);
		attributes.put("favorite_athletes", null);
		attributes.put("education", null);
		attributes.put("gender", null);
		attributes.put("relationship_status", null);
		attributes.put("locale", null);
		attributes.put("languages", null);
		attributes.put("email", null);
		attributes.put("hometown", null);
		attributes.put("favorite_teams", null);
		attributes.put("website", null);
		attributes.put("work", null);
	}

	public void addAttribute(String key, Attribute attribute) {
		if (attribute instanceof UniqueAttribute) {
			if (attributes.get(key) != null)
				return; // use first
			attributes.put(key, attribute);

		} else if (attribute instanceof ListAttribute) {
			if (attributes.get(key) != null && attributes.get(key) instanceof ListAttribute) {
				((ListAttribute) attributes.get(key)).addAllAttributes((ListAttribute) attributes);
			} else {
				attributes.put(key, attribute);
			}

		} else if (attribute instanceof ObjectAttribute) {
			if (attributes.get(key) != null && attributes.get(key) instanceof ObjectAttribute) {
				((ObjectAttribute) attributes.get(key)).addAllAttributes((ObjectAttribute) attribute);
			} else {
				attributes.put(key, attribute);
			}
		}
	}

	@Override
	public String toJSONString() {
			return JSONUtils.JSONToString(new JSONObject(attributes));
	}
}
