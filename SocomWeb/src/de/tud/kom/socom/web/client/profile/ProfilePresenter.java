package de.tud.kom.socom.web.client.profile;

import com.google.gwt.user.client.ui.RootPanel;

import de.tud.kom.socom.web.client.AppController;
import de.tud.kom.socom.web.client.baseelements.AbstractMainPresenter;
import de.tud.kom.socom.web.client.baseelements.ViewWithErrorsInterface;
import de.tud.kom.socom.web.client.eventhandler.LoginEventHandler;
import de.tud.kom.socom.web.client.events.LoginErrorNetworkUserNotFoundEvent;
import de.tud.kom.socom.web.client.events.LoginErrorWrongUserIDPasswortEvent;
import de.tud.kom.socom.web.client.events.LoginEvent;
import de.tud.kom.socom.web.client.events.LogoutEvent;


public class ProfilePresenter extends AbstractMainPresenter implements LoginEventHandler { 
	
	public interface ProfileViewInterface extends ViewWithErrorsInterface
	{
		
	}
	
	// FIXME this class is only a skeleton..not finished YET  + it's View as well
	
	private static ProfilePresenter instance;
	
	private ProfileViewInterface view;	
	
	private ProfilePresenter(AppController appController) 
	{
		super(appController);
		appController.getEventHandler().addHandler(LoginEvent.TYPE, this);
	}
	
	public static ProfilePresenter getInstance(AppController appController)
	{
		if (instance == null) instance = new ProfilePresenter(appController);		
		return instance;
	}
	
	@Override 
	public void init()
	{
		this.view = new ProfileView(this); 
		setView(view);
	}
	
	@Override
	public void go(RootPanel targetPanel)
	{
		// dirty hack for backwards stuff
		init(); // redoing it with correct userId
		setTargetPanel(targetPanel);		
	}
	
	private void refresh() {
		// only do this if we are visible and attached and try to do it with AbstractPresenter methods
		if (this.view!=null && getTargetPanel() != null && getTargetPanel().getWidgetIndex(this.view.asWidget()) >= 0)
		{
			getTargetPanel().remove(view.asWidget());
		}
		go(getTargetPanel());		
	}

	@Override
	public void onLoginSuccessEvent(LoginEvent event) {
		if(view.asWidget().isAttached())
			refresh();		
	}

	@Override
	public void onLogoutEvent(LogoutEvent event) {
		if(view.asWidget().isAttached())
			refresh();
	}

	@Override
	public void onLoginNetworkSuccessEvent(LoginEvent event) {
		if(view.asWidget().isAttached())
			refresh();
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

}
