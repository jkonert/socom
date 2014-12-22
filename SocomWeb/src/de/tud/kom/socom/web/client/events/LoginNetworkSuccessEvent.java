package de.tud.kom.socom.web.client.events;

import de.tud.kom.socom.web.client.eventhandler.LoginEventHandler;
import de.tud.kom.socom.web.client.sharedmodels.SocialMediaApplications;

/** successful login via a network **/
public class LoginNetworkSuccessEvent extends LoginEvent {
	
	private long userId;
	private SocialMediaApplications app;
	
	public LoginNetworkSuccessEvent(long uid, SocialMediaApplications app)
	{
		this.userId = uid;
		this.app = app;
	}

	public long getUserId() {
		return userId;
	}
	
	public SocialMediaApplications getSocialMediaApplicationUsed()
	{
		return app;
	}
	

	@Override
	protected void dispatch(LoginEventHandler handler) {
		handler.onLoginNetworkSuccessEvent(this);
		
	}

}
