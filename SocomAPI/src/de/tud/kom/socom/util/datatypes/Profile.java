package de.tud.kom.socom.util.datatypes;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

import de.tud.kom.socom.util.JSONUtils;
import de.tud.kom.socom.util.LoggerFactory;

public class Profile implements JSONString {
	String name, network, snuid;
	Map<String, String> attributes;


	/**
	 * Represents a Person out of a Social Network (e.g. if you fetch friends of
	 * the user you get List<Person>
	 * 
	 * @param fname
	 *            fistName
	 * @param lname
	 *            lastName
	 * @param attributes
	 *            attributes offered by the network, e.g. "hometown" -
	 *            "Darmstadt"
	 * @param network
	 *            network id, shows where the person is taken from
	 * @param networkID
	 *            person-specific ID inside the given network, here you can find
	 *            the person
	 */
	public Profile(String name, Map<String, String> attributes, String network, String networkID) {
		this.name = name;
		this.attributes = attributes;
		this.network = network;
		this.snuid = networkID;

	}

	public Profile(String name, String network, String networkID) {
		this.name = name;
		this.attributes = null;
		this.network = network;
		this.snuid = networkID;

	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}

	public void addAllAttributes(Map<String, String> att) {
		if (this.attributes == null)
			setAttributes(att);
		else
			this.attributes.putAll(att);
	}

	public void addAttribute(String key, String val) {
		if (this.attributes == null)
			this.attributes = new HashMap<String, String>();
		this.attributes.put(key, val);
	}

	/**
	 * @return the attributes offered by the network, e.g. "hometown" -
	 *         "Darmstadt"
	 */
	public Map<String, String> getAttributes() {
		return attributes;
	}

	/**
	 * @return the network id shows where the person is taken from
	 */
	public String getNetwork() {
		return network;
	}

	/**
	 * @return the networkID: person-specific ID inside the given network, here
	 *         you can find the person
	 */
	public String getNetworkID() {
		return snuid;
	}

	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getName()).append(" (").append(getNetworkID()).append(")\n");
		if (attributes != null)
			for (String s : attributes.keySet())
				sb.append(s).append(attributes.get(s)).append("\n");

		return sb.toString();
	}

	public String toJSONString() {
		JSONObject json = new JSONObject();
		try {
			json.put("name", name);
			json.put("network", network.toString());
			json.put("snuid", snuid);
			if (attributes != null)
				json.put("attributes", attributes);
		} catch (JSONException e) {
			LoggerFactory.getLogger().Error(e.getMessage());
		}
		return JSONUtils.JSONToString(json);
	}
}