package de.tud.kom.socom.util.exceptions;

@SuppressWarnings("serial")
public class AlreadyStartedException extends SocomException {

	private static final int ERROR_CODE = 47;
	
	@Override
	public int getErrorCode() {
		return ERROR_CODE;
	}

}
