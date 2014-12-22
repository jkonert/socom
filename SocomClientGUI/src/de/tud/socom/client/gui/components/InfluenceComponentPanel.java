package de.tud.socom.client.gui.components;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JSpinner;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class InfluenceComponentPanel extends ComponentPanel {

	private static final String FETCH_RESULT = "fetchResult";
	private static final String STOP_INFLUENCE = "stopInfluence";
	private static final String ADD_PREDEFINED_ANSWER_WITH_DATA = "addPredefinedAnswerWithData";
	private static final String ADD_PREDEFINED_ANSWER = "addPredefinedAnswer";
	private static final String REMOVE_PREDEFINED_ANSWER = "removePredefinedAnswer";
	private static final String START_INFLUENCE = "startInfluence";
	private static final String PREPARE_INFLUENCE = "createInfluence";
	private static final String PREPARE_INFLUENCE_FROM_TEMPLATE = "createInfluence (Template)";
	private static final String PREPARE_INFLUENCE_TEMPLATE = "createInfluenceTemplate";

	/**
	 * Create the panel.
	 */
	public InfluenceComponentPanel() {
		updatePanel(PREPARE_INFLUENCE);
	}

	@Override
	protected JList getJList() {
		return new JList(new String[] { PREPARE_INFLUENCE, PREPARE_INFLUENCE_FROM_TEMPLATE, PREPARE_INFLUENCE_TEMPLATE, START_INFLUENCE, ADD_PREDEFINED_ANSWER, ADD_PREDEFINED_ANSWER_WITH_DATA, REMOVE_PREDEFINED_ANSWER, STOP_INFLUENCE,
				FETCH_RESULT, });
	}

	@Override
	protected void updatePanel(Object item) {
		setAdditionalParameterCount(0);
		String methodString = (String) item;
		if (methodString.equals(PREPARE_INFLUENCE)) {
			setAdditionalParameterCount(5);
			addComboParameter("visibility", "0", "1", "2", "3", "4");
			addStringParameter("question", null);
			final JTextField typefield = addStringParameter("type", null);
			addOptionalIntegerParameter("minchoices");
			addOptionalIntegerParameter("maxchoices");
			addOptionalStringParameter("contextid");
			addBooleanParameterList("allowfreeanswers");
			addOptionalBooleanParameter("freeanswersvotable");

			final JSpinner maxL = addIntegerParameter("maxlines");
			final JSpinner maxD = addIntegerParameter("maxdigits");
			final JCheckBox maxB = addOptionalIntegerParameter("maxbytes");
			maxL.setEnabled(false);
			maxD.setEnabled(false);
			maxB.setEnabled(true);

			typefield.addKeyListener(new KeyListener() {
				@Override
				public void keyTyped(KeyEvent arg0) {
				}

				@Override
				public void keyReleased(KeyEvent arg0) {
					if (typefield.getText().equalsIgnoreCase("text")) {
						maxL.setEnabled(true);
						maxD.setEnabled(true);
						maxB.setEnabled(false);
						maxB.setSelected(false);
					} else {
						maxL.setEnabled(false);
						maxD.setEnabled(false);
						maxB.setEnabled(true);
					}
				}

				@Override
				public void keyPressed(KeyEvent arg0) {
				}
			});
		} else if (methodString.equals(PREPARE_INFLUENCE_FROM_TEMPLATE)) {
			addStringParameter("templateid", null);
		} else if (methodString.equals(PREPARE_INFLUENCE_TEMPLATE) ){
			setAdditionalParameterCount(5);
			addComboParameter("visibility", "0", "1", "2", "3", "4");
			addStringParameter("question", null);
			final JTextField typefield = addStringParameter("type", null);
			addOptionalIntegerParameter("minchoices");
			addOptionalIntegerParameter("maxchoices");
			addOptionalStringParameter("contextid");
			addBooleanParameterList("allowfreeanswers");
			addOptionalBooleanParameter("freeanswersvotable");

			final JSpinner maxL = addIntegerParameter("maxlines");
			final JSpinner maxD = addIntegerParameter("maxdigits");
			final JCheckBox maxB = addOptionalIntegerParameter("maxbytes");
			maxL.setEnabled(false);
			maxD.setEnabled(false);
			maxB.setEnabled(true);

			typefield.addKeyListener(new KeyListener() {
				@Override
				public void keyTyped(KeyEvent arg0) {
				}

				@Override
				public void keyReleased(KeyEvent arg0) {
					if (typefield.getText().equalsIgnoreCase("text")) {
						maxL.setEnabled(true);
						maxD.setEnabled(true);
						maxB.setEnabled(false);
						maxB.setSelected(false);
					} else {
						maxL.setEnabled(false);
						maxD.setEnabled(false);
						maxB.setEnabled(true);
					}
				}

				@Override
				public void keyPressed(KeyEvent arg0) {
				}
			});
		} else if (methodString.equals(START_INFLUENCE)) {
			addStringParameter("id", null);
			addIntegerParameter("time");
		} else if (methodString.equals(ADD_PREDEFINED_ANSWER)) {
			addStringParameter("id", null);
			addStringParameter("answer", null);
		} else if (methodString.equals(ADD_PREDEFINED_ANSWER_WITH_DATA)) {
			activatePost();
			setAdditionalParameterCount(1);
			addStringParameterList("id", "answer", "fileextension");
			addFileChooserParameter("Select Predefined Data");
		} else if (methodString.equals(REMOVE_PREDEFINED_ANSWER)) {
			addStringParameter("influenceid", null);
			addStringParameter("answerid", null);
		} else if (methodString.equals(STOP_INFLUENCE)) {
			addStringParameter("id", null);
		} else if (methodString.equals(FETCH_RESULT)) {
			addStringParameter("id", null);
		}
		refresh();
	}

	@Override
	protected String getComponent() {
		return "influence";
	}

}
