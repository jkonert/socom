package de.tud.kom.socom.web.client.baseelements.viewinfos;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

public class InfoInfluenceAlreadyFinishedView extends AbstractInfoView {

	@UiTemplate("InfoInfluenceAlreadyFinishedView.ui.xml")
	interface inf extends UiBinder<Widget, InfoInfluenceAlreadyFinishedView> {
	}
	private static inf uiBinder = GWT.create(inf.class);

	// no fields @UiField HTMLPanel Panel
	
		
	public InfoInfluenceAlreadyFinishedView() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	
}
