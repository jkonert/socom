package de.tud.kom.socom.web.client.influence;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.json.client.JSONException;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

import de.tud.kom.socom.web.client.AppController;
import de.tud.kom.socom.web.client.HistoryManager;
import de.tud.kom.socom.web.client.HistoryToken;
import de.tud.kom.socom.web.client.AppController.PageElementIDs;
import de.tud.kom.socom.web.client.AppController.Presenters;
import de.tud.kom.socom.web.client.administration.itemadministration.ViewWithItemAdministration;
import de.tud.kom.socom.web.client.baseelements.AbstractMainPresenter;
import de.tud.kom.socom.web.client.baseelements.MainPresenter;
import de.tud.kom.socom.web.client.baseelements.Presenter;
import de.tud.kom.socom.web.client.baseelements.ViewInterface;
import de.tud.kom.socom.web.client.baseelements.ViewWithErrorsInterface;
import de.tud.kom.socom.web.client.baseelements.presenters.ListPresenter;
import de.tud.kom.socom.web.client.baseelements.viewerrors.ErrorCodeUnknownTypeView;
import de.tud.kom.socom.web.client.baseelements.viewerrors.ErrorIllegalDurationNumberView;
import de.tud.kom.socom.web.client.baseelements.viewerrors.ErrorLoginNeededView;
import de.tud.kom.socom.web.client.baseelements.viewerrors.ErrorNotYetStartedView;
import de.tud.kom.socom.web.client.baseelements.viewerrors.ErrorSelectionRestrictionView;
import de.tud.kom.socom.web.client.baseelements.viewerrors.ErrorServerUnknownErrorView;
import de.tud.kom.socom.web.client.baseelements.viewerrors.ErrorView;
import de.tud.kom.socom.web.client.baseelements.viewerrors.ErrorViewFactory;
import de.tud.kom.socom.web.client.baseelements.viewerrors.ErrorVisibilityViolationView;
import de.tud.kom.socom.web.client.baseelements.viewinfos.InfoInfluenceAlreadyFinishedView;
import de.tud.kom.socom.web.client.content.ContentPresenter;
import de.tud.kom.socom.web.client.eventhandler.FormSubmitCompleteEventHandler;
import de.tud.kom.socom.web.client.eventhandler.FormSubmitEventHandler;
import de.tud.kom.socom.web.client.eventhandler.FormValueChangeEventHandler;
import de.tud.kom.socom.web.client.eventhandler.HasFormSubmitCompleteHandlers;
import de.tud.kom.socom.web.client.eventhandler.HasFormSubmitHandlers;
import de.tud.kom.socom.web.client.eventhandler.HasFormValueChangeHandlers;
import de.tud.kom.socom.web.client.eventhandler.LoginEventHandler;
import de.tud.kom.socom.web.client.eventhandler.ViewChangeWithinPresenterEventHandler;
import de.tud.kom.socom.web.client.events.FormSubmitCompleteEvent;
import de.tud.kom.socom.web.client.events.FormSubmitEvent;
import de.tud.kom.socom.web.client.events.FormValueChangeEvent;
import de.tud.kom.socom.web.client.events.LoginErrorNetworkUserNotFoundEvent;
import de.tud.kom.socom.web.client.events.LoginErrorWrongUserIDPasswortEvent;
import de.tud.kom.socom.web.client.events.LoginEvent;
import de.tud.kom.socom.web.client.events.LogoutEvent;
import de.tud.kom.socom.web.client.events.ViewChangeWithinPresenterEvent;
import de.tud.kom.socom.web.client.influence.strategies.AudioInfluenceStrategy;
import de.tud.kom.socom.web.client.influence.strategies.ImageInfluenceStrategy;
import de.tud.kom.socom.web.client.influence.strategies.InfluenceStrategy;
import de.tud.kom.socom.web.client.influence.strategies.TextInfluenceStrategy;
import de.tud.kom.socom.web.client.services.influence.SoComInfluenceServiceAsync;
import de.tud.kom.socom.web.client.sharedmodels.Influence;
import de.tud.kom.socom.web.client.sharedmodels.InfluenceAnswer;
import de.tud.kom.socom.web.client.sharedmodels.SimpleEntry;
import de.tud.kom.socom.web.client.util.FileUtils.FileState;
import de.tud.kom.socom.web.client.util.exceptions.NotVisibleException;

