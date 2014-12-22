package de.tud.kom.socom.web.client.eventhandler;

import com.google.gwt.event.shared.EventHandler;

import de.tud.kom.socom.web.client.events.FormSubmitEvent;

public interface FormSubmitEventHandler extends EventHandler {

	public void onFormSubmitEvent(FormSubmitEvent event);
}
