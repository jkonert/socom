package de.tud.kom.socom.util.exceptions;

@SuppressWarnings("serial")
public class IllegalFileSizeException extends SocomException {

	public static final int ERROR_CODE = 34;

	public IllegalFileSizeException(String maxSize) {
		super("Max Size: " + maxSize);
	}

	@Override
	public int getErrorCode() {
		return ERROR_CODE;
	}

}
