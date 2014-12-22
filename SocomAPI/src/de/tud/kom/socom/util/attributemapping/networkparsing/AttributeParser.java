package de.tud.kom.socom.util.attributemapping.networkparsing;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import de.tud.kom.socom.util.attributemapping.Attribute;
import de.tud.kom.socom.util.attributemapping.AttributeMap;

public abstract class AttributeParser {
	protected Map<String, String> trans;
	protected AttributeMap atts;
	
	public AttributeParser(AttributeMap atts){
		this.atts = atts;
		trans = new HashMap<String, String>();
		fillTranslations();
	}
	
	protected void addAttribute(String key, Attribute value){
		String newKey = translate(key);
		atts.addAttribute(newKey, value);
	}
	
	protected String translate(String key) {
		return trans.get(key);
	}

	protected abstract void fillTranslations(); 
	public abstract void parseAttributes(JSONObject json);
}