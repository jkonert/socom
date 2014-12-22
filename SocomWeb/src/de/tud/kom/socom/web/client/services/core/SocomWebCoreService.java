package de.tud.kom.socom.web.client.services.core;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import de.tud.kom.socom.web.client.sharedmodels.User;

@RemoteServiceRelativePath("core")
public interface SocomWebCoreService extends RemoteService {

	User getUser(long userId, long userId2);

	String getUserName(long uid);

	boolean setProfileVisibility(long userId, int selectedIndex);

	String getSHA(String input);
 
	boolean isFriendOf(long userId, long friendId);

}
