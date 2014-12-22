package de.tud.kom.socom.web.client.eventhandler;

import com.google.gwt.event.shared.EventHandler;

import de.tud.kom.socom.web.client.events.LoginErrorNetworkUserNotFoundEvent;
import de.tud.kom.socom.web.client.events.LoginErrorWrongUserIDPasswortEvent;
import de.tud.kom.socom.web.client.events.LoginEvent;
import de.tud.kom.socom.web.client.events.LogoutEvent;

public interface LoginEventHandler extends EventHandler {

	public void onLoginSuccessEvent(LoginEvent event);
	
	public void onLogoutEvent(LogoutEvent event);
	
	public void onLoginNetworkSuccessEvent(LoginEvent event);

	public void onLoginErrorNetworkUserNotFound(
			LoginErrorNetworkUserNotFoundEvent loginErrorNetworkUserNotFound);

	public void onLoginErrorWrongUserIDPasswortEvent(
			LoginErrorWrongUserIDPasswortEvent loginErrorWrongUserIDPasswortEvent);
	
}
