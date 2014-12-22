package de.tud.kom.socom.web.client.baseelements.viewinfos;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

public class InfoSNLoginSuccessfullView extends AbstractInfoView {

	@UiTemplate("InfoSNLoginSuccessfullView.ui.xml")
	interface inf extends UiBinder<Widget, InfoSNLoginSuccessfullView> {
	}
	private static inf uiBinder = GWT.create(inf.class);

	 @UiField SpanElement sn;
	
	public InfoSNLoginSuccessfullView(String socialNetwork) {
		initWidget(uiBinder.createAndBindUi(this));
		sn.setInnerText(socialNetwork);
	}
}
