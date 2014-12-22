package de.tud.kom.socom.web.client.util.exceptions;

import com.google.gwt.user.client.rpc.IsSerializable;

public class IllegalTypeException extends Exception implements IsSerializable {
	
	private static final long serialVersionUID = 2145733514710723349L;

	public IllegalTypeException() {
	}
	
	public IllegalTypeException(String reason) {
		super(reason);
	}
}
