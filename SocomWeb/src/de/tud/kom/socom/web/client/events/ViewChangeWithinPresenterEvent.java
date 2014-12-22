package de.tud.kom.socom.web.client.events;

import com.google.gwt.event.shared.GwtEvent;

import de.tud.kom.socom.web.client.HistoryToken;
import de.tud.kom.socom.web.client.baseelements.Presenter;
import de.tud.kom.socom.web.client.eventhandler.ViewChangeWithinPresenterEventHandler;

/** thrown BEFORE the Presenter currently loaded may change it's display/state due to the changed History event thrown.
 * This event is basically useful for the currently loaded Presenter. 
 * Other components should not subscribe (i.e. un-subscribe) as long as they are not the current Presenter.
 * The current Presenter can use this to listen to changes/clicks of his own Components that cause a HistoryToken change
 * (all Hyperlinks etc.)
 * 
 * @author jkonert
 *
 */
public class ViewChangeWithinPresenterEvent extends GwtEvent<ViewChangeWithinPresenterEventHandler> {
	
	public static final Type<ViewChangeWithinPresenterEventHandler> TYPE = new Type<ViewChangeWithinPresenterEventHandler>();
	
	private Presenter currentPresenter;
	private HistoryToken oldHistoryValue;
	private HistoryToken newHistoryValue;
	
	public ViewChangeWithinPresenterEvent(Presenter currentPresenter, HistoryToken oldHistoryValue, HistoryToken newHistoryValue)
	{
		this.currentPresenter = currentPresenter;
		this.oldHistoryValue = oldHistoryValue;
		this.newHistoryValue = newHistoryValue;
	}
	
	
	public Presenter getCurrentPresenter() {
		return currentPresenter;
	}


	public HistoryToken getOldHistoryValue() {
		return oldHistoryValue;
	}


	public HistoryToken getNewHistoryValue() {
		return newHistoryValue;
	}


	@Override
	public Type<ViewChangeWithinPresenterEventHandler> getAssociatedType() {
		return TYPE;
	}
	
	@Override
	protected void dispatch(ViewChangeWithinPresenterEventHandler handler) {
		handler.onViewChangeWithinPresenterEvent(this);		
	}

	
}
