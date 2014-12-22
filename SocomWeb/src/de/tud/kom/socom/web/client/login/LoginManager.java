package de.tud.kom.socom.web.client.login;

import java.util.Date;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import de.tud.kom.socom.web.client.AppController;
import de.tud.kom.socom.web.client.eventhandler.LoginEventHandler;
import de.tud.kom.socom.web.client.events.CommunicationFailureEvent;
import de.tud.kom.socom.web.client.events.LoginErrorNetworkUserNotFoundEvent;
import de.tud.kom.socom.web.client.events.LoginErrorWrongUserIDPasswortEvent;
import de.tud.kom.socom.web.client.events.LoginEvent;
import de.tud.kom.socom.web.client.events.LoginSuccessEvent;
import de.tud.kom.socom.web.client.events.LogoutEvent;
import de.tud.kom.socom.web.client.sharedmodels.LoginResult;
import de.tud.kom.socom.web.client.util.RequestInformation;

/**
 * central class handling the cookie management and RequestInformation update
 * 
 * @author jkonert
 * 
 */
public class LoginManager implements LoginEventHandler {

	private RequestInformation requestInformation;
	private HandlerManager eventBus;
	private AppController appController;
	private NetworkLoginManager networkLoginManager; // lazy loading..

	public LoginManager(AppController appController) {
		this.appController = appController;
		this.eventBus = appController.getEventHandler();
		this.eventBus.addHandler(LoginEvent.TYPE, this);
	}
	
	public void login(String username, String pw) {
		appController.getRPCFactory().getLoginService().login(username, pw, new AsyncCallback<LoginResult>() {
	
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Error: " + caught.getMessage());
			}
	
			@Override
			public void onSuccess(LoginResult result) {
				String sid = result.getSid();
				if(sid == null) {
					storeLoginInformation(new LoginResult(false), true, true);
					return;
				}
				setSessionCookie(sid);
				storeLoginInformation(result, true, true);
			}
		});
	}

	public void checkIfLoggedIn(RequestInformation rq) 
	{
		this.requestInformation = rq;
		String sid = getSessionID();
		setSessionCookie(sid);
		appController.getRPCFactory().getLoginService().isLoggedIn(sid, new AsyncCallback<LoginResult>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.toString());
				eventBus.fireEvent(new CommunicationFailureEvent(caught));
			}

			@Override
			public void onSuccess(LoginResult result) {
				storeLoginInformation(result, true, false);
			}
		});
	}

	public void setSessionCookie(String sid) {
		final long DURATION = 1000 * 60 * 60 * 24 * 14; //duration remembering login. 2 weeks
	    Date expires = new Date(System.currentTimeMillis() + DURATION);
		Cookies.setCookie("sid", sid, expires);
	}
	
	/**
	 * @return sessionid or null if it doesnt exist
	 */	
	public String getSessionID()
	{
		// FIXME RH: manage this cookie consistent with login/logout events. means: delete it when logout. re-set it when logged in. (JK)
		// FIXME don't we need a setSessionID() method as well here? encapsulated in LoginManager?
		return Cookies.getCookie("sid");
	} 
	
	public void logout() {
		if (!requestInformation.isLoggedIn())
			return;
		String sid = getSessionID();
		Cookies.removeCookie("sid");
		appController.getRPCFactory().getLoginService().logout(sid,new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				eventBus.fireEvent(new CommunicationFailureEvent(caught));
			}

			@Override
			public void onSuccess(Boolean result) {
				eventBus.fireEvent(new LogoutEvent());
			}
		});
	}
	
	/**
	 * new method to only save user information
	 */
	public void storeLoginInformation(LoginResult result, boolean fireSuccessEvent, boolean fireFailureEvent){
		if(result.isSuccess() && !result.isDeleted()){
			// TODO: Each login sets the user online for a short period of
			// time
			// --> realize by a timestamp in table to user "lastTimeOnline"
			// . Each display checks if currentTime-lastTimeOnline <
			// PERIOD_ONLINE
			// should be automatically done in API on every user-related
			// call
			
			requestInformation.setUserID(result.getUid());
			requestInformation.setUserName(result.getUsername());
			requestInformation.setUserIsAdmin(result.isAdmin());
			if(fireSuccessEvent) eventBus.fireEvent(new LoginSuccessEvent(result.getUid()));
		}
		else
		{
			if(fireFailureEvent) 
			{
				Window.alert("Error logging in. Username or Password incorrect.");
				eventBus.fireEvent(new LoginErrorWrongUserIDPasswortEvent());
			}
		}
	}
	
	public NetworkLoginManager getNetworkLoginManager() {
		if (this.networkLoginManager == null) {
			this.networkLoginManager = new NetworkLoginManager(appController);
		}
		return this.networkLoginManager;
	}

	@Override
	public void onLoginSuccessEvent(LoginEvent event) {
		// caused by myself. ignore.
		// cookie setting etc. could be of course done here, but as we already
		// have some admin information that is not in
		// the event, it is done above on Async result directly.
	}

	@Override
	public void onLogoutEvent(LogoutEvent event) {
		requestInformation.clearUserInformation();
	}

	@Override
	public void onLoginErrorNetworkUserNotFound(LoginErrorNetworkUserNotFoundEvent loginErrorNetworkUserNotFound) {
		// ignore

	}

	@Override
	public void onLoginErrorWrongUserIDPasswortEvent(LoginErrorWrongUserIDPasswortEvent loginErrorWrongUserIDPasswortEvent) {
		// ignore

	}

	@Override
	public void onLoginNetworkSuccessEvent(LoginEvent event) {
		// ignore
	}
}
