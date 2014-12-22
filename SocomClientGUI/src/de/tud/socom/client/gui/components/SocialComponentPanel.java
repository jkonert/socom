package de.tud.socom.client.gui.components;

import javax.swing.JList;

@SuppressWarnings("serial")
public class SocialComponentPanel extends ComponentPanel {

	private static final String GOOGLE_PLUS = "Google+";
	private static final String FACEBOOK = "Facebook";
	private static final String GET_PICTURE_THUMBNAIL = "getPictureThumbnail";
	private static final String WHO_IS = "getSocomId";
	private static final String GET_LIKES = "getSupporter";
	private static final String COMMENT = "comment";
	private static final String READ_POST = "readPost";
	private static final String PUBLISH_MEDIA_ON_FEED = "publishMediaOnFeed";
	private static final String PUBLISH_INFLUENCE_ONFEED = "publishInfluenceOnfeed";
	private static final String PUBLISH_ON_FEED = "publishOnFeed";
	private static final String GET_PROFILE_DATA_OF = "getProfileDataOf";
	private static final String GET_PROFILE_DATA = "getProfileData";
	private static final String GET_SUPPORTED_OPS = "getSupportedOps";
	private static final String GET_SUPPORTED_NETWORKS = "getSupportedNetworks";
	private static final String GET_NETWORK_FRIENDS = "getNetworkFriends";
	private static final String IS_LOGGED_IN = "isLoggedIn";
	private static final String LOGOUT = "logout";
	private static final String RELOGIN = "relogin";
	private static final String LOGIN_URL = "loginURL";
	private static final String CONNECT_PAGE = "connectGamePage";
	private static final String CONNECT_APP = "connectGameApp";

	/**
	 * Create the panel.
	 */
	public SocialComponentPanel() {
		updatePanel(LOGIN_URL);
	}

	@Override
	protected JList getJList() {
		return new JList(new String[] { GET_SUPPORTED_NETWORKS, GET_SUPPORTED_OPS, LOGIN_URL, LOGOUT, IS_LOGGED_IN, GET_NETWORK_FRIENDS, GET_PROFILE_DATA,
				GET_PROFILE_DATA_OF, PUBLISH_ON_FEED, PUBLISH_INFLUENCE_ONFEED, PUBLISH_MEDIA_ON_FEED, READ_POST, COMMENT, GET_LIKES, WHO_IS,
				GET_PICTURE_THUMBNAIL, RELOGIN, CONNECT_PAGE, CONNECT_APP });
	}

	@Override
	protected void updatePanel(Object item) {
		setAdditionalParameterCount(0);
		String methodString = (String) item;
		if (methodString.equals(GET_SUPPORTED_NETWORKS)) {

		}
		if (methodString.equals(GET_SUPPORTED_OPS)) {
			addNetworkParameter();
		}
		if (methodString.equals(LOGIN_URL)) {
			addNetworkParameter();
		}
		if (methodString.equals(LOGOUT)) {
			addNetworkParameter();
		}
		if (methodString.equals(IS_LOGGED_IN)) {
			addNetworkParameter();
		}
		if (methodString.equals(GET_NETWORK_FRIENDS)) {
		}
		if (methodString.equals(GET_PROFILE_DATA)) {
		}
		if (methodString.equals(GET_PROFILE_DATA_OF)) {
			addNetworkParameter();
			addStringParameter("usersnid", null);
		}
		if (methodString.equals(PUBLISH_ON_FEED)) {
			setAdditionalParameterCount(1);
			addBooleanParameter("publishonpage");
			addStringParameter("message", null);
			addOptionalStringParameter("circleid");
		}
		if (methodString.equals(PUBLISH_INFLUENCE_ONFEED)) {
			addBooleanParameter("publishonpage");
			addStringParameterList("message", "influence");
		}
		if (methodString.equals(PUBLISH_MEDIA_ON_FEED)) {
			activatePost();
			setAdditionalParameterCount(1);
			addBooleanParameter("publishonpage");
			addStringParameter("message", null);
			addComboParameter("type", "photos", "videos");
			addFileChooserParameter("Select File");
		}
		if (methodString.equals(READ_POST)) {
			addNetworkParameter();
			addStringParameter("post", null);
		}
		if (methodString.equals(COMMENT)) {
			addNetworkParameter();
			addStringParameterList("post", "message");
		}
		if (methodString.equals(GET_LIKES)) {
			addNetworkParameter();
			addStringParameter("post", null);
		}
		if (methodString.equals(WHO_IS)) {
			addNetworkParameter();
			addStringParameter("snuid", null);
		}
		if (methodString.equals(GET_PICTURE_THUMBNAIL)) {
			addNetworkParameter();
			addStringParameter("usersnid", null);
		}
		if (methodString.equals(RELOGIN)) {
		}
		if(methodString.equals(CONNECT_PAGE)){
			addNetworkParameter();
			addStringParameterList("gamepassword", "pageidentifier");
		}
		if(methodString.equals(CONNECT_APP)){
			addNetworkParameter();
			addStringParameterList("game", "password", "app_id", "app_secret");
		}
		refresh();
	}

	private void addNetworkParameter() {
		addComboParameter("network", FACEBOOK, GOOGLE_PLUS);
	}

	@Override
	protected String getComponent() {
		return "social";
	}
}
