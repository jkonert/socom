package de.tud.kom.socom.util.exceptions;

import de.tud.kom.socom.util.enums.ErrorCode;

public class AchievementNotFoundException extends SocomException {

	private static final long serialVersionUID = 1L;
	protected static int ERROR_CODE = ErrorCode.ACHIEVEMENT_NOT_FOUND.ordinal();
	
	public AchievementNotFoundException() {
		super();
	}
	
	public AchievementNotFoundException(String name) {
		super("Achievement=" + name + " not found.");
	}
	
	@Override
	public int getErrorCode() {
		return ERROR_CODE;
	}

}
