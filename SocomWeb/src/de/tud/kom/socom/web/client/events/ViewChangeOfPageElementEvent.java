package de.tud.kom.socom.web.client.events;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.IsWidget;

import de.tud.kom.socom.web.client.HistoryToken;
import de.tud.kom.socom.web.client.AppController.PageElementIDs;
import de.tud.kom.socom.web.client.baseelements.Presenter;
import de.tud.kom.socom.web.client.eventhandler.LoginEventHandler;
import de.tud.kom.socom.web.client.eventhandler.ViewChangeOfPageElementEventHandler;
import de.tud.kom.socom.web.client.eventhandler.ViewChangePresenterEventHandler;
import de.tud.kom.socom.web.client.eventhandler.ViewChangeWithinPresenterEventHandler;

/** thrown BEFORE the PageElement is exchanged by new Widget or text
 * 
 * @author jkonert
 *
 */
public class ViewChangeOfPageElementEvent extends GwtEvent<ViewChangeOfPageElementEventHandler> {
	
	public static final Type<ViewChangeOfPageElementEventHandler> TYPE = new Type<ViewChangeOfPageElementEventHandler>();
	private PageElementIDs pageElementName;
	private IsWidget newContent;
	
	
	public ViewChangeOfPageElementEvent(PageElementIDs pageElement, IsWidget oldWidget, IsWidget newContentWidget)
	{
		this.pageElementName = pageElement;
		this.newContent = newContentWidget; 
	}
	
	
	
	public PageElementIDs getPageElementName() {
		return pageElementName;
	}



	public IsWidget getNewContent() {
		return newContent;
	}



	@Override
	public Type<ViewChangeOfPageElementEventHandler> getAssociatedType() {
		return TYPE;
	}
	
	@Override
	protected void dispatch(ViewChangeOfPageElementEventHandler handler) {
		handler.onViewChangeOfPageElementHandler(this);		
	}

	
}
