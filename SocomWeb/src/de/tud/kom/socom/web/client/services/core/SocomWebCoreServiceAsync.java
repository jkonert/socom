package de.tud.kom.socom.web.client.services.core;

import com.google.gwt.user.client.rpc.AsyncCallback;

import de.tud.kom.socom.web.client.sharedmodels.User;

public interface SocomWebCoreServiceAsync {

	void getUser(long loggedInUserId, long fetchedUserId, AsyncCallback<User> asyncCallback);

	void getUserName(long uid, AsyncCallback<String> asyncCallback);

	void setProfileVisibility(long userId, int selectedIndex, AsyncCallback<Boolean> asyncCallback);

	void getSHA(String input, AsyncCallback<String> callback);

	void isFriendOf(long userId, long friendId, AsyncCallback<Boolean> callback);

}
