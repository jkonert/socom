package de.tud.kom.socom.web.client.baseelements.viewerrors;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

/**
 *
 */
public class ErrorReportFilterRestrictionView extends AbstractErrorView {

	@UiTemplate("ErrorReportFilterRestrictionView.ui.xml")
	interface inf extends UiBinder<Widget, ErrorReportFilterRestrictionView> {
	}
	private static inf uiBinder = GWT.create(inf.class);
	
		
	public ErrorReportFilterRestrictionView() {
		initWidget(uiBinder.createAndBindUi(this));
	}
}
