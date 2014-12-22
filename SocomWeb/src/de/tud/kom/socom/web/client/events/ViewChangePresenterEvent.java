package de.tud.kom.socom.web.client.events;

import com.google.gwt.event.shared.GwtEvent;

import de.tud.kom.socom.web.client.baseelements.Presenter;
import de.tud.kom.socom.web.client.eventhandler.LoginEventHandler;
import de.tud.kom.socom.web.client.eventhandler.ViewChangePresenterEventHandler;

/** thrown BEFORE the Presenter loaded to RootPanel 'content' is exchanged by a new one
 * 
 * @author jkonert
 *
 */
public class ViewChangePresenterEvent extends GwtEvent<ViewChangePresenterEventHandler> {
	
	public static final Type<ViewChangePresenterEventHandler> TYPE = new Type<ViewChangePresenterEventHandler>();
	private Presenter oldPresenter;
	private Presenter newPresentern;
	
	public ViewChangePresenterEvent(Presenter oldPresenter, Presenter newPresenter)
	{
		this.oldPresenter = oldPresenter;
		this.newPresentern = newPresenter;
	}
	
	public Presenter getOldPresenter() {
		return oldPresenter;
	}

	public Presenter getNewPresentern() {
		return newPresentern;
	}

	@Override
	public Type<ViewChangePresenterEventHandler> getAssociatedType() {
		return TYPE;
	}
	
	@Override
	protected void dispatch(ViewChangePresenterEventHandler handler) {
		handler.onViewChangePresenterEvent(this);		
	}
	
}
