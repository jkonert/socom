package de.tud.kom.socom.util.datatypes;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

import de.tud.kom.socom.util.JSONUtils;
import de.tud.kom.socom.util.LoggerFactory;

public class InfluenceResult implements JSONString {

	class InfluenceAnswer implements JSONString {
		long id;
		String answer;
		int count;
		
		public InfluenceAnswer(long id, String answer, int count) {
			this.id = id;
			this.answer = answer;
			this.count = count;
		}

		@Override
		public String toJSONString() {
			try
			{
				return JSONUtils.JSONToString(new JSONObject().put("id", id).put("answer", answer).put("count", count));
			} catch (JSONException e)
			{
				LoggerFactory.getLogger().Error(e);
			}
			return null;
		}
	}
	
	private String question;
	private boolean allowedFreeAnswers, freeAnswersVotable;
	private List<InfluenceAnswer> predefinedAnswers = new LinkedList<InfluenceAnswer>();
	private List<InfluenceAnswer> freeAnswers = new LinkedList<InfluenceAnswer>();

	public InfluenceResult(String question, boolean allowedFreeAnswers, boolean freeAnswersVotable) {
		this.question = question;
		this.allowedFreeAnswers = allowedFreeAnswers;
		this.freeAnswersVotable = freeAnswersVotable;
	}

	public String getQuestion() {
		return question;
	}

	public boolean getAllowedFreeAnswers() {
		return allowedFreeAnswers;
	}

	public List<InfluenceAnswer> getPredefinedAnswers() {
		return predefinedAnswers;
	}

	public void addPredefinedAnswer(long id, String answer, int count) {
		predefinedAnswers.add(new InfluenceAnswer(id, answer, count));
	}

	public List<InfluenceAnswer> getFreeAnswers() {
		return freeAnswers;
	}

	public void addFreeAnswer(long id, String answer) {
		freeAnswers.add(new InfluenceAnswer(id, answer, -1));
	}

	public void addFreeAnswer(long id, String answer, int answerCount) {
		freeAnswers.add(new InfluenceAnswer(id, answer, answerCount));
	}

	@Override
	public String toJSONString() {
		JSONObject json = getJSON();
		return JSONUtils.JSONToString(json);
	}

	public JSONObject getJSON() {
		JSONObject json = new JSONObject();
		try {
			json.put("question", question);
			json.put("allowedFreeAnswers", allowedFreeAnswers);
			json.put("freeAnswersVotable", freeAnswersVotable);
			json.put("predefinedAnswers", predefinedAnswers);
			json.put("freeAnswers", freeAnswers);
		} catch (JSONException e) {
			LoggerFactory.getLogger().Error(e);
		}
		return json;
	}

}
