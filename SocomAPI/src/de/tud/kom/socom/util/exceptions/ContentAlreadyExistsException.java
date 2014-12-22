package de.tud.kom.socom.util.exceptions;

@SuppressWarnings("serial")
public class ContentAlreadyExistsException extends SocomException {

	private static final int ERROR_CODE = 43;
	
	@Override
	public int getErrorCode() {
		return ERROR_CODE;
	}

}
