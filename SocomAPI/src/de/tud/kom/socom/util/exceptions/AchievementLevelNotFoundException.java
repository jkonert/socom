package de.tud.kom.socom.util.exceptions;

import de.tud.kom.socom.util.enums.ErrorCode;

public class AchievementLevelNotFoundException extends SocomException {

	private static final long serialVersionUID = 1L;
	protected static int ERROR_CODE = ErrorCode.ACHIEVEMENT_LEVEL_NOT_FOUND.ordinal();
	
	public AchievementLevelNotFoundException() {
		super();
	}
	
	public AchievementLevelNotFoundException(String name) {
		super("Achievement level=" + name + " not found.");
	}
	
	@Override
	public int getErrorCode() {
		return ERROR_CODE;
	}

}
