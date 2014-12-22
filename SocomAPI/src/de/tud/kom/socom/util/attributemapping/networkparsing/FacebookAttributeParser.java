package de.tud.kom.socom.util.attributemapping.networkparsing;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.tud.kom.socom.util.LoggerFactory;
import de.tud.kom.socom.util.attributemapping.Attribute;
import de.tud.kom.socom.util.attributemapping.AttributeMap;
import de.tud.kom.socom.util.attributemapping.ListAttribute;
import de.tud.kom.socom.util.attributemapping.ObjectAttribute;
import de.tud.kom.socom.util.attributemapping.UniqueAttribute;

public class FacebookAttributeParser extends AttributeParser {

	public FacebookAttributeParser(AttributeMap atts) {
		super(atts);
	}

	@Override
	protected String translate(String key) {
		return trans.get(key);
	}

	@Override
	public void parseAttributes(JSONObject json) {
		try {
			@SuppressWarnings("rawtypes")
			Iterator i = json.keys();
			while (i.hasNext()) {
				String current = (String) i.next();
				if (trans.containsKey(current)) {
					Attribute att = parseAttribute(json.get(current));
					atts.addAttribute(current, att);
				}
			}
		} catch (JSONException e) {
			LoggerFactory.getLogger().Error(e);
		}
	}

	private Attribute parseAttribute(Object json) throws JSONException {
		if (json instanceof String) {
			return new UniqueAttribute((String) json);

		} else if (json instanceof JSONArray) {
			JSONArray array = (JSONArray) json;
			ListAttribute list = new ListAttribute();

			for (int i = 0; i < array.length(); i++) {
				Attribute a = parseAttribute(array.get(i));
				list.addAttribute(a);
			}
			return list;

		} else if (json instanceof JSONObject) {
			JSONObject jsonObject = (JSONObject) json;
			ObjectAttribute object = new ObjectAttribute();
			@SuppressWarnings("rawtypes")
			Iterator i = jsonObject.keys();
			while (i.hasNext()) {
				String current = (String) i.next();
				if (trans.containsKey(current)) {
					Attribute att = parseAttribute(jsonObject.get(current));
					object.addAttribute(translate(current), att);
				}
			}
			return object;
		}
		return null;
	}

	protected void fillTranslations() {
		trans.put("name", "name");
		trans.put("first_name", "first_name");
		trans.put("last_name", "last_name");
		trans.put("gender", "gender");
		trans.put("locale", "locale");
		trans.put("languages", "languages");
		trans.put("bio", "about");
		trans.put("birthday", "birthday");
		trans.put("education", "education");
		trans.put("email", "email");
		trans.put("hometown", "hometown");
		trans.put("favorite_athletes", "favorite_athletes");
		trans.put("favorite_teams", "favorite_teams");
		trans.put("relationship_status", "relationship_status");
		trans.put("website", "website");
		trans.put("work", "work");
	}
}
