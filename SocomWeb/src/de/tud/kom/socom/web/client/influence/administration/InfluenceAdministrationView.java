package de.tud.kom.socom.web.client.influence.administration;

import java.text.ParseException;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.LongBox;
import com.google.gwt.user.client.ui.Widget;

import de.tud.kom.socom.web.client.influence.InfluencePresenter;

public class InfluenceAdministrationView extends Composite {

	@UiTemplate("InfluenceAdministrationView.ui.xml")
	interface inf extends UiBinder<Widget, InfluenceAdministrationView> {
	}
	private static inf uiBinder = GWT.create(inf.class);

	public InfluenceAdministrationView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField Button startButton;
	@UiField Button stopButton;
	@UiField Button buttonClose;
	@UiField Button buttonStartNow;
	@UiField LongBox timeBox;
	@UiField DialogBox startDialog;
	private InfluencePresenter presenter;
	
	@UiHandler("startButton")
	public void onStartButtonClicked(ClickEvent e){
		startDialog.removeStyleName("hidden");
	}
	
	@UiHandler("stopButton")
	public void onStopButtonClicked(ClickEvent e){
		presenter.onStopInfluence();
	}
	
	@UiHandler("buttonClose")
	public void onCloseStartButtonClicked(ClickEvent e) {
		startDialog.addStyleName("hidden");
	}
	
	@UiHandler("buttonStartNow")
	public void onStartNowButtonClicked(ClickEvent e) {		
		presenter.onStartInfluence(timeBox.getValue());
		startDialog.addStyleName("hidden");
	}

	public void setPresenter(InfluencePresenter presenter) {
		this.presenter = presenter;
	}
	
	@UiHandler("timeBox")
	public void onTimeValueChanged(KeyUpEvent e) {
		try{
			timeBox.getValueOrThrow();
		} catch(ParseException ex) {
			presenter.adminIllegalTimeValue(true);
			if(buttonStartNow.isEnabled())
				buttonStartNow.setEnabled(false);
			return;
		}
		presenter.adminIllegalTimeValue(false);
		if(!buttonStartNow.isEnabled())
			buttonStartNow.setEnabled(true);
	}
}