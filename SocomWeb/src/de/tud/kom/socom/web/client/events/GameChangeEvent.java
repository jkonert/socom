package de.tud.kom.socom.web.client.events;

import com.google.gwt.event.shared.GwtEvent;

import de.tud.kom.socom.web.client.eventhandler.GameChangeEventHandler;

/** class for game change events **/
public class GameChangeEvent extends GwtEvent<GameChangeEventHandler> {
	
	public static final Type<GameChangeEventHandler> TYPE = new Type<GameChangeEventHandler>();
	private String to;
	
	public GameChangeEvent(String to){
		this.to = to;
	}
	  
	@Override
	public Type<GameChangeEventHandler> getAssociatedType() {
		return TYPE;
	}
	
	@Override
	public void dispatch(GameChangeEventHandler handler) {
		handler.gameChanged(to);
	}
	 
}
