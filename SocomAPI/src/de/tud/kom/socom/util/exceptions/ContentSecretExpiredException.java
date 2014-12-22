package de.tud.kom.socom.util.exceptions;

@SuppressWarnings("serial")
public class ContentSecretExpiredException extends SocomException {

	private static final int ERROR_CODE = 15;
	
	public ContentSecretExpiredException(){
		super("Secret expired, only valid for 15 Minutes");
	}
	
	@Override
	public int getErrorCode() {
		return ERROR_CODE;
	}

}
