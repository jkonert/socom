package de.tud.kom.socom.web.client.reporting;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class ItemReportView extends Composite {

	public interface SendReportCallback {
		public void onSendReport(String report);
	}
	
	@UiTemplate("ItemReportView.ui.xml")
	interface inf extends UiBinder<Widget, ItemReportView> {
	}
	private static inf uiBinder = GWT.create(inf.class);

	public ItemReportView() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	@UiField Button reportButton;
	@UiField ReportPopup reportDialog;
	
	@UiHandler("reportButton")
	public void onReportButtonClicked(ClickEvent event) {
		reportDialog.removeStyleName("hidden");
	}
	
	public void setSendReportCallback(SendReportCallback cb) {
		reportDialog.setReportCallback(cb);
	}
}