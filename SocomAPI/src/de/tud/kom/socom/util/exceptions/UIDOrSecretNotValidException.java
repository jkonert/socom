package de.tud.kom.socom.util.exceptions;

public class UIDOrSecretNotValidException extends SocomException {

	private static final long serialVersionUID = 1L;
	protected static int ERROR_CODE = 5;

	public UIDOrSecretNotValidException() {
		super("User not authenticated.");
	}
	
	@Override
	public int getErrorCode() {
		return ERROR_CODE;
	}	
}
