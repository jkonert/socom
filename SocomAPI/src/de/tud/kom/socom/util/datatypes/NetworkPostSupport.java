package de.tud.kom.socom.util.datatypes;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

import de.tud.kom.socom.util.JSONUtils;
import de.tud.kom.socom.util.LoggerFactory;

public class NetworkPostSupport implements JSONString {

	private long count;
	private List<PostSupporters> supporters;

	public NetworkPostSupport(long count) {
		this.count = count;
		this.supporters = new LinkedList<NetworkPostSupport.PostSupporters>();
	}

	public NetworkPostSupport(long count, List<PostSupporters> supporters) {
		this.count = count;
		this.supporters = supporters;
	}

	public void addSupporter(PostSupporters sup) {
		this.supporters.add(sup);
	}

	public long getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public List<PostSupporters> getSupporters() {
		return supporters;
	}

	public void setSupporters(List<PostSupporters> supporters) {
		this.supporters = supporters;
	}

	public class PostSupporters implements JSONString {
		private String name;
		private String id;

		public PostSupporters(String name, String id) {
			super();
			this.name = name;
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public String getId() {
			return id;
		}

		@Override
		public String toJSONString() {
			try {
				return JSONUtils.JSONToString(new JSONObject().put("name", name).put("id", id));
			} catch (JSONException e) {
				LoggerFactory.getLogger().Error(e);
				return null;
			}
		}
	}

	@Override
	public String toJSONString() {
		try {
			JSONObject json = new JSONObject();
			json.put("count", count);
			json.put("supporters", new JSONArray(supporters));
			return JSONUtils.JSONToString(json);
		} catch (JSONException e) {
			LoggerFactory.getLogger().Error(e);
			return null;
		}
	}

}
