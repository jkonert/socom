package de.tud.kom.socom.components.statistics;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

import de.tud.kom.socom.util.JSONUtils;
import de.tud.kom.socom.util.LoggerFactory;

public class GameStatistic implements JSONString {

	private String gamename;
	private long gameid;
	private long instanceCount, usersPlaying, usersCurrentlyPlaying, contentCount;
	private List<GameStatistic.ShortGameInstanceStatistic> instances;

	public GameStatistic(String gamename, long gameid, long instanceCount, long userPlaying, long usersCurrentlyPlaying, long contentCount) {
		super();
		this.gamename = gamename;
		this.gameid = gameid;
		this.instanceCount = instanceCount;
		this.usersPlaying = userPlaying;
		this.usersCurrentlyPlaying = usersCurrentlyPlaying;
		this.contentCount = contentCount;
		instances = new LinkedList<GameStatistic.ShortGameInstanceStatistic>();
	}

	public String getGamename() {
		return gamename;
	}

	public long getGameid() {
		return gameid;
	}

	public long getInstanceCount() {
		return instanceCount;
	}

	public void setInstanceCount(long instanceCount) {
		this.instanceCount = instanceCount;
	}
	
	public long getUserPlaying() {
		return usersPlaying;
	}

	public long getUsersCurrentlyPlaying() {
		return usersCurrentlyPlaying;
	}

	public long getContentCount() {
		return contentCount;
	}
	
	public List<ShortGameInstanceStatistic> getInstances() {
		return instances;
	}
	
	public void addGameInstanceStatistic(ShortGameInstanceStatistic instance) {
		instances.add(instance);
	}

	@Override
	public String toJSONString() {
		try {
			JSONObject json = new JSONObject();
			json.put("name", gamename);
			json.put("id", gameid);
			json.put("instanceCount", instanceCount);
			json.put("usersPlaying", usersPlaying);
			json.put("usersCurrentlyPlaying", usersCurrentlyPlaying);
			json.put("contentCount", contentCount);
			json.put("instances", new JSONArray(getInstances()));
			return JSONUtils.JSONToString(json);
		} catch (JSONException e) {
			LoggerFactory.getLogger().Error(e);
		}
		return null;
	}

	public class ShortGameInstanceStatistic implements JSONString {
		private String instanceName, version;
		private long usersCurrentlyPlaying, id;

		public ShortGameInstanceStatistic(String instanceDescription, long id, String version, long usersCurrentlyPlaying) {
			this.instanceName = instanceDescription;
			this.id = id;
			this.version = version;
			this.usersCurrentlyPlaying = usersCurrentlyPlaying;
		}

		public String getInstancenName() {
			return instanceName;
		}

		public long getExternalid() {
			return id;
		}

		public String getVersion() {
			return version;
		}

		public long getUsersCurrentlyPlaying() {
			return usersCurrentlyPlaying;
		}

		@Override
		public String toJSONString() {
			try {
				JSONObject json = new JSONObject();
				json.put("description", instanceName);
				json.put("version", version);
				json.put("id", id);
				json.put("usersCurrentlyPlaying", usersCurrentlyPlaying);
				return JSONUtils.JSONToString(json);
			} catch (JSONException e) {
				LoggerFactory.getLogger().Error(e);
			}
			return null;
		}
	}
}
