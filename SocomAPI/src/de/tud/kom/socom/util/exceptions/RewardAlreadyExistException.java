package de.tud.kom.socom.util.exceptions;

import de.tud.kom.socom.util.enums.ErrorCode;

public class RewardAlreadyExistException extends SocomException {
	
	private static final long serialVersionUID = 1L;
	protected static int ERROR_CODE = ErrorCode.REWARD_ALREADY_EXIST.ordinal();
	
	public RewardAlreadyExistException() {
		super();
	}
	
	public RewardAlreadyExistException(String name) {
		super("Reward=" + name + " already exists.");
	}
	
	@Override
	public int getErrorCode() {
		return ERROR_CODE;
	}

}
