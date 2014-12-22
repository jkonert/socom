package de.tud.kom.socom.web.client.events;

import de.tud.kom.socom.web.client.eventhandler.LoginEventHandler;

/** successful login **/
public class LoginSuccessEvent extends LoginEvent {
	
	private long userId;
	
	public LoginSuccessEvent(long uid)
	{
		this.userId = uid;
	}

	public long getUserId() {
		return userId;
	}
	

	@Override
	protected void dispatch(LoginEventHandler handler) {
		handler.onLoginSuccessEvent(this);
		
	}

}
