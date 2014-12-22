package de.tud.kom.socom.web.client.reporting;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

import de.tud.kom.socom.web.client.reporting.ItemReportView.SendReportCallback;

public class ReportPopup extends Composite {
	
	private static final int MAX_REPORT_LENGTH = 200; 

	private static ReportPopupUiBinder uiBinder = GWT
			.create(ReportPopupUiBinder.class);
	
	@UiField TextArea reportTextArea;
	@UiField Button buttonClose;
	@UiField Label textLength;
	@UiField Panel textLengthPanel;

	private SendReportCallback callback;

	interface ReportPopupUiBinder extends UiBinder<Widget, ReportPopup> {
	}
	

	public ReportPopup() {
		initWidget(uiBinder.createAndBindUi(this));
		limitArea();
	}

	private void limitArea() {
		reportTextArea.getElement().setAttribute("maxlength", String.valueOf(MAX_REPORT_LENGTH));
		reportTextArea.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				int size = reportTextArea.getText().length();
				textLength.setText(String.valueOf(MAX_REPORT_LENGTH-size));
				
				if(size == MAX_REPORT_LENGTH) {
					Animation a = new Animation() {
						@Override
						protected void onUpdate(double progress) {
							String rgbvalue = "rgb(" + ((int)((1-progress)*255)) + ",0,0)";
							textLengthPanel.getElement().getStyle().setProperty("color", rgbvalue);
						}
					};
					a.run(1000);
				}
			}
		});
	}
	
	@UiHandler("buttonClose")
	public void onClose(ClickEvent e) {
		this.addStyleName("hidden");
	}
	
	@UiHandler("buttonSend")
	public void onSend(ClickEvent e) {
		if(this.callback != null) {
			this.callback.onSendReport(reportTextArea.getText());
			this.addStyleName("hidden");
		}
	}

	public void setReportCallback(SendReportCallback cb) {
		this.callback = cb;
	}
}