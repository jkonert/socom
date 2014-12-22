package de.tud.kom.socom.util.exceptions;

public class NotImplementedException extends SocomException {

	private static final long serialVersionUID = 1L;
	protected static int ERROR_CODE = 8;
	
	public NotImplementedException(String function, String container){
		super("Operation -"+function+"- not supported for -"+ container +"-.");
	}
	
	@Override
	public int getErrorCode() {
		return ERROR_CODE;
	}	
}
