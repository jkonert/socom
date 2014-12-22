package de.tud.kom.socom.web.server.sessions;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.tud.kom.socom.web.client.sharedmodels.LoginResult;

public class SessionManager {
	
	private static final SessionManager instance = new SessionManager();
	private static Map<String, LoginResult> sessions;

	private SessionManager() {
		sessions = new HashMap<String, LoginResult>();
	}

	public static SessionManager get(){
		return instance;
	}
	
	public String createSession(LoginResult login) {
		String sid = UUID.randomUUID().toString();
		if(sessions.containsKey(sid)) return createSession(login);
		sessions.put(sid, login);
		return sid;
	}
	
	public LoginResult getSession(String sid) {
		if(sid == null) return new LoginResult(false);
		LoginResult result = sessions.get(sid);
		return result == null ? new LoginResult(false) : result;
	}
	
	public void removeSession(String sid) {
		sessions.remove(sid);
	}
}
