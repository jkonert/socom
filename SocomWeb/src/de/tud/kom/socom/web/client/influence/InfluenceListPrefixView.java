package de.tud.kom.socom.web.client.influence;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import de.tud.kom.socom.web.client.baseelements.Presenter;

public class InfluenceListPrefixView extends Composite {

	@UiTemplate("InfluenceListPrefixView.ui.xml")
	interface InfluenceListPrefixViewUiBinder extends UiBinder<Widget, InfluenceListPrefixView> {
	}
	private static InfluenceListPrefixViewUiBinder uiBinder = GWT.create(InfluenceListPrefixViewUiBinder.class);

	@UiField HeadingElement headline;

	public InfluenceListPrefixView(Presenter presenter) {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
}
