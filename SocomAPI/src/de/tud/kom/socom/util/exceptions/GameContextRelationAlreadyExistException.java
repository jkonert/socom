package de.tud.kom.socom.util.exceptions;

import de.tud.kom.socom.util.enums.ErrorCode;


public class GameContextRelationAlreadyExistException extends SocomException {

	private static final long serialVersionUID = 1L;
	protected static int ERROR_CODE = ErrorCode.SCENE_RELATION_ALREADY_EXIST.ordinal();
	
	public GameContextRelationAlreadyExistException(){
		super();
	}
	
	public GameContextRelationAlreadyExistException(long gameInst, String parentId, String childId){
		super("GameRelation=" + parentId + " -> " + parentId + " already exists in gameinstance #" + gameInst + ".");
	}

	@Override
	public int getErrorCode() {
		return ERROR_CODE;
	}
}
