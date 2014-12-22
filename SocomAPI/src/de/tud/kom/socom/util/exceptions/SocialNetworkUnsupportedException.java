package de.tud.kom.socom.util.exceptions;

import de.tud.kom.socom.util.enums.ErrorCode;

public class SocialNetworkUnsupportedException extends SocomException {

	private static final long serialVersionUID = 1L;
	protected static int ERROR_CODE = ErrorCode.SOCIAL_NETWORK_UNSUPPORTED.ordinal();
	
	public SocialNetworkUnsupportedException(){
		super();
	}
	
	public SocialNetworkUnsupportedException(String name){
		super("SocialNetwork=" + name + " is currently not supported.");
	}

	@Override
	public int getErrorCode() {
		return ERROR_CODE;
	}	
}
