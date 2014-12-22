package de.tud.kom.socom.util.exceptions;

import org.json.JSONObject;

import de.tud.kom.socom.util.enums.ErrorCode;

@SuppressWarnings("serial")
public class SocialNetworkException extends SocomException
{

	public SocialNetworkException() {
		super();
	}
	
	public SocialNetworkException(String msg) {
		super(msg);
	}
	
	public SocialNetworkException(JSONObject json) {
		super(json.toString());
	}
	
	@Override
	public int getErrorCode() {
		return ErrorCode.SN_EXCEPTION.ordinal();
	}

}
