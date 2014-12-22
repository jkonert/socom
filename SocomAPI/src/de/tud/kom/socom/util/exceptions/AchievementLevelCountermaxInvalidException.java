package de.tud.kom.socom.util.exceptions;

import de.tud.kom.socom.util.enums.ErrorCode;

public class AchievementLevelCountermaxInvalidException extends SocomException {

	private static final long serialVersionUID = 1L;
	protected static int ERROR_CODE = ErrorCode.ACHIEVEMENT_LEVEL_COUNTERMAX_INVALID.ordinal();
	
	public AchievementLevelCountermaxInvalidException() {
		super();
	}
	
	public AchievementLevelCountermaxInvalidException(String name) {
		super("Achievement level countermax=" + name + " invalid.");
	}
	
	@Override
	public int getErrorCode() {
		return ERROR_CODE;
	}

}
