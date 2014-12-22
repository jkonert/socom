package de.tud.kom.socom.web.client.sharedmodels;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * 
 * @author rhaban
 * 
 */
public class InfluenceAnswer implements IsSerializable {

	private long id;
	private String answer;
	private boolean isPredefined;
	private boolean isNewFreeAnswer;
	private String ownerName;
	private long ownerId;
	
	private int visibility = -1, deletedFlag;
	private AnswerResult results;

	public InfluenceAnswer() {
	}
	
	public InfluenceAnswer(long uid, boolean isNewFreeAnswer, int visibility) {
		this.ownerId = uid;
		this.isNewFreeAnswer = isNewFreeAnswer;
		this.isPredefined = false;
		this.visibility = visibility;
	}

	public InfluenceAnswer(long id, String answer, boolean isPredefined, int deletedFlag) {
		this.id = id;
		this.answer = answer;
		this.isPredefined = isPredefined;
		this.deletedFlag = deletedFlag;
	}
	
	public InfluenceAnswer(long id, String answer, boolean isPredefined, long ownerId, String ownerName, int deletedFlag, int visibility) {
		this.id = id;
		this.answer = answer;
		this.isPredefined = isPredefined;
		this.ownerId = ownerId;
		this.ownerName = ownerName;
		this.deletedFlag = deletedFlag;
		this.visibility = visibility;
	}	
	
	// FIXME: RH  Why is this method an ADD and called from a loop in HSQLInfluenceDatabaseAccess, if it here does not "add" the item but "set"s it?  Change name or behaviour
	public void addAnswer(AnswerResult result) {
		results = result;
	}
	
	public void setOwnerId(long ownerId) {
		this.ownerId = ownerId;
	}
	
	/** may return null
	 * 
	 * @return
	 */
	public AnswerResult getResult(){
		return results;
	}
	
	public long getId() {
		return this.id;
	}

	public String getAnswer() {
		return this.answer;
	}
	
	public boolean isPredefined() {
		return this.isPredefined;
	}

	public String getOwnerName() {
		return this.ownerName;
	}

	public int getDeletedFlag() {
		return this.deletedFlag;
	}
	
	public long getOwnerId() {
		return this.ownerId;
	}
	
	public boolean isNewFreeAnswer(){
		return this.isNewFreeAnswer;
	}
	
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	
	public void setVisibility(int visibility) {
		this.visibility = visibility;
	}
	
	public int getVisibility() {
		return this.visibility;
	}
}
