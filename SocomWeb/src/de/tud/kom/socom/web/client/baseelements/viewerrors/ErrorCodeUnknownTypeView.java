package de.tud.kom.socom.web.client.baseelements.viewerrors;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

/** All ErrorCode*  are for internal errors. means it displays to user that an internal error has happended.
 * Here an unknown parameter or type appeared.
 * 
 * @author jkonert
 *
 */
public class ErrorCodeUnknownTypeView extends AbstractErrorView {

	@UiTemplate("ErrorCodeUnknownTypeView.ui.xml")
	interface inf extends UiBinder<Widget, ErrorCodeUnknownTypeView> {
	}
	private static inf uiBinder = GWT.create(inf.class);

	@UiField SpanElement type;
	
		
	/***
	 * 
	 * @param type  can contain HTML but is parsed as SafeHTML
	 */
	public ErrorCodeUnknownTypeView(String type) {
		initWidget(uiBinder.createAndBindUi(this));
		if (type != null) this.type.setInnerHTML("( "+SafeHtmlUtils.fromString(type)+")");
	}
	
	
}
