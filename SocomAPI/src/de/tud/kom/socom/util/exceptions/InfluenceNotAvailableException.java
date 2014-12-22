package de.tud.kom.socom.util.exceptions;

@SuppressWarnings("serial")
public class InfluenceNotAvailableException extends SocomException {
	
	private static final int ERROR_CODE = 18;
	
	@Override
	public int getErrorCode() {
		return ERROR_CODE;
	}

}
