package de.tud.kom.socom.util.exceptions;

@SuppressWarnings("serial")
public class InfluenceTemplateException extends SocomException {
	
	private static final int ERROR_CODE = 45;
	
	public InfluenceTemplateException(String msg) {
		super(msg);
	}
	
	@Override
	public int getErrorCode() {
		return ERROR_CODE;
	}

}
