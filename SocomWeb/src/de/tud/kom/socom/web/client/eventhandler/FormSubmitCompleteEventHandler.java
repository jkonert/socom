package de.tud.kom.socom.web.client.eventhandler;

import com.google.gwt.event.shared.EventHandler;

import de.tud.kom.socom.web.client.events.FormSubmitCompleteEvent;

public interface FormSubmitCompleteEventHandler extends EventHandler {

	public void onFormSubmitCompleteEvent(FormSubmitCompleteEvent event);
}
