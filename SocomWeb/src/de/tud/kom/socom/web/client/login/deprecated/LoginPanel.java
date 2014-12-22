package de.tud.kom.socom.web.client.login.deprecated;


import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LongBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import de.tud.kom.socom.web.client.SoComWebEntryPoint;
import de.tud.kom.socom.web.client.services.core.SocomWebCoreService;
import de.tud.kom.socom.web.client.services.core.SocomWebCoreServiceAsync;
import de.tud.kom.socom.web.client.sharedinformation.StaticFacebookInformation;

public class LoginPanel extends VerticalPanel {

	private LongBox uidBox;
	private TextBox passwordBox;
	private Button loginButton, loginFBButton, logoutButton;
	private SoComWebEntryPoint entryPoint;

	public LoginPanel(SoComWebEntryPoint entryPoint) {
		super();
		this.entryPoint = entryPoint;
//		if (this.entryPoint.isLoggedIn()) {
//			buildLogoutArea();
//		} else {
//			buildLoginArea();
//		}
	}

	private void buildLogoutArea() {
//		socomService.getUserName(entryPoint.getUserId(), new AsyncCallback<String>() {
//
//			@Override
//			public void onFailure(Throwable caught) {
//				Window.alert("Error building Logout-Panel: " + caught.getMessage());
//			}
//
//			@Override
//			public void onSuccess(String result) {
//				add(new InlineHTML("Logged in as: " + result));
//				add(logoutButton = new Button("Logout"));
//				initLogoutListeners();
//			}
//		});
	}

	private void initLogoutListeners() {
//		logoutButton.addClickHandler(new ClickHandler() {
//
//			@Override
//			public void onClick(ClickEvent event) {
//				socomService.logout(new AsyncCallback<Boolean>() {
//
//					@Override
//					public void onFailure(Throwable caught) {
//						Window.alert("Error logging out: " + caught.getMessage());
//					}
//
//					@Override
//					public void onSuccess(Boolean result) {
//						entryPoint.setUserID(-1);
//						entryPoint.setUserIsAdmin(false);
//						Cookies.removeCookie("uid");
//						Cookies.removeCookie("secret");
//						Window.Location.reload();
//					}
//				});
//			}
//		});
	}

	private void buildLoginArea() {
		HorizontalPanel manualLoginUid = new HorizontalPanel();
		add(manualLoginUid);

		Label lblUserId = new Label("User ID");
		manualLoginUid.add(lblUserId);

		uidBox = new LongBox();
		manualLoginUid.add(uidBox);

		HorizontalPanel manualLoginPassword = new HorizontalPanel();
		add(manualLoginPassword);

		Label lblSecret = new Label("Password");
		manualLoginPassword.add(lblSecret);

		passwordBox = new TextBox();
		manualLoginPassword.add(passwordBox);

		loginButton = new Button("Login");
		add(loginButton);

		loginFBButton = new Button("Login with Facebook");
		add(loginFBButton);

		initLoginListeners();
	}

	private void initLoginListeners() {
		StartLoginEventHandler loginEventHandler = new StartLoginEventHandler();
		loginButton.addClickHandler(loginEventHandler);
		loginButton.addKeyPressHandler(loginEventHandler);
		uidBox.addKeyPressHandler(new KeyPressHandler() {

			@Override
			public void onKeyPress(KeyPressEvent event) {
				int keyCode = event.getCharCode();
				if (keyCode == 0) {
					// Probably Firefox
					keyCode = event.getNativeEvent().getKeyCode();
				}
				if (keyCode == KeyCodes.KEY_ENTER) {
					passwordBox.setFocus(true);
				}
			}
		});
		passwordBox.addKeyPressHandler(loginEventHandler);

		loginFBButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				String leftMargin = String.valueOf(getLeftMargin());
				String topMargin = String.valueOf(getTopMargin());

//				Window.open(StaticFacebookInformation.FACEBOOK_URL, "Facebook Login to Socom", "width=1200,height=600,resizable=yes,scrollbars=no,popup,top=" + topMargin
//						+ ",left=" + leftMargin);
			}

			private native int getTopMargin() /*-{
		return (screen.height - 600) / 2;
	}-*/;

			private native int getLeftMargin() /*-{
		return (screen.width - 1200) / 2;
	}-*/;

		});
	}

	private class StartLoginEventHandler implements ClickHandler, KeyPressHandler {

		@Override
		public void onKeyPress(KeyPressEvent event) {
			int keyCode = event.getCharCode();
			if (keyCode == 0) {
				// Probably Firefox
				keyCode = event.getNativeEvent().getKeyCode();
			}
			if (keyCode == KeyCodes.KEY_ENTER) {
				initLogin();
			}
		}

		@Override
		public void onClick(ClickEvent event) {
			initLogin();
		}

		private void initLogin() {
//			if (uidBox.getText().length() == 0 || passwordBox.getText().length() <= 3) {
//				Window.alert("Invalid input.");
//				return;
//			}
//			final String uid = uidBox.getText();
//			final String password = passwordBox.getText();
//
//			socomService.getSHA(password, new AsyncCallback<String>() {
//
//				@Override
//				public void onFailure(Throwable caught) {
//					Window.alert("Error hashing Password (" + caught.getMessage() + ")");
//				}
//
//				@Override
//				public void onSuccess(final String hashSecret) {
//					socomService.login(Long.parseLong(uid), hashSecret, new AsyncCallback<boolean[]>() {
//
//						@Override
//						public void onFailure(Throwable caught) {
//							Window.alert("Failure during login:\n" + caught.toString() + "\n" + caught.getMessage());
//						}
//
//						@Override
//						public void onSuccess(boolean[] result) {
//
//							if (!result[0]) {
//								Window.alert("Error logging in");
//								return;
//							}
//
//							Cookies.setCookie("uid", uid);
//							Cookies.setCookie("secret", hashSecret);
//							Window.Location.reload();
//						}
//					});
//				}
//			});
		}

	}
}
