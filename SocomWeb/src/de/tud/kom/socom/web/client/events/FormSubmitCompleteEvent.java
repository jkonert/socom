package de.tud.kom.socom.web.client.events;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.Widget;

import de.tud.kom.socom.web.client.eventhandler.FormSubmitCompleteEventHandler;
import de.tud.kom.socom.web.client.sharedmodels.SimpleEntry;

/** can be thrown by View-Elements when an upload of a file via a form element finished (error or success)
 * 
 * @author jkonert
 *
 */
public final class FormSubmitCompleteEvent extends GwtEvent<FormSubmitCompleteEventHandler> {
	
	public static final Type<FormSubmitCompleteEventHandler> TYPE = new Type<FormSubmitCompleteEventHandler>();

	private String result;	
	private Widget sourceWidget;
	private SimpleEntry<String, String>[] params;

	
	public FormSubmitCompleteEvent(Widget sourceWidget, String htmlResult)
	{
		this.sourceWidget = sourceWidget;
		this.result = htmlResult;
		this.params = null;
	}
	
	/**
	 * 
	 * @param sourceWidget
	 * @param htmlResult   normally a JSON string result that can be parsed
	 */
	public FormSubmitCompleteEvent(Widget sourceWidget, String htmlResult, SimpleEntry<String, String>... sourceParams)
	{
		this.sourceWidget = sourceWidget;
		this.result = htmlResult;
		this.params = sourceParams;
	}
	
	/** the result string as returned from form recieving server. normally a JSON parsable string.
	 * 
	 * @return
	 */
	public String getResult()
	{
		return result;
	}
	
	public final Widget getSourceWidget() {
		return sourceWidget;
	}


	@Override
	public Type<FormSubmitCompleteEventHandler> getAssociatedType() {
		return TYPE;
	}
	
	@Override
	protected void dispatch(FormSubmitCompleteEventHandler handler) {
		handler.onFormSubmitCompleteEvent(this);
	}

	/** provides all key=value pairs set by event source to provide additional information with event
	 * 
	 * @return
	 */
	public SimpleEntry<String, String>[] getSourceParameter() {
		return params;
	}
	
}
