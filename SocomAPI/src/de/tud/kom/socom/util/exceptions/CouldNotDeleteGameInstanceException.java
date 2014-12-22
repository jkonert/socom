package de.tud.kom.socom.util.exceptions;

import de.tud.kom.socom.util.enums.ErrorCode;

public class CouldNotDeleteGameInstanceException extends SocomException {

	private static final long serialVersionUID = 1L;
	protected static int ERROR_CODE = ErrorCode.COULD_NOT_DELETE_GAME_INSTANCE.ordinal();
	
	public CouldNotDeleteGameInstanceException(){
		super();
	}
	
	public CouldNotDeleteGameInstanceException(long gameInstanceId){
		super("Could not delete gameinstance #" + gameInstanceId + ".");
	}

	@Override
	public int getErrorCode() {
		return ERROR_CODE;
	}	
}
