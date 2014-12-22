package de.tud.kom.socom.util.exceptions;

public class UserNotFoundException extends SocomException {

	private static final long serialVersionUID = 1L;
	protected static int ERROR_CODE = 7;

	public UserNotFoundException() {
		super();
	}

	public UserNotFoundException(long id) {
		super("User with ID=" + id + " not found.");
	}

	@Override
	public int getErrorCode() {
		return ERROR_CODE;
	}
}
