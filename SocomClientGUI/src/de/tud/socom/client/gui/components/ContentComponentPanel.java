package de.tud.socom.client.gui.components;

import javax.swing.JList;
import javax.swing.JTextField;

import de.tud.socom.client.logic.Cookies;
import de.tud.socom.client.logic.Status;

@SuppressWarnings("serial")
public class ContentComponentPanel extends ComponentPanel {

	private static final String DELETE_COMMENT = "deleteComment";
	private static final String ADD_COMMENT = "addComment";
	private static final String RATE_CONTENT = "rateContent";
	private static final String DOWNLOAD_CONTENT = "downloadContent";
	private static final String GET_CONTENT_INFO = "getContentInfo";
	private static final String GET_CONTENT_INFO_FOR_SCENE = "getContentInfoForContext";
	private static final String UPLOAD_GAME_CONTENT = "uploadContent";
	private static final String ALLOCATE_USER_CONTENT = "createUserContent";
	private static final String ALLOCATE_GAME_CONTENT = "createGameContent";

	/**
	 * Create the panel.
	 */
	public ContentComponentPanel() {
		updatePanel(ALLOCATE_USER_CONTENT);
	}

	@Override
	protected JList getJList() {
		return new JList(new String[] { ALLOCATE_USER_CONTENT, ALLOCATE_GAME_CONTENT, UPLOAD_GAME_CONTENT, GET_CONTENT_INFO_FOR_SCENE, GET_CONTENT_INFO, DOWNLOAD_CONTENT,
				RATE_CONTENT, ADD_COMMENT, DELETE_COMMENT });
	}

	@Override
	protected void updatePanel(Object selectedValue) {
		setAdditionalParameterCount(0);
		String method = (String) selectedValue;
		if (method.equals(ALLOCATE_USER_CONTENT)) {
			int metadatacount = 3;
			setAdditionalParameterCount(metadatacount);
			addStringParameterList("contextid", "title", "description");
			addComboParameter("type", "binary", "audio", "image", "text");
			addComboParameter("visibility", "0", "1", "2", "3", "4");
			addComboParameter("category", "question", "information", "hint", "solution");
			for (int i = 0; i < metadatacount; i++)
				addOptionalParameter();
		} else if (method.equals(ALLOCATE_GAME_CONTENT)) {
			int metadatacount = 4;
			setAdditionalParameterCount(metadatacount);
			addStringParameterList("contextid", "title", "description");
			addComboParameter("type", "binary", "audio", "image", "text");
			addComboParameter("category", "question", "information", "hint", "solution");
			for (int i = 0; i < metadatacount; i++)
				addOptionalParameter();
		} else if (method.equals(UPLOAD_GAME_CONTENT)) {
			activatePost();
			setAdditionalParameterCount(1);
			JTextField field = addStringParameter("contentident", null);
			String contentident = "";
			for (String cook : Cookies.getCookies())
				if (cook.startsWith("contentident"))
					contentident = cook;
			field.setText(contentident);
			addFileChooserParameter("Select Content for actual Cookie");
		} else if (method.equals(GET_CONTENT_INFO_FOR_SCENE)) {
			setAdditionalParameterCount(1);
			addStringParameter("context", null);
			addOptionalStringParameter("since", "Form: 'yyyy-MM-dd HH:mm:ss' or in ms since 01.01.1970 00:00");
		} else if (method.equals(GET_CONTENT_INFO)) {
			setAdditionalParameterCount(6);
			addOptionalStringParameter("contexts", "Comma-Separated");
			addOptionalStringParameter("since", "Form: 'yyyy-MM-dd HH:mm:ss' or in ms since 01.01.1970 00:00");
			addOptionalStringParameter("type", "Comma-Separated");
			addOptionalStringParameter("title", "Comma-Separated");
			addOptionalStringParameter("keywords", "Comma-Separated");
			addOptionalStringParameter("metadata", "Comma-Separated key-value pairs. Example: key1:val1,key2:val2");
		} else if (method.equals(DOWNLOAD_CONTENT)) {
			Status.get().setIsDownload(true);
			addIntegerParameter("contentid");
		} else if (method.equals(RATE_CONTENT)) {
			addIntegerParameter("contentid");
			addStringParameter("rating", null);
		} else if (method.equals(ADD_COMMENT)) {
			addIntegerParameter("contentid");
			addStringParameter("message", null);
		} else if (method.equals(DELETE_COMMENT)) {
			setAdditionalParameterCount(1);
			addIntegerParameter("contentid");
			addOptionalIntegerParameter("delete");
		}
		refresh();
	}

	@Override
	protected String getComponent() {
		return "content";
	}

}
