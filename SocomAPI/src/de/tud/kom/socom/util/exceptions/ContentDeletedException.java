package de.tud.kom.socom.util.exceptions;

@SuppressWarnings("serial")
public class ContentDeletedException extends SocomException {

	private static final int ERROR_CODE = 33;
	
	@Override
	public int getErrorCode() {
		return ERROR_CODE;
	}
}