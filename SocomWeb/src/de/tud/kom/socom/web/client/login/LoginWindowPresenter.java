package de.tud.kom.socom.web.client.login;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

import de.tud.kom.socom.web.client.AppController;
import de.tud.kom.socom.web.client.baseelements.AbstractPresenter;
import de.tud.kom.socom.web.client.baseelements.ViewWithErrorsInterface;
import de.tud.kom.socom.web.client.baseelements.viewerrors.ErrorServerUnknownErrorView;
import de.tud.kom.socom.web.client.eventhandler.LoginEventHandler;
import de.tud.kom.socom.web.client.events.LoginErrorNetworkUserNotFoundEvent;
import de.tud.kom.socom.web.client.events.LoginErrorWrongUserIDPasswortEvent;
import de.tud.kom.socom.web.client.events.LoginEvent;
import de.tud.kom.socom.web.client.events.LogoutEvent;

public class LoginWindowPresenter extends AbstractPresenter implements LoginEventHandler
{ // this could implement a LoginWindowInterface that is used by View to
	// callback to presenter

	private LoginWindowViewInterface view;

	/** Interface for views for this Presenter to implemented **/
	public interface LoginWindowViewInterface extends ViewWithErrorsInterface
	{
		void setLoginFocus();

		void setVisibility(boolean visibility);
		
		void setSNLoginButtonEnabled(boolean enable);

		String getUserNameText();

		String getPasswordText();

		void clear();
	}

	private static LoginWindowPresenter instance;

	private LoginWindowPresenter(AppController appController)
	{
		super(appController);
		appController.getEventHandler().addHandler(LoginEvent.TYPE, this);
		init();
	}

	public static LoginWindowPresenter getInstance(AppController appController)
	{
		if (instance == null)
			instance = new LoginWindowPresenter(appController);
		return instance;
	}

	@Override
	public void init()
	{
		this.view = new LoginWindowView(this);
		setView(view);
	}

	@Override
	public void go(RootPanel targetPanel)
	{
		setTargetPanel(targetPanel);
//		Widget viewWidget = view.asWidget();
//		if (!viewWidget.isAttached())
//		{
//			targetPanel.add(viewWidget);			
//		}
		view.setVisibility(true);
		view.setLoginFocus();

		String currentGame = getAppController().getRequestInformation().getCurrentGame();
		String defaultGame = AppController.GAME_PART_DEFAULT;
		this.view.setSNLoginButtonEnabled(!currentGame.equals(defaultGame));
	}

	@Override
	public void onLoginSuccessEvent(LoginEvent event) {
		view.setVisibility(false);
		view.clear();
		if (view.asWidget().isAttached())
			view.asWidget().removeFromParent();

	}

	@Override
	public void onLogoutEvent(LogoutEvent event) {
		view.setVisibility(false);
		view.clear();
		if (view.asWidget().isAttached())
			view.asWidget().removeFromParent();

	}

	@Override
	public void onLoginErrorNetworkUserNotFound(
		LoginErrorNetworkUserNotFoundEvent loginErrorNetworkUserNotFound) {
		// TODO RH implement (JK)
		// TODO JK add errormessages and methods to view (JK)

	}

	@Override
	public void onLoginErrorWrongUserIDPasswortEvent(
		LoginErrorWrongUserIDPasswortEvent loginErrorWrongUserIDPasswortEvent) {
		// TODO RH implement (JK) 
		// TODO JK add errormessages and methods to view (JK)

	}

	public void onLoginButtonClicked() {
		final String username = view.getUserNameText();
		final String pw = view.getPasswordText();

		if (username.length() == 0 || pw.length() <= 3)
		{
			Window.alert("Kein Benutzername oder zu kurzes Passwort.");
			return;
		}
		getAppController().getLoginManager().login(username, pw);
	}

	public void onPasswordForgottenClicked() {
		Window.alert("Leider noch nicht verfügbar.");  // better completely make the link invisible...
	}

	public void onFacebookButtonClicked() {
		view.setVisibility(false);
		
		getAppController().getRPCFactory().getSocialNetworkService()
			.getFacebookLoginUrl(getAppController().getRequestInformation().getCurrentGame(), new AsyncCallback<String>() {
				
				@Override
				public void onSuccess(String url) {
					LoginOAuthWindowPresenter.getInstance(getAppController()).setOAuthLoginURL(url);
					LoginOAuthWindowPresenter.getInstance(getAppController()).go(getTargetPanel());		
					LoginOAuthWindowPresenter.getInstance(getAppController()).show();
				}
				
				@Override
				public void onFailure(Throwable caught) {
					view.showError(new ErrorServerUnknownErrorView());
				}
			});
		
//		LoginOAuthWindowPresenter.getInstance(getAppController())
//			.setOAuthLoginURL(getAppController().getLoginManager().getNetworkLoginManager()
//				.getOAuthLoginUrl(SocialMediaApplications.facebook));
//		LoginOAuthWindowPresenter.getInstance(getAppController()).go(getTargetPanel());		
//		LoginOAuthWindowPresenter.getInstance(getAppController()).show();
	}

	public void onCloseButtonClicked() {
		view.setVisibility(false);
		
	}

	@Override
	public void onLoginNetworkSuccessEvent(LoginEvent event) {
		// ignore
		
	}
}
