package de.tud.kom.socom.util.exceptions;

import de.tud.kom.socom.util.enums.ErrorCode;

public class GameNotAuthenticatedException extends SocomException {

	private static final long serialVersionUID = 1L;
	protected static int ERROR_CODE = ErrorCode.GAME_NOT_AUTHED.ordinal();
	
	public GameNotAuthenticatedException(){
		super();
	}
	
	public GameNotAuthenticatedException(String game){
		super("Game=" + game + " not authenticated.");
	}

	@Override
	public int getErrorCode() {
		return ERROR_CODE;
	}	
}
