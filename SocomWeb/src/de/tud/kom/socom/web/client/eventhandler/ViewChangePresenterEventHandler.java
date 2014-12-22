package de.tud.kom.socom.web.client.eventhandler;

import com.google.gwt.event.shared.EventHandler;

import de.tud.kom.socom.web.client.events.ViewChangePresenterEvent;

public interface ViewChangePresenterEventHandler extends EventHandler {	

	public void onViewChangePresenterEvent(ViewChangePresenterEvent event);
	
}
