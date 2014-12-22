package de.tud.kom.socom.util.exceptions;

public class UserAlreadyExistsException extends SocomException {

	private static final long serialVersionUID = 1L;
	protected static int ERROR_CODE = 6;

	public UserAlreadyExistsException(String name) {
		super("User " + name + " already exists in Database.");
	}
	
	@Override
	public int getErrorCode() {
		return ERROR_CODE;
	}	
}
