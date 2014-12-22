package de.tud.kom.socom.web.client.baseelements.viewerrors;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

/** displays a message (nor parameter) that the time is over
 * 
 * @author jkonert
 *
 */
public class ErrorSNLoginErrorView extends AbstractErrorView {

	@UiTemplate("ErrorSNLoginErrorView.ui.xml")
	interface inf extends UiBinder<Widget, ErrorSNLoginErrorView> {
	}
	private static inf uiBinder = GWT.create(inf.class);

	@UiField SpanElement sn;
	
		
	public ErrorSNLoginErrorView(String socialNetwork) {
		initWidget(uiBinder.createAndBindUi(this));
		sn.setInnerText(socialNetwork);
	}
	
	
}
