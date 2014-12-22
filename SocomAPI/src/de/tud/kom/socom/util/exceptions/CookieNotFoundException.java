package de.tud.kom.socom.util.exceptions;

@SuppressWarnings("serial")
public class CookieNotFoundException extends SocomException {

	private static final int ERROR_CODE = 14;

	public CookieNotFoundException(String cookieKey) {
		super("Cookie " + cookieKey + " not found.");
	}

	@Override
	public int getErrorCode() {
		return ERROR_CODE;
	}

}
