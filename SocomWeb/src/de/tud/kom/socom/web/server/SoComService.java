package de.tud.kom.socom.web.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import de.tud.kom.socom.web.client.sharedmodels.LoginResult;
import de.tud.kom.socom.web.server.sessions.SessionManager;
import de.tud.kom.socom.web.server.util.Logger;
import de.tud.kom.socom.web.server.util.LoggerFactory;

@SuppressWarnings("serial")
public class SoComService extends RemoteServiceServlet {
	
	Logger logger = LoggerFactory.getLogger();
	
	public LoginResult getCurrentUser(String sid){
		return SessionManager.get().getSession(sid);
	}
}