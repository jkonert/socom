package de.tud.kom.socom.web.client.util;

import com.google.gwt.user.client.Window.Location;

/**
 * the lightweight object created and handed over to components during
 * generation of website
 * 
 * @author jkonert
 * 
 */
public class RequestInformation {

	public static final long defaultUserID = -1;

	private long userId = defaultUserID;
	private String userName = null;
	private String password = null;
	private String currentGame = null;
	private boolean userIsAdmin = false;
	private String currentPath;
	private boolean completePageBuildMode = true;

	public RequestInformation() {
		currentPath = Location.getPath();
	}

	public void setUserID(long userId) {
		this.userId = userId;
	}

	public void setUserName(String username) {
		this.userName = username;
	}

	public void setUserIsAdmin(boolean isAdmin) {
		this.userIsAdmin = isAdmin;

	}

	public void clearUserInformation() {
		this.userId = defaultUserID;
		this.userName = null;
		this.userIsAdmin = false;
	}

	public long getUserId() {
		return userId;
	}

	public String getUserName() {
		return userName;
	}

	/**
	 * discouraged usage; as this should normally only be needed for AJAX etc
	 * calls...
	 * 
	 * @return
	 */
	public String getUserSecret() {
		return password;
	}

	public boolean isLoggedIn() {
		return userId >= 0;
	}

	public boolean getUserIsAdmin() {
		return userIsAdmin;
	}

	public String getCurrentPath() {
		return currentPath;
	}
	
	public String getCurrentGame() {
		return this.currentGame;
	}
	
	public void setCurrentGame(String game){
		this.currentGame = game;
	}

	/**
	 * returns true if the page was freshly requested (or browser reload) and
	 * everything has to be rebuild
	 * 
	 * @return
	 */
	public boolean isCompletePageBuildMode() {
		return completePageBuildMode;
	}

	/**
	 * should only be called by AppController. After calling,
	 * iscompletePageBuildMode() returns false until new reload happens
	 * 
	 */
	public void endCompletePageBuildMode() {
		this.completePageBuildMode = false;
	}	
}
