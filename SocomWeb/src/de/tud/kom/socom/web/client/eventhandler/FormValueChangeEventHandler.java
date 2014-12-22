package de.tud.kom.socom.web.client.eventhandler;

import com.google.gwt.event.shared.EventHandler;

import de.tud.kom.socom.web.client.events.FormValueChangeEvent;

public interface FormValueChangeEventHandler extends EventHandler {

	public void onFormValueChangeEvent(FormValueChangeEvent event);
}
