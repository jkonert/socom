package de.tud.kom.socom.util.exceptions;

@SuppressWarnings("serial")
public class GameVersionAlreadyExistException extends SocomException {

	private static final int ERROR_CODE = 16;
	
	@Override
	public int getErrorCode() {
		return ERROR_CODE;
	}
}
