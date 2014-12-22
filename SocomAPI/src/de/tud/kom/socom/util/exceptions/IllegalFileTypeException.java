package de.tud.kom.socom.util.exceptions;

@SuppressWarnings("serial")
public class IllegalFileTypeException extends SocomException {

	private static final int ERROR_CODE = 35;

	public IllegalFileTypeException(String type) {
		super("Illegal Filetype: " + type);
	}

	@Override
	public int getErrorCode() {
		return ERROR_CODE;
	}

}
