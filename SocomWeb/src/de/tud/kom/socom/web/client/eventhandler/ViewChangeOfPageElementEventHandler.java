package de.tud.kom.socom.web.client.eventhandler;

import com.google.gwt.event.shared.EventHandler;

import de.tud.kom.socom.web.client.events.ViewChangeOfPageElementEvent;
import de.tud.kom.socom.web.client.events.ViewChangeWithinPresenterEvent;

public interface ViewChangeOfPageElementEventHandler extends EventHandler {	

	public void onViewChangeOfPageElementHandler(ViewChangeOfPageElementEvent event);
	
	
}
