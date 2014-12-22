package de.tud.kom.socom.util.exceptions;

import de.tud.kom.socom.util.enums.ErrorCode;

public class CouldNotDeleteGameException extends SocomException {

	private static final long serialVersionUID = 1L;
	protected static int ERROR_CODE = ErrorCode.COULD_NOT_DELETE_GAME.ordinal();
	
	public CouldNotDeleteGameException(){
		super();
	}
	
	public CouldNotDeleteGameException(String game){
		super("Could not delete game=" + game + ".");
	}

	@Override
	public int getErrorCode() {
		return ERROR_CODE;
	}	
}
