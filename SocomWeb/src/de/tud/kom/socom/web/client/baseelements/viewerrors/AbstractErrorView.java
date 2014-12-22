package de.tud.kom.socom.web.client.baseelements.viewerrors;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class AbstractErrorView extends Composite implements ErrorView
{

	@Override
	public Widget asWidget() {
		return this;
	}

}
