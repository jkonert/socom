package de.tud.kom.socom.web.client.login;

import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

import de.tud.kom.socom.web.client.AppController;
import de.tud.kom.socom.web.client.HistoryManager;
import de.tud.kom.socom.web.client.baseelements.AbstractMainPresenter;
import de.tud.kom.socom.web.client.baseelements.ViewInterface;
import de.tud.kom.socom.web.client.baseelements.ViewWithErrorsInterface;
import de.tud.kom.socom.web.client.baseelements.viewerrors.ErrorSocialMediaLoginFailedUserNotFoundView;
import de.tud.kom.socom.web.client.eventhandler.LoginEventHandler;
import de.tud.kom.socom.web.client.events.CommunicationFailureEvent;
import de.tud.kom.socom.web.client.events.LoginErrorNetworkUserNotFoundEvent;
import de.tud.kom.socom.web.client.events.LoginErrorWrongUserIDPasswortEvent;
import de.tud.kom.socom.web.client.events.LoginEvent;
import de.tud.kom.socom.web.client.events.LoginNetworkSuccessEvent;
import de.tud.kom.socom.web.client.events.LogoutEvent;
import de.tud.kom.socom.web.client.sharedmodels.LoginResult;
import de.tud.kom.socom.web.client.sharedmodels.SocialMediaApplications;

public class LoginOAuthWindowPresenter extends AbstractMainPresenter implements LoginEventHandler {
	// this could implement a LoginWindowInterface that is used by View to
	// callback to presenter

	private LoginOAuthWindowViewInterface preLoginView;
	private PostLoginOAuthWindowViewInterface postLoginView;
	private SafeUri uri;

	private Timer secondsTimer;
	private int remainingSeconds;

	private boolean isPostLogin;

	/** Interface for views for this Presenter to implemented **/
	public interface LoginOAuthWindowViewInterface extends ViewInterface {
		void setVisibility(boolean visibility);

		void clear();

		void setOAuthLoginURL(SafeUri uri);
	}

	public interface PostLoginOAuthWindowViewInterface extends ViewWithErrorsInterface {
		void setNetworkName(SocialMediaApplications app);

		void showSuccess();

		void showFailure();

		void clear();

		void setSecconds(int sec);

		void close();
	}

	private static LoginOAuthWindowPresenter instance;

	private LoginOAuthWindowPresenter(AppController appController) {
		super(appController);
		appController.getEventHandler().addHandler(LoginEvent.TYPE, this);
		init();
	}

	public static LoginOAuthWindowPresenter getInstance(AppController appController) {
		if (instance == null)
			instance = new LoginOAuthWindowPresenter(appController);
		return instance;
	}

	@Override
	public void init() {
		try {
			String stateParameter = Location.getParameter(getAppController().getLoginManager().getNetworkLoginManager()
					.getURLParameterForOAuthTokenProcessing());
			isPostLogin = stateParameter != null
					&& stateParameter.startsWith(getAppController().getLoginManager().getNetworkLoginManager().getURLParameterValueForOAuthTokenProcessing());
			if (isPostLogin)
				this.postLoginView = new PostLoginOAuthWindowView(this);
			else
				this.preLoginView = new LoginOAuthWindowView(this);
		} catch (Throwable e) {
		}
	}

	@Override
	public void go(RootPanel targetPanel) {
		setTargetPanel(targetPanel);
		// an idea... setView(isPostLogin?postLoginView:preLoginView);
		Widget viewWidget = isPostLogin ? postLoginView.asWidget() : preLoginView.asWidget();
		if (!viewWidget.isAttached()) {
			targetPanel.add(viewWidget);
		} else {
			if (isPostLogin)
				postLoginView.clear();
			else
				preLoginView.clear(); // prevent old data to be displayed
		}
		if (isPostLogin) {
			getAppController().getLoginManager().getNetworkLoginManager().receiveToken();
		} else {
			show();
		}
	}

	public void setOAuthLoginURL(String url) {
		this.uri = UriUtils.fromString(url); // maybe change to
												// .fromTrustedString in case we
												// trust any caller.
		preLoginView.setOAuthLoginURL(this.uri);
	}

	public void hide() {
		preLoginView.setVisibility(false);
		if (preLoginView.asWidget().isAttached())
			preLoginView.asWidget().removeFromParent();
	}

	public void show() {
		if (this.uri != null)
			preLoginView.setVisibility(true);
	}

	/* called by view */
	public void onViewClosed() {
		String sid = getAppController().getLoginManager().getSessionID();
		getAppController().getRPCFactory().getLoginService().isLoggedIn(sid, new AsyncCallback<LoginResult>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.toString());
				getAppController().getEventHandler().fireEvent(new CommunicationFailureEvent(caught));
			}

			@Override
			public void onSuccess(LoginResult result) {
				if(result.isSuccess()) {
					getAppController().getLoginManager().storeLoginInformation(result, false, false);
					getAppController().getEventHandler().fireEvent(new LoginNetworkSuccessEvent(result.getUid(), SocialMediaApplications.facebook));
				} else {
					getAppController().getEventHandler().fireEvent(new LoginErrorNetworkUserNotFoundEvent());
				}
			}
		});
	}

	@Override
	public void onLoginSuccessEvent(LoginEvent event) {
		hide();

	}

	@Override
	public void onLogoutEvent(LogoutEvent event) {
		hide();
	}

	@Override
	public void onLoginErrorNetworkUserNotFound(LoginErrorNetworkUserNotFoundEvent loginErrorNetworkUserNotFound) {
		this.postLoginView.showError(new ErrorSocialMediaLoginFailedUserNotFoundView());

	}

	@Override
	public void onLoginErrorWrongUserIDPasswortEvent(LoginErrorWrongUserIDPasswortEvent loginErrorWrongUserIDPasswortEvent) {
		// ignore...not my stuff..?
	}

	@Override
	public void onLoginNetworkSuccessEvent(LoginEvent event) {
		if (!this.isPostLogin)
			return;
		if (event instanceof LoginNetworkSuccessEvent) {
			postLoginView.setNetworkName(((LoginNetworkSuccessEvent) event).getSocialMediaApplicationUsed());
		}
		postLoginView.showSuccess();
		postLoginView.hideErrors();
		// add a timer counting down the seconds and closing window then
		// automatically
		this.remainingSeconds = 9;
		if (secondsTimer != null) {
			secondsTimer.cancel();
		} else {
			secondsTimer = new Timer() {
				public void run() {
					if (--remainingSeconds == 0) {
						// close
						postLoginView.close();
						this.cancel();
						return;
					}
					postLoginView.setSecconds(remainingSeconds);
				}
			};
		}
		secondsTimer.scheduleRepeating(1000);

	}

	public void onCloseButtonClick() {
		if (this.secondsTimer != null)
			this.secondsTimer.cancel();
		postLoginView.close();
	}

	public void onRetryButtonClick() {
		HistoryManager.back();
	}

}
