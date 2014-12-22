package de.tud.socom.client.gui.components;

import javax.swing.JList;

public class AchievementComponentPanel extends ComponentPanel {

	private static final long serialVersionUID = 5281131552556065824L;

	@Override
	protected String getComponent() {
		return "achievements";
	}

	@Override
	protected JList getJList() {
		//TODO fill list
		return new JList();
	}

	@Override
	protected void updatePanel(Object selectedValue) {
		// TODO update on change
		refresh();
	}

}
