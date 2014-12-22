package de.tud.kom.socom.util.exceptions;

import de.tud.kom.socom.util.enums.ErrorCode;

public class GameInstanceNotFoundException extends SocomException {

	private static final long serialVersionUID = 1L;
	protected static int ERROR_CODE = ErrorCode.GAME_VERSION_NOT_FOUND.ordinal();
	
	public GameInstanceNotFoundException(){
		super();
	}
	
	public GameInstanceNotFoundException(String game, String version){
		super("GameVersion=" + game + " " + version + " not found.");
	}

	@Override
	public int getErrorCode() {
		return ERROR_CODE;
	}	
}
