package de.tud.kom.socom.web.client.util.exceptions;

import com.google.gwt.user.client.rpc.IsSerializable;

public class IllegalAccessException extends Exception implements IsSerializable {
	
	private static final long serialVersionUID = -1522362365528418545L;

	public IllegalAccessException() {
	}
	
	public IllegalAccessException(String reason) {
		super(reason);
	}
}
