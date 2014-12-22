package de.tud.kom.socom.web.client.sharedmodels;

import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;





/**
 * 
 * @author rhaban
 * 
 */
public class User implements IsSerializable {

	private long id, currentGameId;
	private boolean isVisible;
	private int visibilty, contentCount, commentCount, ratingsCount, deletedFlag;
	private String name, currentGameName, currentStateName;
	private List<SocialNetworkUser> socialNetworkAccounts;

	public User() {
	}

	public User(long id, String name, boolean isVisible, int visibilty, long currentGameId, String currentGameName, String currentStateName, List<SocialNetworkUser> socialNetworkAccounts, int contentCount, int commentCount, int ratingsCount, int deletedFlag) {
		this.id = id;
		this.name = name;
		this.isVisible = isVisible;
		this.visibilty = visibilty;
		this.currentGameId = currentGameId;
		this.currentGameName = currentGameName;
		this.currentStateName = currentStateName;
		this.socialNetworkAccounts = socialNetworkAccounts;
		this.contentCount = contentCount;
		this.commentCount = commentCount;
		this.ratingsCount = ratingsCount;
		this.deletedFlag = deletedFlag;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @return the Name
	 */
	public String getName() {
		return name;
	}

	public int getDeletedFlag() {
		return deletedFlag;
	}

	/**
	 * @return if the profile is visible
	 */
	public boolean getIsVisible() {
		return isVisible;
	}

	/**
	 * @return the visibility setting
	 */
	public int getVisibility() {
		return visibilty;
	}

	/**
	 * @return the current games id (-1 if offline, -2 if hidden)
	 */
	public long getCurrentGameId() {
		return currentGameId;
	}

	/**
	 * @return the current games name
	 */
	public String getCurrentGameName() {
		return currentGameName;
	}

	/**
	 * @return the current states name
	 */
	public String getCurrentStateName() {
		return currentStateName;
	}

	/**
	 * @return the social network ids
	 */
	public List<SocialNetworkUser> getSocialNetworkAccounts() {
		return socialNetworkAccounts;
	}

	/**
	 * @return the number of gamecontents the user created
	 */
	public int getContentCount() {
		return contentCount;
	}

	/**
	 * @return the number of comments the user posted
	 */
	public int getCommentCount() {
		return commentCount;
	}

	/**
	 * @return the number of ratings the user gave
	 */
	public int getRatingsCount() {
		return ratingsCount;
	}
}
