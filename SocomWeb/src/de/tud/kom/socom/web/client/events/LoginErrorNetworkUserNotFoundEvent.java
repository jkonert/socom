package de.tud.kom.socom.web.client.events;


import de.tud.kom.socom.web.client.eventhandler.LoginEventHandler;

public class LoginErrorNetworkUserNotFoundEvent extends LoginEvent {

	

	@Override
	protected void dispatch(LoginEventHandler handler) {
		handler.onLoginErrorNetworkUserNotFound(this);
		
	}

}
