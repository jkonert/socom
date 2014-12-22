package de.tud.kom.socom.util.attributemapping.networkparsing;

import org.json.JSONObject;

import de.tud.kom.socom.util.attributemapping.AttributeMap;

public class GooglePlusAttributeParser extends AttributeParser {

	public GooglePlusAttributeParser(AttributeMap atts) {
		super(atts);
	}

	@Override
	protected void fillTranslations() {
		throw new RuntimeException("not yet implemented");
	}

	@Override
	public void parseAttributes(JSONObject json) {
		throw new RuntimeException("not yet implemented");
	}

}
