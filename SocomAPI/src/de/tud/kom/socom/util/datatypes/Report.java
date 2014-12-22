package de.tud.kom.socom.util.datatypes;


import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

import de.tud.kom.socom.util.JSONUtils;
import de.tud.kom.socom.util.LoggerFactory;
import de.tud.kom.socom.util.ResourceLoader;

public class Report implements JSONString{

	private String report, type, reference2, review;
	private long reference, id = -1L, reviewedby = -1L, informant;
	private Date timestamp, reviewedon;
	private boolean reviewed = false;
	
	public Report(long id, long informant, String report, String type, String reference2, long reference, Date timestamp) {
		super();
		this.id = id;
		this.informant = informant;
		this.report = report;
		this.type = type;
		this.reference2 = reference2;
		this.reference = reference;
		this.timestamp = timestamp;
	}
	
	public void addReview(long reviewedby, String review, Date reviewedon){
		this.reviewed = true;
		this.reviewedby = reviewedby;
		this.review = review;
		this.reviewedon = reviewedon;
	}
	
	public String getReport() {
		return report;
	}

	public void setReport(String report) {
		this.report = report;
	}

	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getReference2() {
		return reference2;
	}

	public void setReference2(String reference2) {
		this.reference2 = reference2;
	}

	public long getReference() {
		return reference;
	}
	
	public void setReference(long reference) {
		this.reference = reference;
	}

	public Date getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public boolean isReviewed() {
		return reviewed;
	}

	public void setReviewed(boolean reviewed) {
		this.reviewed = reviewed;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getInformant() {
		return informant;
	}

	public void setInformant(long informant) {
		this.informant = informant;
	}

	public long getReviewedby() {
		return reviewedby;
	}

	public void setReviewedby(long reviewedby) {
		this.reviewedby = reviewedby;
	}

	public String getReview() {
		return review;
	}

	public void setReview(String review) {
		this.review = review;
	}

	public Date getReviewedon() {
		return reviewedon;
	}

	public void setReviewedon(Date reviewedon) {
		this.reviewedon = reviewedon;
	}

	@Override
	public String toJSONString() {
		JSONObject json = new JSONObject();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ResourceLoader.getResource("simple_date_format"));
		try {
			if(id != -1L) json.put("id", id);
			json.put("informant", informant);
			json.put("timestamp", simpleDateFormat.format(timestamp));
			json.put("type", type);
			json.put("reference", reference);
			json.put("reference2", reference2);
			json.put("report", report);
			json.put("reviewed", reviewed);
			if(reviewed) {
				json.put("reviewedby", reviewedby);
				json.put("review", review);
				json.put("reviewedon", simpleDateFormat.format(reviewedon));
			}
		} catch (JSONException e) {
			LoggerFactory.getLogger().Error(e);
		}
		return JSONUtils.JSONToString(json);
	}
}
