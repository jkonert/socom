package de.tud.kom.socom.util.datatypes;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

import de.tud.kom.socom.util.JSONUtils;
import de.tud.kom.socom.util.LoggerFactory;

public class InfluenceConfiguration implements JSONString {

	private String externalid, question, type;
	private long gameInstanceId, contextId;
	private int minchoices, maxchoices, maxdigits, maxlines, maxbytes, visibility;
	private boolean allowFreeAnswers, freeAnswersVotable, isTemplate;

	public InfluenceConfiguration(String externalid, String question, String type, long gameInstanceId,
			long contextId, int minchoices, int maxchoices, int maxdigits, int maxlines, int maxbytes,
			int visibility, boolean allowFreeAnswers, boolean freeAnswersVotable, boolean isTemplate) {
		super();
		this.externalid = externalid;
		this.question = question;
		this.type = type;
		this.gameInstanceId = gameInstanceId;
		this.contextId = contextId;
		this.minchoices = minchoices;
		this.maxchoices = maxchoices;
		this.maxdigits = maxdigits;
		this.maxlines = maxlines;
		this.maxbytes = maxbytes;
		this.visibility = visibility;
		this.allowFreeAnswers = allowFreeAnswers;
		this.freeAnswersVotable = freeAnswersVotable;
		this.isTemplate = isTemplate;
	}

	public String getExternalid() {
		return externalid;
	}

	public void setExternalid(String externalid) {
		this.externalid = externalid;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public long getGameInstanceId() {
		return gameInstanceId;
	}

	public void setGameInstanceId(long gameInstanceId) {
		this.gameInstanceId = gameInstanceId;
	}

	public long getContextId() {
		return contextId;
	}

	public void setContextId(long contextId) {
		this.contextId = contextId;
	}

	public int getMinchoices() {
		return minchoices;
	}

	public void setMinchoices(int minchoices) {
		this.minchoices = minchoices;
	}

	public int getMaxchoices() {
		return maxchoices;
	}

	public void setMaxchoices(int maxchoices) {
		this.maxchoices = maxchoices;
	}

	public int getMaxdigits() {
		return maxdigits;
	}

	public void setMaxdigits(int maxdigits) {
		this.maxdigits = maxdigits;
	}

	public int getMaxlines() {
		return maxlines;
	}

	public void setMaxlines(int maxlines) {
		this.maxlines = maxlines;
	}

	public int getMaxbytes() {
		return maxbytes;
	}

	public void setMaxbytes(int maxbytes) {
		this.maxbytes = maxbytes;
	}

	public int getVisibility() {
		return visibility;
	}

	public void setVisibility(int visibility) {
		this.visibility = visibility;
	}

	public boolean isAllowFreeAnswers() {
		return allowFreeAnswers;
	}

	public void setAllowFreeAnswers(boolean allowFreeAnswers) {
		this.allowFreeAnswers = allowFreeAnswers;
	}

	public boolean isFreeAnswersVotable() {
		return freeAnswersVotable;
	}

	public void setFreeAnswersVotable(boolean freeAnswersVotable) {
		this.freeAnswersVotable = freeAnswersVotable;
	}

	public boolean isTemplate() {
		return isTemplate;
	}

	public void setTemplate(boolean isTemplate) {
		this.isTemplate = isTemplate;
	}

	
	@Override
	public String toJSONString() {
		JSONObject json = getJSON();
		return JSONUtils.JSONToString(json);
	}

	public JSONObject getJSON() {
		JSONObject json = new JSONObject();
		try {
			json.put("externalid", externalid);
			json.put("question", question);
			json.put("type", type);
			json.put("gameInstanceId", gameInstanceId);
			json.put("contextId", contextId);
			json.put("minchoices", minchoices);
			json.put("maxchoices", maxchoices);
			json.put("maxdigits", maxdigits);
			json.put("maxlines", maxlines);
			json.put("maxbytes", maxbytes);
			json.put("visibility", visibility);
			json.put("allowFreeAnswers", allowFreeAnswers);
			json.put("freeAnswersVotable", freeAnswersVotable);
			json.put("isTemplate", isTemplate);
		} catch (JSONException e) {
			LoggerFactory.getLogger().Error(e);
		}
		return json;
	}
}
