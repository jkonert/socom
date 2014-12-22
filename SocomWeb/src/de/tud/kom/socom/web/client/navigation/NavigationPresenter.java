package de.tud.kom.socom.web.client.navigation;

import com.google.gwt.user.client.ui.RootPanel;

import de.tud.kom.socom.web.client.AppController;
import de.tud.kom.socom.web.client.baseelements.Presenter;
import de.tud.kom.socom.web.client.baseelements.ViewInterface;
import de.tud.kom.socom.web.client.eventhandler.GameChangeEventHandler;
import de.tud.kom.socom.web.client.eventhandler.LoginEventHandler;
import de.tud.kom.socom.web.client.events.GameChangeEvent;
import de.tud.kom.socom.web.client.events.LoginErrorNetworkUserNotFoundEvent;
import de.tud.kom.socom.web.client.events.LoginErrorWrongUserIDPasswortEvent;
import de.tud.kom.socom.web.client.events.LoginEvent;
import de.tud.kom.socom.web.client.events.LogoutEvent;
import de.tud.kom.socom.web.client.util.RequestInformation;

/**
 *  Presenter logic for navigation bar of website (#navigation).
 *  Singleton.
 * @author jkonert
 *
 */
// cannot extend AbstractController  as view is not Error aware
public class NavigationPresenter implements Presenter, LoginEventHandler , GameChangeEventHandler
{ // this could implement a NavigationPresenterInterface to be used by views

	
	/** Interface for views for this NavigationPresenter to implemented **/
	public interface NavigationViewInterface extends ViewInterface
	{	
		void setGamesVisibility(boolean visibility);		
		void setProfileVisibility(boolean visibility);		
		void setAdminVisibility(boolean visibility);
	}
	
	
	private static NavigationPresenter instance;
	
	private AppController appController;
	private NavigationViewInterface view;
	
	private NavigationPresenter(AppController appController) 
	{
		this.appController = appController;
		appController.getEventHandler().addHandler(LoginEvent.TYPE, this);
		appController.getEventHandler().addHandler(GameChangeEvent.TYPE, this);
		init();
	}
	
	public static NavigationPresenter getInstance(AppController appController)
	{
		if (instance == null) instance = new NavigationPresenter(appController);		
		return instance;
	}
	
	@Override
	public void init()
	{
		this.view = new NavigationView(this); 				
	}

	@Override
	public void go(RootPanel targetPanel) {
		RequestInformation rq = appController.getRequestInformation();
		if (!rq.isLoggedIn())
		{
			view.setProfileVisibility(false);
		}
		if (!rq.getUserIsAdmin())
		{
			view.setAdminVisibility(false);
		}
		targetPanel.add(view.asWidget());
	}

	@Override
	public void onLoginSuccessEvent(LoginEvent event) {
		view.setProfileVisibility(true);
		if (appController.getRequestInformation().getUserIsAdmin()) view.setAdminVisibility(true);
		
	}

	@Override
	public void onLogoutEvent(LogoutEvent event) {
		view.setAdminVisibility(false);
		view.setProfileVisibility(false);
		
	}

	@Override
	public void onLoginNetworkSuccessEvent(LoginEvent event) {
		view.setProfileVisibility(true);
		if (appController.getRequestInformation().getUserIsAdmin()) view.setAdminVisibility(true);
		
	}

	@Override
	public void onLoginErrorNetworkUserNotFound(
		LoginErrorNetworkUserNotFoundEvent loginErrorNetworkUserNotFound) {
		// ignore
		
	}

	@Override
	public void onLoginErrorWrongUserIDPasswortEvent(
		LoginErrorWrongUserIDPasswortEvent loginErrorWrongUserIDPasswortEvent) {
		// ignore
		
	}

	@Override
	public void gameChanged(String newGame) {
		//show only if default game is active
		boolean show = newGame.equals(AppController.GAME_PART_DEFAULT);
		view.setGamesVisibility(show);
	}
}