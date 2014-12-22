package de.tud.kom.socom.web.client.services.login;

import com.google.gwt.user.client.rpc.AsyncCallback;

import de.tud.kom.socom.web.client.sharedmodels.LoginResult;


public interface SoComLoginServiceAsync {

	void login(String username, String password, AsyncCallback<LoginResult> callback);

	void isLoggedIn(String sid, AsyncCallback<LoginResult> callback);

	void logout(String sid, AsyncCallback<Boolean> callback);
}