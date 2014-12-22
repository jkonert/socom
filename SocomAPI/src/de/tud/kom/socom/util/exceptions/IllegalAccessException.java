package de.tud.kom.socom.util.exceptions;

@SuppressWarnings("serial")
public class IllegalAccessException extends SocomException {

	private static final int ERROR_CODE = 19;
	
	@Override
	public int getErrorCode() {
		return ERROR_CODE;
	}

}
