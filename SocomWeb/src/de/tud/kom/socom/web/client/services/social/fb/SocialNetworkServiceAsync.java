package de.tud.kom.socom.web.client.services.social.fb;

import com.google.gwt.user.client.rpc.AsyncCallback;

import de.tud.kom.socom.web.client.sharedmodels.LoginResult;


public interface SocialNetworkServiceAsync {

	void getFacebookToken(String game, String code, AsyncCallback<LoginResult> asyncCallback);

	void getFacebookLoginUrl(String game, AsyncCallback<String> asyncCallback);
}