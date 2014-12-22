package de.tud.kom.socom.util.exceptions;

import de.tud.kom.socom.util.enums.ErrorCode;

public class AchievementCategoryNotFoundException extends SocomException {

	private static final long serialVersionUID = 1L;
	protected static int ERROR_CODE = ErrorCode.ACHIEVEMENT_CATEGORY_NOT_FOUND.ordinal();
	
	public AchievementCategoryNotFoundException() {
		super();
	}
	
	public AchievementCategoryNotFoundException(String name) {
		super("Achievement category = " + name + " not found.");
	}
	
	@Override
	public int getErrorCode() {
		return ERROR_CODE;
	}

}
