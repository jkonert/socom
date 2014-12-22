package de.tud.kom.socom.util.exceptions;

@SuppressWarnings("serial")
public class ContentNotAvailableException extends SocomException {

	private static final int ERROR_CODE = 12;
	
	@Override
	public int getErrorCode() {
		return ERROR_CODE;
	}

}
