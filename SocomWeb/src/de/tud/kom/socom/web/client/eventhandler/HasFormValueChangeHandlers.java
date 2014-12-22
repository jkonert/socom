package de.tud.kom.socom.web.client.eventhandler;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public interface HasFormValueChangeHandlers extends HasHandlers
{

		  /**
		   * Adds a {@link FormValueChangeEvent} handler.
		   * 
		   * @param handler the handler
		   * @return the registration for the event
		   */
		  HandlerRegistration addValueChangeHandler(FormValueChangeEventHandler handler);			 		 
}
