package de.tud.kom.socom.web.client.sharedmodels;

import com.google.gwt.user.client.rpc.IsSerializable;

public class LoginResult implements IsSerializable {

	private String sid = null;
	private boolean success, isAdmin = false;
	private String username = null;
	private long uid = -1l;
	private int deleted = 0;
	
	@SuppressWarnings("unused")
	private LoginResult(){}
	
	public LoginResult(boolean success) {
		this.success = success;
	}
	
	public LoginResult(boolean success, boolean isAdmin, String userName, long uid) {
		this.success = success;
		this.isAdmin = isAdmin;
		this.username = userName;
		this.uid = uid;
	}
	
	public LoginResult(boolean success, boolean isAdmin, String userName, long uid, int deleted) {
		this.success = success;
		this.isAdmin = isAdmin;
		this.username = userName;
		this.uid = uid;
		this.deleted = deleted;
	}

	public boolean isSuccess() {
		return success;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public String getUsername() {
		return username;
	}

	public long getUid() {
		return uid;
	}
	
	public int getDeleted() {
		return deleted;
	}
	
	public boolean isDeleted(){
		return deleted > 0;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}
	
	public String getSid() {
		return sid;
	}
	
	public void setUsername(String usern) {
		this.username = usern;
	}
}