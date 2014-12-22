package de.tud.kom.socom.web.client.baseelements.viewerrors;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;

import com.google.gwt.user.client.ui.Widget;

/** Displays a message that between x and y elements have to be selected
 * 
 * @author jkonert
 *
 */
public class ErrorSelectionRestrictionView extends AbstractErrorView {

	@UiTemplate("ErrorSelectionRestrictionView.ui.xml")
	interface inf extends UiBinder<Widget, ErrorSelectionRestrictionView> {
	}
	private static inf uiBinder = GWT.create(inf.class);

	@UiField SpanElement minText;
	@UiField SpanElement maxText;
	@UiField SpanElement currentText;
	
		
	public ErrorSelectionRestrictionView(int min, int max, int current) {
		initWidget(uiBinder.createAndBindUi(this));
		minText.setInnerText(String.valueOf(min));
		maxText.setInnerText(String.valueOf(max));
		currentText.setInnerText(String.valueOf(current));
	}
	
	
}
