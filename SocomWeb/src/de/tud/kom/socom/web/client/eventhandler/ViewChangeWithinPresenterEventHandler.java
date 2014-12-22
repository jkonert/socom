package de.tud.kom.socom.web.client.eventhandler;

import com.google.gwt.event.shared.EventHandler;

import de.tud.kom.socom.web.client.events.ViewChangeWithinPresenterEvent;

public interface ViewChangeWithinPresenterEventHandler extends EventHandler {	

	public void onViewChangeWithinPresenterEvent(ViewChangeWithinPresenterEvent event);
	
}
