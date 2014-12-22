package de.tud.kom.socom.web.client.eventhandler;

import com.google.gwt.event.shared.EventHandler;

import de.tud.kom.socom.web.client.events.CommunicationFailureEvent;

public interface CommunicationFailureEventHandler extends EventHandler{

	public void onCommunicationFailureEvent(CommunicationFailureEvent event);
	
}
