package de.tud.socom.client.gui.components;

import javax.swing.JList;

@SuppressWarnings("serial")
public class GameComponentPanel extends ComponentPanel {

	private static final String SET_GAME_CONTEXT_IMAGE = "setGameContextImage";
	private static final String SET_GAME_INSTANCE_IMAGE = "setGameInstanceImage";
	private static final String ADD_GAME = "addGame";
	private static final String REMOVE_GAME = "removeGame";
	private static final String GET_GAME = "getGame";
	private static final String ADD_GAME_INSTANCE = "addGameInstance";
	private static final String REMOVE_GAME_INSTANCE = "removeGameInstance";
	private static final String SET_GAME_INSTANCE_DESCRIPTION = "setGameInstanceDescription";
	private static final String ADD_GAME_SCENE = "addGameContext";
	private static final String REMOVE_GAME_SCENE = "removeGameContext";
	private static final String GET_GAME_SCENES = "getGameContexts";
	private static final String SET_GAME_CONTEXT_DESCRIPTION = "setGameContextDescription";
	private static final String ADD_GAME_SCENE_RELATION = "addGameContextRelation";
	private static final String REMOVE_GAME_SCENE_RELATION = "removeGameContextRelation";
	private static final String GET_GAME_SCENE_RELATIONS = "getGameContextRelations";

	/**
	 * Create the panel.
	 */
	public GameComponentPanel() {
		updatePanel(ADD_GAME);
	}

	protected void updatePanel(Object item) {
		setAdditionalParameterCount(0);
		String methodString = (String) item;
		if (methodString.equals(ADD_GAME)) {
			addStringParameterList("game", "password");
			addStringPredefinedParameterList("genre", "Adventure", "mastersecret", "123a45b6");
		}
		if (methodString.equals(REMOVE_GAME)) {
			addStringParameter("game", null);
			addStringParameter("mastersecret", "123a45b6");
		}
		if (methodString.equals(GET_GAME)) {
			addStringParameterList("game", "password");
		}
		if (methodString.equals(ADD_GAME_INSTANCE)) {
			addStringParameterList("game", "password", "version", "description");
		}
		if (methodString.equals(REMOVE_GAME_INSTANCE)) {
			addStringParameterList("game", "password", "version");
		}
		if(methodString.equals(SET_GAME_INSTANCE_DESCRIPTION)){
			activatePost();
			setAdditionalParameterCount(1);
			addStringParameterList("game","password","version");
			addStringParameter("description", null);
			
		}
		if(methodString.equals(SET_GAME_CONTEXT_DESCRIPTION)) {
			
		}
		if (methodString.equals(GET_GAME_SCENES)) {
			addStringParameterList("game", "password", "version");
		}
		if (methodString.equals(ADD_GAME_SCENE)) {
			addStringParameterList("game", "password", "version", "contextid", "name");
		}
		if (methodString.equals(REMOVE_GAME_SCENE)) {
			addStringParameterList("game", "password", "version", "contextid");
		}
		if (methodString.equals(ADD_GAME_SCENE_RELATION)) {
			addStringParameterList("game", "password", "version", "parent", "child");
		}
		if (methodString.equals(REMOVE_GAME_SCENE_RELATION)) {
			addStringParameterList("game", "password", "version", "parent", "child");
		}
		if (methodString.equals(GET_GAME_SCENE_RELATIONS)) {
			addStringParameterList("game", "password", "version");
		}
		if (methodString.equals(SET_GAME_INSTANCE_IMAGE)) {
			setAdditionalParameterCount(1);
			activatePost();
			addStringParameterList("game", "password", "gameversion", "extension");
			addFileChooserParameter("Select Image File");
		}
		if (methodString.equals(SET_GAME_CONTEXT_IMAGE)) {
			setAdditionalParameterCount(1);
			activatePost();
			addStringParameterList("game", "password", "gameversion", "contextid", "extension");
			addFileChooserParameter("Select Image File");
		}
		refresh();
	}

	@Override
	protected JList getJList() {
		JList list = new JList(new String[] { ADD_GAME, REMOVE_GAME, GET_GAME, ADD_GAME_INSTANCE, REMOVE_GAME_INSTANCE, ADD_GAME_SCENE, REMOVE_GAME_SCENE,
				GET_GAME_SCENES, ADD_GAME_SCENE_RELATION, REMOVE_GAME_SCENE_RELATION, GET_GAME_SCENE_RELATIONS, SET_GAME_INSTANCE_IMAGE, SET_GAME_CONTEXT_IMAGE });
		return list;
	}

	@Override
	protected String getComponent() {
		return "game";
	}

}
