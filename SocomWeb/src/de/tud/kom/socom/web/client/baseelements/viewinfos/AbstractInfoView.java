package de.tud.kom.socom.web.client.baseelements.viewinfos;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class AbstractInfoView extends Composite implements InfoView
{

	@Override
	public Widget asWidget() {
		return this;
	}

}
