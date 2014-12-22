package de.tud.socom.client.gui.components;

import javax.swing.JList;

import de.tud.socom.client.gui.LoginPanel;

@SuppressWarnings("serial")
public class UserComponentPanel extends ComponentPanel {

	private static final String CREATE_USER_WITH_SOCIAL_NETWORK = "createUserWithSocialNetwork";
	private static final String CHANGE_USER_PASSWORD = "changeUserPassword";
	private static final String CHANGE_USERNAME = "changeUsername";
	private static final String GET_METADATA = "getMetadata";
	private static final String DELETE_METADATA = "deleteMetadata";
	private static final String UPDATE_METADATA = "updateMetadata";
	private static final String CREATE_METADATA = "createMetadata";
	private static final String GET_TIME_PLAYED = "getTimePlayed";
	private static final String SET_TIME_PLAYED = "setTimePlayed";
	private static final String RESET_TIME_PLAYED = "resetTimePlayed";
	private static final String ADD_TIME_PLAYED = "addTimePlayed";
	private static final String GET_LOGS = "getJournalEntries";
	private static final String ADD_LOG = "addJournalEntry";
	private static final String GET_VISITED_SCENES = "getVisitedContexts";
	private static final String SET_CURRENT_SCENES = "setCurrentContext";
	private static final String GET_USERS_GAMES = "getUsersGames";
	private static final String LOGOUT = "logout";
	public static final String VALIDATE_USER = "loginUser";
	private static final String DELETE_USER = "deleteUser";
	private static final String BECOME_ADMIN = "becomeAdmin";
	private static final String CREATE_USER = "createUser";

	private static final String GET_USER = "getUser";
	
	private static final String GOOGLE_PLUS = "Google+";
	private static final String VZ_NET = "VZNet";
	private static final String FACEBOOK = "Facebook";

	/**
	 * Create the panel.itemitem
	 */
	public UserComponentPanel() {
		updatePanel(GET_USER);
	}

	@Override
	protected JList getJList() {
		JList lst = new JList(new String[] { GET_USER, CREATE_USER,
				DELETE_USER, BECOME_ADMIN, VALIDATE_USER, CHANGE_USERNAME,
				CHANGE_USER_PASSWORD, LOGOUT, GET_USERS_GAMES,
				SET_CURRENT_SCENES, GET_VISITED_SCENES, ADD_LOG, GET_LOGS,
				ADD_TIME_PLAYED, RESET_TIME_PLAYED, SET_TIME_PLAYED,
				GET_TIME_PLAYED, CREATE_METADATA, UPDATE_METADATA,
				DELETE_METADATA, GET_METADATA });
		return lst;
	}

	@Override
	protected void updatePanel(Object selectedValue) {
		setAdditionalParameterCount(0);
		String methodString = (String) selectedValue;
		if (methodString.equals(GET_USER)) {

		}
		if (methodString.equals(CREATE_USER)) {
			addStringParameterList("username", "password", "game", "version",
					"gamepassword");
			addIntegerParameter("visibility");
		}
		if (methodString.equals(BECOME_ADMIN)) {
			addStringParameterList("password");
			addStringParameter("mastersecret", "123a45b6");
		}
		if (methodString.equals(DELETE_USER)) {
			addStringParameter("password", null);
		}
		if (methodString.equals(VALIDATE_USER)) {
			addStringParameterList("username", "password", "game", "version",
					"gamepassword");
		}
		if (methodString.equals(CHANGE_USERNAME)) {
			addStringParameterList("password", "username");
		}
		if (methodString.equals(CHANGE_USER_PASSWORD)) {
			addStringParameterList("password", "newpassword");
		}
		if (methodString.equals(LOGOUT)) {
			if (LoginPanel.get().isLoggedIn())
				LoginPanel.get().startLogout();
		}
		if (methodString.equals(GET_USERS_GAMES)) {

		}
		if (methodString.equals(SET_CURRENT_SCENES)) {
			setAdditionalParameterCount(1);
			addStringParameter("context", null);
			addOptionalBooleanParameter("isNewGame");
		}
		if (methodString.equals(GET_VISITED_SCENES)) {

		}
		if (methodString.equals(ADD_LOG)) {
			setAdditionalParameterCount(1);
			addStringParameterList("type", "message");
			addOptionalIntegerParameter("visibility");
		}
		if (methodString.equals(GET_LOGS)) {
			setAdditionalParameterCount(3);
			addOptionalIntegerParameter("limit");
			addOptionalIntegerParameter("offset");
			addOptionalStringParameter("type");
		}
		if (methodString.equals(ADD_TIME_PLAYED)) {
			addIntegerParameter("time");
		}
		if (methodString.equals(RESET_TIME_PLAYED)) {

		}
		if (methodString.equals(SET_TIME_PLAYED)) {
			addIntegerParameter("time");
		}
		if (methodString.equals(CREATE_METADATA)) {
			setAdditionalParameterCount(1);
			addStringParameterList("key", "value");
			addOptionalIntegerParameter("visibility");
		}
		if (methodString.equals(UPDATE_METADATA)) {
			setAdditionalParameterCount(1);
			addStringParameterList("key", "value");
			addOptionalIntegerParameter("visibility");
		}
		if (methodString.equals(DELETE_METADATA)) {
			setAdditionalParameterCount(1);
			addStringParameter("key", null);
			addOptionalIntegerParameter("deleted");
		}
		if (methodString.equals(GET_METADATA)) {
			setAdditionalParameterCount(1);
			addOptionalStringParameter("of");
		}
		if(methodString.equals(CREATE_USER_WITH_SOCIAL_NETWORK)) {
			addStringParameterList("game", "version", "gamepassword");
			addIntegerParameter("visibility");
			addComboParameter("network", FACEBOOK, VZ_NET, GOOGLE_PLUS);
		}
		refresh();
	}

	@Override
	protected String getComponent() {
		return "user";
	}
}