package de.tud.kom.socom.web.client.services.social.fb;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import de.tud.kom.socom.web.client.sharedmodels.LoginResult;


@RemoteServiceRelativePath("fb")
public interface SocialNetworkService extends RemoteService {
	LoginResult getFacebookToken(String game, String code);

	String getFacebookLoginUrl(String game);
}
