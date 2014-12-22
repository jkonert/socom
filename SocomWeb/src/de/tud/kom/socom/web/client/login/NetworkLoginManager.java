package de.tud.kom.socom.web.client.login;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import de.tud.kom.socom.web.client.AppController;
import de.tud.kom.socom.web.client.events.CommunicationFailureEvent;
import de.tud.kom.socom.web.client.events.LoginErrorNetworkUserNotFoundEvent;
import de.tud.kom.socom.web.client.events.LoginNetworkSuccessEvent;
import de.tud.kom.socom.web.client.sharedmodels.LoginResult;
import de.tud.kom.socom.web.client.sharedmodels.SocialMediaApplications;

/** LoginHandling by SocialNetworkToken
 * 
 * @author rhaban, jkonert
 *
 */
public class NetworkLoginManager {
	private HandlerManager eventBus;
	private AppController appController;
	/** this parameter urlParameterForOAuthProcessing should be given back to us on all callbacks from SocialMediaApplications 
	 *  as it identifies the handling by this NetworkManager component */
	private static final String urlParameterValueForOAuthProcessing = "gnetworklogin-cb";
	private static final String urlParameterForOAuthProcessing = "state";
	private static final String urlParameterAndApplicationIdentifierSeperator = ".";
	public static final String TOKEN_URL_PARAMETER = "code";

//	private static final HashMap<SocialMediaApplications, String> loginUrls = new HashMap<SocialMediaApplications, String>();
//	static
//	{
//		// adds state=gnetworklogin-cb,fb,   parameter
//		loginUrls.put(SocialMediaApplications.facebook, StaticFacebookInformation.FACEBOOK_URL+"&"+urlParameterForOAuthProcessing+"="+urlParameterValueForOAuthProcessing+urlParameterAndApplicationIdentifierSeperator+SocialMediaApplications.facebook.getIdentifier());
//		// more to come..
//	}
	
	/** should only be called by LoginManager; get your instance from there */
	protected NetworkLoginManager(AppController appController) {
		this.appController = appController;
		this.eventBus = appController.getEventHandler();
	}


//	public String getOAuthLoginUrl(SocialMediaApplications SMApp)
//	{
//		
//		return loginUrls.get(SMApp);		
//	}
	
	public String getURLParameterValueForOAuthTokenProcessing() {
		return urlParameterValueForOAuthProcessing;
	}
	
	public String getURLParameterForOAuthTokenProcessing(){
		return urlParameterForOAuthProcessing;
	}
	
	/** processed tokens maybe in URL and handles the loginprocess accordingly
	 * 
	 */
	public void receiveToken() {
		String stateParameter = appController.getLocationParameter(urlParameterForOAuthProcessing);
		int i = stateParameter.indexOf(urlParameterAndApplicationIdentifierSeperator);
		int j = stateParameter.indexOf(urlParameterAndApplicationIdentifierSeperator, i+1);
		String id = stateParameter.substring(i+1,j);
		SocialMediaApplications app = SocialMediaApplications.valueOfIndentifier(id);
		switch(app)
		{
		case facebook:
				receiveFBToken();
			break;
		case googleplus:
			break;
		default:
			// Looger warn: Invalid parameter
		}
	}

	private void receiveFBToken() {
		String code = appController.getLocationParameter(TOKEN_URL_PARAMETER);
		appController.getRPCFactory().getSocialNetworkService()
			.getFacebookToken(appController.getRequestInformation().getCurrentGame(), code, new AsyncCallback<LoginResult>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
				eventBus.fireEvent(new CommunicationFailureEvent(caught));
			}

			@Override
			public void onSuccess(final LoginResult result) {
				if(result == null || !result.isSuccess()){
					eventBus.fireEvent(new LoginErrorNetworkUserNotFoundEvent());
					return;
				}
				
				appController.getLoginManager().setSessionCookie(result.getSid());
//				appController.getLoginManager().storeLoginInformation(result, false, false);
//				// add additional information to LoginResult which includes the used network for login
				eventBus.fireEvent(new LoginNetworkSuccessEvent(result.getUid(), SocialMediaApplications.facebook));
			}
		});
	}
}