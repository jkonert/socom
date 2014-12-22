package de.tud.kom.socom.web.client.events;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.Widget;

import de.tud.kom.socom.web.client.eventhandler.FormSubmitEventHandler;
import de.tud.kom.socom.web.client.sharedmodels.SimpleEntry;

/** can be thrown by View-Elements when an upload of a file via a form element started
 * 
 * @author jkonert
 *
 */
public final class FormSubmitEvent extends GwtEvent<FormSubmitEventHandler> {
	
	public static final Type<FormSubmitEventHandler> TYPE = new Type<FormSubmitEventHandler>();

	private Widget sourceWidget;
	private SimpleEntry<String, String>[] params;

	
	public FormSubmitEvent(Widget sourceWidget)
	{
		this.sourceWidget = sourceWidget;
		this.params = null;
	}
	
	public FormSubmitEvent(Widget sourceWidget, SimpleEntry<String, String>... sourceParams)
	{
		this.sourceWidget = sourceWidget;
		this.params = sourceParams;
	}

	public final Widget getSourceWidget() {
		return sourceWidget;
	}
	
	/** provides all key=value pairs set by event source to provide additional information with event
	 * 
	 * @return
	 */
	public SimpleEntry<String, String>[] getSourceParameter() {
		return params;
	}
	


	@Override
	public Type<FormSubmitEventHandler> getAssociatedType() {
		return TYPE;
	}
	
	@Override
	protected void dispatch(FormSubmitEventHandler handler) {
		handler.onFormSubmitEvent(this);
	}
	
}
