package de.tud.kom.socom.web.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.SQLException;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;

import de.tud.kom.socom.web.client.services.social.fb.SocialNetworkService;
import de.tud.kom.socom.web.client.sharedmodels.LoginResult;
import de.tud.kom.socom.web.server.database.social.HSQLSocialDatabaseAccess;
import de.tud.kom.socom.web.server.database.social.SocialDatabaseAccess;
import de.tud.kom.socom.web.server.sessions.SessionManager;

@SuppressWarnings("serial")
public class SocialNetworkServiceImpl extends SoComService implements SocialNetworkService {

	private static final String FACEBOOK_OAUTH_TOKEN_URL = "https://graph.facebook.com/oauth/access_token";
	private static final String FACEBOOK_OAUTH_URL = "https://www.facebook.com/dialog/oauth";
	private static final String FACEBOOK_OAUTH_URL_CLIENT_PARAMETER = "client_id";
	private static final String FACEBOOK_OAUTH_URL_REDIRECT_PARAMETER = "redirect_uri";
	private static final String FACEBOOK_OAUTH_URL_SCOPE_PARAMETER_FULL = "scope=user_about_me,publish_stream,read_stream,offline_access";
	private static final String FACEBOOK_OAUTH_URL_STATE_PARAMETER_KEY = "state";
	private static final String FACEBOOK_OAUTH_URL_STATE_PARAMETER_VALUE = "gnetworklogin-cb";
	private static final String FACEBOOK_OAUTH_URL_STATE_PARAMETER_SEPARATOR = ".";
	private static final String FACEBOOK_NAME = "facebook";
	private static final String FACEBOOK_IDENTIFIER = "fb";
	private static final String FACEBOOK_OAUTH_URL_SECRET_PARAMETER = "client_secret";
	private static final String FACEBOOK_OAUTH_URL_CODE_PARAMETER = "code";
	

//	private static final String TOKEN_REQUEST_URL = 
//	"https://graph.facebook.com/oauth/access_token?client_id=" + 
//ResourceLoader.getResource("facebook_app_id") + "&redirect_uri="
//	+ ResourceLoader.getResource("facebook_redirect_url") + 
//	"&client_secret=" + ResourceLoader.getResource("facebook_app_secret") + "&code=";
	
	
	private SocialDatabaseAccess db = HSQLSocialDatabaseAccess.getInstance();

	@Override
	public LoginResult getFacebookToken(String game, String code) {
		try {
			long fbAppId = db.getAppId(FACEBOOK_NAME, game);
			String fbRedirectUrl = db.getGeneralRedirectUrl(FACEBOOK_NAME, game);
			String fbappSecret = db.getAppSecret(FACEBOOK_NAME, game);
			String url_ = FACEBOOK_OAUTH_TOKEN_URL + "?" + 
					FACEBOOK_OAUTH_URL_CLIENT_PARAMETER + "=" + fbAppId + "&" + 
					FACEBOOK_OAUTH_URL_REDIRECT_PARAMETER + "=" + fbRedirectUrl + "&" + 
					FACEBOOK_OAUTH_URL_SECRET_PARAMETER + "=" + fbappSecret + "&" +
					FACEBOOK_OAUTH_URL_CODE_PARAMETER + "=" + code;
			
			URL url = new URL(url_);
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			String answer = reader.readLine();
			int endIndex = answer.indexOf("&");
			if(endIndex == -1) endIndex =  answer.length();
			String token = answer.substring(13, endIndex); // "access_token=".length()
			
			LoginResult id = getUserFromFacebookUid(token);
			return id;
		} catch (IOException e) {
			logger.Error(e);
			return new LoginResult(false, false, e.getMessage(), -1);
		} catch (SQLException e) {
			e.printStackTrace();
			logger.Error(e);
			return new LoginResult(false, false, e.getMessage(), -1);
		}
	}

	private LoginResult getUserFromFacebookUid(String token) {
		LoginResult loginResult = null;
		
		FacebookClient client = new DefaultFacebookClient(token);
		com.restfb.types.User user = client.fetchObject("me", com.restfb.types.User.class);
		String uid = user.getId();
		try {
			loginResult = db.getLoginInformationUsingNetworkId(FACEBOOK_NAME, uid);
		} catch (SQLException e) {
			logger.Error(e);
			return null;
		}
		String sid = SessionManager.get().createSession(loginResult);
		loginResult.setSid(sid);
		
		if(loginResult.isSuccess() && (loginResult.isAdmin() || !loginResult.isDeleted())) {
			logger.Info("Login user #" + loginResult.getUid() + ": " + loginResult.getUsername() + " (Using Facebook ID #" +uid + " in Web-Application)");
		}
		
		return loginResult;
	}

	@Override
	public String getFacebookLoginUrl(String game) {
		try {
		long appid = db.getAppId(FACEBOOK_NAME, game);
		String redirect_url = db.getGeneralRedirectUrl(FACEBOOK_NAME, game);
		String url = FACEBOOK_OAUTH_URL + "?" + 
				FACEBOOK_OAUTH_URL_CLIENT_PARAMETER + "=" + appid + "&" + 
				FACEBOOK_OAUTH_URL_REDIRECT_PARAMETER + "=" + redirect_url + "&" + 
				FACEBOOK_OAUTH_URL_SCOPE_PARAMETER_FULL + "&" + 
				FACEBOOK_OAUTH_URL_STATE_PARAMETER_KEY + "=" + FACEBOOK_OAUTH_URL_STATE_PARAMETER_VALUE	+ 
				FACEBOOK_OAUTH_URL_STATE_PARAMETER_SEPARATOR + FACEBOOK_IDENTIFIER +
				FACEBOOK_OAUTH_URL_STATE_PARAMETER_SEPARATOR + game;
		return url;
		} catch (SQLException e) {
			logger.Error(e);
			return null;
		}
	}
}
