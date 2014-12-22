package de.tud.kom.socom.web.client.services.login;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import de.tud.kom.socom.web.client.sharedmodels.LoginResult;


@RemoteServiceRelativePath("login")
public interface SoComLoginService extends RemoteService {
	
	public LoginResult login(String username, String password);
	LoginResult isLoggedIn(String sid);
	public boolean logout(String sid);
}
