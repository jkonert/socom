package de.tud.kom.socom.util.exceptions;

public abstract class SocomException extends Exception{

	private static final long serialVersionUID = 1L;
	
	public abstract int getErrorCode();
	
	public SocomException(String message){
		super(message);
	}
	
	public SocomException(){
		super();
	}
}
