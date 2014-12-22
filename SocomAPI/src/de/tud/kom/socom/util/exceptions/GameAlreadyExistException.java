package de.tud.kom.socom.util.exceptions;

import de.tud.kom.socom.util.enums.ErrorCode;


public class GameAlreadyExistException extends SocomException {

	private static final long serialVersionUID = 1L;
	protected static int ERROR_CODE = ErrorCode.GAME_ALREADY_EXIST.ordinal();
	
	public GameAlreadyExistException(){
		super();
	}
	
	public GameAlreadyExistException(String name){
		super("Game=" + name + " already exists.");
	}

	@Override
	public int getErrorCode() {
		return ERROR_CODE;
	}
}
