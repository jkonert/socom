package de.tud.kom.socom.web.client.events;

import com.google.gwt.event.shared.GwtEvent;

import de.tud.kom.socom.web.client.eventhandler.LoginEventHandler;

/** parent class for all login related events to have same type **/
public abstract class LoginEvent extends GwtEvent<LoginEventHandler> {
	
	public static final Type<LoginEventHandler> TYPE = new Type<LoginEventHandler>();
	
	@Override
	public Type<LoginEventHandler> getAssociatedType() {
		return TYPE;
	}
	
}
