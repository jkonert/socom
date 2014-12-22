package de.tud.kom.socom.web.client.events;

import com.google.gwt.event.shared.GwtEvent;

import de.tud.kom.socom.web.client.eventhandler.CommunicationFailureEventHandler;

public class CommunicationFailureEvent extends GwtEvent<CommunicationFailureEventHandler> {

	private Throwable exception;

	public CommunicationFailureEvent(Throwable exception)
	{
		this.exception = exception;
	}
	public static final Type<CommunicationFailureEventHandler> TYPE = new Type<CommunicationFailureEventHandler>();

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<CommunicationFailureEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(CommunicationFailureEventHandler handler) {
		handler.onCommunicationFailureEvent(this);
		
	}

	public Throwable getException() {
		return exception;
	}

}
