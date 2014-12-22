package de.tud.kom.socom.util.exceptions;

import de.tud.kom.socom.util.enums.ErrorCode;

public class ContextNotFoundException extends SocomException {

	private static final long serialVersionUID = 1L;
	protected static int ERROR_CODE = ErrorCode.SCENE_NOT_FOUND.ordinal();
	
	public ContextNotFoundException(){
		super();
	}
	
	public ContextNotFoundException(String context, long gameinstance){
		super("Context=" + context + " (gameinstance #" + gameinstance + ") not found.");
	}

	@Override
	public int getErrorCode() {
		return ERROR_CODE;
	}	
}