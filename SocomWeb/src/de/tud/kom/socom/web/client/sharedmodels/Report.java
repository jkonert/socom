package de.tud.kom.socom.web.client.sharedmodels;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Report implements IsSerializable {

	private long id, type, reference, date, informant, reviewedby, reviewdate;
	private String typeName, report, reference2, review, informantName;
	private boolean reviewed;
	
	@SuppressWarnings("unused")
	private Report(){}

	public Report(long id, long type, long reference, long date, long informant, long reviewedby, long reviewdate,
			String typeName, String informantName, String report, String reference2, String review, boolean reviewed) {
		super();
		this.id = id;
		this.type = type;
		this.reference = reference;
		this.date = date;
		this.informant = informant;
		this.informantName = informantName;
		this.reviewedby = reviewedby;
		this.reviewdate = reviewdate;
		this.typeName = typeName;
		this.report = report;
		this.reference2 = reference2;
		this.review = review;
		this.reviewed = reviewed;
	}

	public long getId() {
		return id;
	}

	public long getType() {
		return type;
	}

	public long getReference() {
		return reference;
	}

	public long getDate() {
		return date;
	}

	public long getInformant() {
		return informant;
	}

	public long getReviewedby() {
		return reviewedby;
	}

	public long getReviewedon() {
		return reviewdate;
	}

	public String getTypeName() {
		return typeName;
	}

	public String getReport() {
		return report;
	}

	public String getReference2() {
		return reference2;
	}

	public String getReview() {
		return review;
	}

	public boolean isReviewed() {
		return reviewed;
	}

	public String getInformantName() {
		return informantName;
	}
}