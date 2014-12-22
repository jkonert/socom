package de.tud.kom.socom.util.exceptions;

@SuppressWarnings("serial")
public class PostNotAvailableException extends SocomException {

	private static final int ERROR_CODE = 21;
	
	@Override
	public int getErrorCode() {
		return ERROR_CODE;
	}

}
