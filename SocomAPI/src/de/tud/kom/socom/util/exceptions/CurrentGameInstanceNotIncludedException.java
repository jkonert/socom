package de.tud.kom.socom.util.exceptions;

import de.tud.kom.socom.util.enums.ErrorCode;

public class CurrentGameInstanceNotIncludedException extends SocomException {

	private static final long serialVersionUID = 1L;
	protected static int ERROR_CODE = ErrorCode.CURRENT_GAME_INSTANCE_NOT_IN_SESSION.ordinal();
	
	public CurrentGameInstanceNotIncludedException(){
		super();
	}

	@Override
	public int getErrorCode() {
		return ERROR_CODE;
	}	
}
