package de.tud.kom.socom.web.client.sharedmodels;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

public class AnswerResult implements IsSerializable{
	/**
	 * 
	 */
	private long answerId;
	private int hits;
	private Date timestamp;
	
	public AnswerResult(){}
	
	public AnswerResult(long answerId, int hits, Date timestamp) {
		this.answerId = answerId;
		this.hits = hits;
		this.timestamp = timestamp;
	}

	public long getAnswerId() {
		return answerId;
	}

	public int getVotes() {
		return hits;
	}

	public Date getTimestamp() {
		return timestamp;
	}
}