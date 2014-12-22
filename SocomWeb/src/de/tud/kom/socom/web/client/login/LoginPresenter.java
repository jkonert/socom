package de.tud.kom.socom.web.client.login;

import com.google.gwt.user.client.ui.RootPanel;

import de.tud.kom.socom.web.client.AppController;
import de.tud.kom.socom.web.client.baseelements.AbstractPresenter;
import de.tud.kom.socom.web.client.baseelements.ViewWithErrorsInterface;
import de.tud.kom.socom.web.client.eventhandler.LoginEventHandler;
import de.tud.kom.socom.web.client.events.LoginErrorNetworkUserNotFoundEvent;
import de.tud.kom.socom.web.client.events.LoginErrorWrongUserIDPasswortEvent;
import de.tud.kom.socom.web.client.events.LoginEvent;
import de.tud.kom.socom.web.client.events.LogoutEvent;
import de.tud.kom.socom.web.client.util.RequestInformation;

public class LoginPresenter extends AbstractPresenter implements LoginEventHandler { // this could implement a LoginPresenterInterface that is used by View to callback to presenter

	/** Interface for views for this NavigationPresenter to implemented **/
	public interface LoginViewInterface extends ViewWithErrorsInterface
	{
		public void setLoginVisible();
		/** accepts null, then no name and prefix text is displayed */
		public void setLogoutVisible(String username);
	}
	
	
	private static LoginPresenter instance;	
	private LoginViewInterface view;
	
	private LoginPresenter(AppController appController) 
	{
		super(appController);
		getAppController().getEventHandler().addHandler(LoginEvent.TYPE, this);
		init();
	}
	
	public static LoginPresenter getInstance(AppController appController)
	{
		if (instance == null) instance = new LoginPresenter(appController);		
		return instance;
	}
	
	@Override 
	public void init()
	{
		this.view = new LoginView(this); 		
	}
	
	@Override
	public void go(RootPanel targetPanel)
	{
		setTargetPanel(targetPanel);
		RequestInformation rq = getAppController().getRequestInformation();
		if (rq.isLoggedIn())
		{			
			view.setLogoutVisible(null);
		}
		else view.setLoginVisible();
		targetPanel.add(view.asWidget());		
		
	}

	public void onLoginButtonClicked() {
		// XXX: potential code for Async lazy loading.. see Section ' Code Splitting' in https://developers.google.com/web-toolkit/articles/mvp-architecture-2 (JK)
		LoginWindowPresenter.getInstance(getAppController()).go(getTargetPanel());		
	}
	
	public void onLogoutButtonClicked() {
		getAppController().getLoginManager().logout();		
	}

	@Override
	public void onLoginSuccessEvent(LoginEvent event) {
		view.setLogoutVisible(null);		
		
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
	public void onLogoutEvent(LogoutEvent event) {
		view.setLoginVisible();		
	}

	@Override
	public void onLoginNetworkSuccessEvent(LoginEvent event) {
		view.setLogoutVisible(null);
		
	}
}
