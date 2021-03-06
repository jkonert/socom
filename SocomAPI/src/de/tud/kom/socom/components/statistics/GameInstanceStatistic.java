package de.tud.kom.socom.components.statistics;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

import de.tud.kom.socom.util.JSONUtils;

public class GameInstanceStatistic implements JSONString {

	private String gamename, version, description;
	private long id, usersplaying, userscurrentlyplaying;
	private List<GameInstanceStatistic.GameContextStatistic> contextStats;

	public GameInstanceStatistic(String gamename, String version, String description, long id, long usersplaying, long userscurrentlyplaying) {
		super();
		this.gamename = gamename;
		this.version = version;
		this.description = description;
		this.id = id;
		this.usersplaying = usersplaying;
		this.userscurrentlyplaying = userscurrentlyplaying;
		this.contextStats = new LinkedList<GameInstanceStatistic.GameContextStatistic>();
	}

	public void addContextStat(GameContextStatistic stat) {
		this.contextStats.add(stat);
	}

	@Override
	public String toJSONString() {
		JSONObject json = new JSONObject();
		try {
			json.put("gamename", gamename);
			json.put("version", version);
			json.put("description", description);
			json.put("instanceid", id);
			json.put("usersplaying", usersplaying);
			json.put("userscurrentlyplaying", userscurrentlyplaying);
			json.put("contexts", contextStats);
			return JSONUtils.JSONToString(json);
		} catch (JSONException e) {
			return null;
		}
	}

	public class GameContextStatistic implements JSONString {
		private long id;
		private List<ContextRelationStatistic> fromRelations, toRelations;
		private boolean autogenerated;
		private String name;
		private long timeSpentTotal, timeSpentAvg, usersSeen, contentCount, influenceCount, contentHits;

		public GameContextStatistic(long id, boolean autogenerated, String name, long timeSpentTotal, long timeSpentAvg, long usersSeen,
				long contentCount, long influenceCount, long contentHits) {
			super();
			this.id = id;
			this.fromRelations = new LinkedList<GameInstanceStatistic.GameContextStatistic.ContextRelationStatistic>();
			this.toRelations = new LinkedList<GameInstanceStatistic.GameContextStatistic.ContextRelationStatistic>();
			this.autogenerated = autogenerated;
			this.name = name;
			this.timeSpentTotal = timeSpentTotal;
			this.timeSpentAvg = timeSpentAvg;
			this.usersSeen = usersSeen;
			this.contentCount = contentCount;
			this.influenceCount = influenceCount;
			this.contentHits = contentHits;
		}

		public void addFromRelation(GameInstanceStatistic.GameContextStatistic.ContextRelationStatistic stat) {
			this.fromRelations.add(stat);
		}

		public void addToRelation(GameInstanceStatistic.GameContextStatistic.ContextRelationStatistic stat) {
			this.toRelations.add(stat);
		}

		@Override
		public String toJSONString() {
			JSONObject json = new JSONObject();
			try {
				json.put("id", id);
				json.put("relations", new JSONObject().put("from", fromRelations).put("to", toRelations));
				json.put("autogenerated", autogenerated);
				json.put("name", name);
				json.put("timespenttotal", timeSpentTotal);
				json.put("timespendavg", timeSpentAvg);
				json.put("usersseen", usersSeen);
				json.put("contentcount", contentCount);
				json.put("contenthits", contentHits);
				json.put("influencecount", influenceCount);
				return JSONUtils.JSONToString(json);
			} catch (JSONException e) {
				return null;
			}
		}

		public class ContextRelationStatistic implements JSONString {
			private long source, dest, timesUsed;
			private boolean autogenerated;

			public ContextRelationStatistic(long source, long dest, long timesUsed, boolean autogenerated) {
				this.source = source;
				this.dest = dest;
				this.timesUsed = timesUsed;
				this.autogenerated = autogenerated;
			}

			@Override
			public String toJSONString() {
				JSONObject json = new JSONObject();
				try {
					json.put("parent", source);
					json.put("child", dest);
					json.put("timesused", timesUsed);
					json.put("autogenerated", autogenerated);
					return JSONUtils.JSONToString(json);
				} catch (JSONException e) {
					return null;
				}
			}
		}
	}
}
