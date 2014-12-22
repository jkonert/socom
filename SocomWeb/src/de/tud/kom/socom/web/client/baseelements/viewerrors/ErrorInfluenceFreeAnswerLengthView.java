package de.tud.kom.socom.web.client.baseelements.viewerrors;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;

import com.google.gwt.user.client.ui.Widget;

/** Displays a message a text ist longer than a given value
 *
 */
public class ErrorInfluenceFreeAnswerLengthView extends AbstractErrorView {

	@UiTemplate("ErrorInfluenceFreeAnswerLengthView.ui.xml")
	interface inf extends UiBinder<Widget, ErrorInfluenceFreeAnswerLengthView> {
	}
	private static inf uiBinder = GWT.create(inf.class);

	@UiField SpanElement maxText;
	@UiField SpanElement currentText;
	
		
	public ErrorInfluenceFreeAnswerLengthView(int max, int current) {
		initWidget(uiBinder.createAndBindUi(this));
		maxText.setInnerText(String.valueOf(max));
		currentText.setInnerText(String.valueOf(current));
	}
}
