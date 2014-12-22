package de.tud.kom.socom.web.client.sharedmodels;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * 
 * @author rhaban
 * 
 */
public class SocialNetworkUser implements IsSerializable {

	private String userName, snName, urlProfile;

	public SocialNetworkUser() {
	}

	public SocialNetworkUser(String userName, String snName, String urlProfile) {
		this.userName = userName;
		this.snName = snName;
		this.urlProfile = urlProfile;
	}

	/**
	 * @return the users name
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @return the networks name
	 */
	public String getSnName() {
		return snName;
	}

	/**
	 * @return the url pattern for profiles
	 */
	public String getUrlProfile() {
		return urlProfile;
	}
}
