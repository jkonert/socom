package de.tud.kom.socom.util.exceptions;

@SuppressWarnings("serial")
public class GameContextAlreadyExistException extends SocomException {

	private static final int ERROR_CODE = 17;
	
	@Override
	public int getErrorCode() {
		return ERROR_CODE;
	}
}
