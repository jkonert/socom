package de.tud.kom.socom.util.exceptions;

import de.tud.kom.socom.util.enums.ErrorCode;

public class RewardNotFoundException extends SocomException {

	private static final long serialVersionUID = 1L;
	protected static int ERROR_CODE = ErrorCode.REWARD_NOT_FOUND.ordinal();
	
	public RewardNotFoundException() {
		super();
	}
	
	public RewardNotFoundException(String name) {
		super("Reward=" + name + " not found.");
	}
	
	@Override
	public int getErrorCode() {
		return ERROR_CODE;
	}

}
