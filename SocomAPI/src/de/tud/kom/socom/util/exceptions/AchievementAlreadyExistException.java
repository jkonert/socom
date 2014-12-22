package de.tud.kom.socom.util.exceptions;

import de.tud.kom.socom.util.enums.ErrorCode;

public class AchievementAlreadyExistException extends SocomException {

	private static final long serialVersionUID = 1L;
	protected static int ERROR_CODE = ErrorCode.ACHIEVEMENT_ALREADY_EXIST.ordinal();
	
	public AchievementAlreadyExistException() {
		super();
	}
	
	public AchievementAlreadyExistException(String name) {
		super("Achievement=" + name + " already exists.");
	}
	
	@Override
	public int getErrorCode() {
		return ERROR_CODE;
	}

}