/** this presenter shows a list of active and visible influences for current user
 *  If an Id parameter is found it hands-over to InfluencePresenter
 * @author jkonert
 *
 */

public class InfluenceListPresenter extends ListPresenter<Influence> implements MainPresenter, LoginEventHandler, ViewChangeWithinPresenterEventHandler
{

	private static Cell<Influence> influenceListItemCell;
	private static InfluenceListPresenter instance;
	private AsyncDataProvider<Influence> dataProvider;

	InfluenceListPrefixView prefix;
	
	InfluenceListPresenter(AppController appController) 
														
    {				
		super(getInfluenceListItemCell() , appController);
		appController.getEventHandler().addHandler(ViewChangeWithinPresenterEvent.TYPE, this);

	}
	
	@Override public void init()
	{
		prefix = new InfluenceListPrefixView(this);		
		super.init();
		
		if (dataProvider == null)
		{
			// create AsyncDataProvider and use getListView as Display
			dataProvider = new AsyncDataProvider<Influence>()
				{
					private int maxNumber = 0; // known end of data
					private int maxDisplayed=0; // estimate of length
					boolean reachedEnd;
					@Override
					protected void onRangeChanged(HasData<Influence> display) 
					{
						 final Range range = display.getVisibleRange();							
						 AppController ac = InfluenceListPresenter.this.getAppController();
						 ac.getRPCFactory().getInfluenceService().getInfluences(ac.getLoginManager().getSessionID(), true, range.getStart(), range.getStart()+range.getLength(), 
						        new AsyncCallback<List<Influence>>()
						 {
							 @Override 
							 public void onSuccess(List<Influence> results)
							 {
								 if (results.size() <range.getLength())
								 {// reached end
									 maxNumber = range.getStart() + results.size() -1;
									 maxDisplayed = maxNumber;
									 reachedEnd = true;
								 }
								 else if (!reachedEnd)
								 {
									 maxDisplayed = range.getStart()+range.getLength();
								 }
								 updateRowCount(maxDisplayed, reachedEnd);
								 updateRowData(range.getStart(), results);
							 }
							 
							 @Override
							 public void onFailure(Throwable t)
							 {
								 
							 }
						 });
						 
						 // 
						
					}
			
				};
		}
		getListView().setDataProvider(dataProvider); // this can be called each time on init
	}
	
	public static InfluenceListPresenter getInstance(AppController appController)
	{
		if (instance == null) instance = new InfluenceListPresenter(appController);		
		return instance;
	}

	private static Cell<Influence> getInfluenceListItemCell() {
		if (influenceListItemCell == null)
		{
			influenceListItemCell = new InfluenceListItemCell();
		}
		return influenceListItemCell;
	}
		

	@Override public void go(RootPanel targetPanel)
	{
		// normally never to be found as this is a Listcontroller here
		final String influenceId = getAppController().getCurrentHistoryToken().getPresenterModule();
		
		// switch to single-View if parameter found
		if (influenceId != null && !influenceId.isEmpty())			
		{
			InfluencePresenter.getInstance(this.getAppController()).go(targetPanel);
		}
		else
		{
			// show List-View
			if (targetPanel.getWidgetIndex(this.prefix.asWidget()) < 0)
			{// not attached yet
				targetPanel.add(prefix);
			}
			super.go(targetPanel);
			
		}
		
	}

	@Override
	public void onLoginSuccessEvent(LoginEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLogoutEvent(LogoutEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLoginNetworkSuccessEvent(LoginEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLoginErrorNetworkUserNotFound(
		LoginErrorNetworkUserNotFoundEvent loginErrorNetworkUserNotFound) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLoginErrorWrongUserIDPasswortEvent(
		LoginErrorWrongUserIDPasswortEvent loginErrorWrongUserIDPasswortEvent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSelectionChanged(SelectionChangeEvent event,
		SingleSelectionModel<Influence> model) {
		String id = model.getSelectedObject().getExternalId();
		HistoryToken t = getAppController().getCurrentHistoryToken().clone();
		t.setPresenter(Presenters.influence, false);
		t.setPresenterModule(id, true);
		HistoryManager.newItem(t, true); // FIRE!
	}

	@Override
	public void onViewChangeWithinPresenterEvent(
		ViewChangeWithinPresenterEvent event) {		
		if (event.getCurrentPresenter().equals(this))
		{
			go(getTargetPanel());
		}
	}

	
	// TODO add more events and methods to implement here in parent abstract class to listen/react to events in List...
	
	

}