package de.tud.kom.socom.util.exceptions;

import de.tud.kom.socom.util.enums.ErrorCode;

public class CouldNotDeleteContextException extends SocomException {

	private static final long serialVersionUID = 1L;
	protected static int ERROR_CODE = ErrorCode.COULD_NOT_DELETE_SCENE.ordinal();

	public CouldNotDeleteContextException() {
		super();
	}

	public CouldNotDeleteContextException(long gameinstid, String context) {
		super("Could not delete context=" + context + " from the gameinstance #" + gameinstid + ".");
	}

	public CouldNotDeleteContextException(String message) {
		super(message);
	}

	@Override
	public int getErrorCode() {
		return ERROR_CODE;
	}
}
