package de.tud.kom.socom.web.client.eventhandler;

import com.google.gwt.event.shared.EventHandler;

public interface GameChangeEventHandler extends EventHandler {

	public void gameChanged(String newGame);
	
}
