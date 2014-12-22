package de.tud.kom.socom.util.exceptions;

public class MissingTokenException extends SocomException {

	private static final long serialVersionUID = 1L;
	private static final int ERROR_CODE = 13;

	public MissingTokenException(String network) {
		super("Missing Access Token for " + network);
	}

	@Override
	public int getErrorCode() {
		return ERROR_CODE;
	}

}
