package de.tud.kom.socom.web.client.sharedmodels;

import com.google.gwt.user.client.rpc.IsSerializable;

public class IDSecretPair implements IsSerializable {
	private long uid;
	private String secret;

	public IDSecretPair() {
	}

	public IDSecretPair(long uid, String secret) {
		this.uid = uid;
		this.secret = secret;
	}

	public long getId() {
		return uid;
	}

	public String getSecret() {
		return secret;
	}

	public String toString() {
		return "ID: " + uid + "\nSecret: " + secret;
	}
}