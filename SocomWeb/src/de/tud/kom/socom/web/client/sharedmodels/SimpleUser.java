package de.tud.kom.socom.web.client.sharedmodels;

import com.google.gwt.user.client.rpc.IsSerializable;

public class SimpleUser implements IsSerializable {

	private String name;
	private long uid;
	private boolean isAdmin;
	private int deleted;
	
	public SimpleUser() {
	}
	
	public SimpleUser(String name, long uid, boolean isAdmin, int deleted) {
		super();
		this.name = name;
		this.uid = uid;
		this.isAdmin = isAdmin;
		this.deleted = deleted;
	}
	public String getName() {
		return name;
	}
	public long getUid() {
		return uid;
	}
	public boolean isAdmin() {
		return isAdmin;
	}
	public int getDeleted() {
		return deleted;
	}

	public void setDeleted(int newDeletedState) {
		this.deleted = newDeletedState;
	}
}
