package de.tud.kom.socom.web.client.util.exceptions;


public class NotVisibleException extends Exception {

	private static final long serialVersionUID = 7549471364442976735L;
	private int visibility;
	
	public NotVisibleException(){}
	
	public NotVisibleException(int visibiltiy) {
		this.visibility = visibiltiy;
	}

	public int getVisibility(){
		return visibility;
	}
}
