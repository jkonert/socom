package de.tud.socom.client.gui.components;

import javax.swing.JList;

public class StatisticComponentPanel extends ComponentPanel {

	private static final long serialVersionUID = 8155271282286693239L;

	private static final String GET_SOCOM_STATISTIC = "getSocomStatistic";

	@Override
	protected String getComponent() {
		return "stats";
	}

	@Override
	protected JList getJList() {
		return new JList(new String[] {
			GET_SOCOM_STATISTIC,
		});
	}

	@Override
	protected void updatePanel(Object selectedValue) {
		setAdditionalParameterCount(0);
		String methodString = (String) selectedValue;
		if (methodString.equals(GET_SOCOM_STATISTIC)) {
			
		}
	}

}
