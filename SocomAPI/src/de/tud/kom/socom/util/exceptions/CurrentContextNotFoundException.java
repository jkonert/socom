package de.tud.kom.socom.util.exceptions;

import de.tud.kom.socom.util.enums.ErrorCode;

public class CurrentContextNotFoundException extends SocomException {

	private static final long serialVersionUID = 1L;
	protected static int ERROR_CODE = ErrorCode.CURRENT_SCENE_NOT_FOUND.ordinal();
	
	public CurrentContextNotFoundException(){
		super();
	}
	
	public CurrentContextNotFoundException(long userId){
		super("Current context for user=" + userId + " not found in the current game.");
	}

	@Override
	public int getErrorCode() {
		return ERROR_CODE;
	}	
}
