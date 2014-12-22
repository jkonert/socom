package de.tud.kom.socom.util.exceptions;

@SuppressWarnings("serial")
public class AlreadyRatedException extends SocomException {

	private static final int ERROR_CODE = 20;
	
	@Override
	public int getErrorCode() {
		return ERROR_CODE;
	}

}
