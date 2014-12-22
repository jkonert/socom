package de.tud.kom.socom.util.exceptions;

public class UIDNotIncludedException extends SocomException {

	private static final long serialVersionUID = 1L;
	protected static int ERROR_CODE = 2;

	public UIDNotIncludedException(){
		super("UID not included in Session");
	}

	@Override
	public int getErrorCode() {
		return ERROR_CODE;
	}	
}
