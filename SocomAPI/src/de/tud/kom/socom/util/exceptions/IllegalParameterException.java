package de.tud.kom.socom.util.exceptions;


public class IllegalParameterException extends SocomException {

	private static final long serialVersionUID = 1L;
	protected static int ERROR_CODE = 4;

	public IllegalParameterException(){
		super("Parameter is missing or unusable.");
	}
	
	public IllegalParameterException(String parameter){
		super("Parameter "+parameter+" is missing.");
	}
	
	public IllegalParameterException(String parameter, String message) {
		super(message);
	}

	@Override
	public int getErrorCode() {
		return ERROR_CODE;
	}	
}
