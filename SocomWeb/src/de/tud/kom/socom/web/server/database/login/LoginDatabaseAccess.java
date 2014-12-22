package de.tud.kom.socom.web.server.database.login;

import de.tud.kom.socom.web.client.sharedmodels.LoginResult;

public interface LoginDatabaseAccess {
	
	public LoginResult validateLogin(String username, String hashpassword);
	public LoginResult validateLogin(long uid, String hashpassword);

}
