package de.tud.kom.socom.util.exceptions;

@SuppressWarnings("serial")
public class ContentNotFoundException extends SocomException {
	
	private static final int ERROR_CODE = 44;

	@Override
	public int getErrorCode() {
		return ERROR_CODE;
	}

}
