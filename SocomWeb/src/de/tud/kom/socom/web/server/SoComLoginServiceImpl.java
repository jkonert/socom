package de.tud.kom.socom.web.server;

import de.tud.kom.socom.web.client.services.login.SoComLoginService;
import de.tud.kom.socom.web.client.sharedmodels.LoginResult;
import de.tud.kom.socom.web.server.database.login.HSQLLoginDatabaseAccess;
import de.tud.kom.socom.web.server.database.login.LoginDatabaseAccess;
import de.tud.kom.socom.web.server.sessions.SessionManager;
import de.tud.kom.socom.web.server.util.Hasher;
import de.tud.kom.socom.web.server.util.Logger;
import de.tud.kom.socom.web.server.util.LoggerFactory;

@SuppressWarnings("serial")
public class SoComLoginServiceImpl extends SoComService implements SoComLoginService {

	public static final String SESSION_USER_ATTRIBUTE = "user";
	private LoginDatabaseAccess logindb = HSQLLoginDatabaseAccess.getInstance();
	private Logger logger = LoggerFactory.getLogger();
	
	@Override
	public LoginResult login(String username, String plainpassword) {
		String sha = Hasher.getSHA(plainpassword); //FIXME receiving plain password?!
		LoginResult userInformation = logindb.validateLogin(username, sha);
		String sid = SessionManager.get().createSession(userInformation);
		userInformation.setSid(sid);
		logger.Info("Login user #" + userInformation.getUid() + ": " + userInformation.getUsername() + " (Using Web-Application)");
		return userInformation;
	}

	@Override
	public LoginResult isLoggedIn(String sid) {
		return getCurrentUser(sid);
	}

	@Override
	public boolean logout(String sid) {
		LoginResult lr = SessionManager.get().getSession(sid);
		SessionManager.get().removeSession(sid);
		logger.Info("Logout user #" + lr.getUid() + ": " + lr.getUsername() + " (Using Web-Application)");
		return true;
	}
}