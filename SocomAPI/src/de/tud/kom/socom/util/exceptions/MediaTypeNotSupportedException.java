package de.tud.kom.socom.util.exceptions;

public class MediaTypeNotSupportedException extends SocomException {

	private static final long serialVersionUID = 1L;
	protected static int ERROR_CODE = 10;

	public MediaTypeNotSupportedException(String type) {
		super("Type " + type + " is not supported in this Network.");
	}

	@Override
	public int getErrorCode() {
		return ERROR_CODE;
	}
}
