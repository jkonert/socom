package de.tud.kom.socom.web.client.baseelements.viewerrors;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

/** displays a message (nor parameter) that the time is over
 * 
 * @author jkonert
 *
 */
public class ErrorNoUserWasFoundView extends AbstractErrorView {

	@UiTemplate("ErrorNoUserWasFoundView.ui.xml")
	interface inf extends UiBinder<Widget, ErrorNoUserWasFoundView> {
	}
	private static inf uiBinder = GWT.create(inf.class);

	// no fields @UiField HTMLPanel Panel
	
		
	public ErrorNoUserWasFoundView() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	
}
