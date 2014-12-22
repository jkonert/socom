package de.tud.kom.socom.components.game;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

import de.tud.kom.socom.util.JSONUtils;
import de.tud.kom.socom.util.LoggerFactory;

/**
 * 
 * @author rhaban
 * 
 */
public class Game implements JSONString {
	private long id;
	private String name, genre, password;
	private List<GameInstance> instances;

	public Game(long id, String name, String genre, String password, List<GameInstance> instances) {
		this.id = id;
		this.name = name;
		this.genre = genre;
		this.password = password;
		this.instances = instances;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getGenre() {
		return genre;
	}

	public String getPassword() {
		return password;
	}

	public List<GameInstance> getInstances() {
		return instances;
	}

	public void addInstance(GameInstance instance) {
		instances.add(instance);
	}

	@Override
	public String toJSONString() {
		JSONObject json = new JSONObject();
		try {
			json.put("id", id);
			json.put("game", name);
			json.put("genre", genre);
			json.put("instances", instances);

			return JSONUtils.JSONToString(json);
		} catch (JSONException e) {
			LoggerFactory.getLogger().Error(e);
		}
		return JSONUtils.JSONToString(json);
	}
}
