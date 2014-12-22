package de.tud.kom.socom.web.client.administration;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.LongBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import de.tud.kom.socom.web.client.sharedmodels.SimpleUser;

import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Label;

public class UserPropertiesView extends Composite {

	private static UserPropertiesViewUiBinder uiBinder = GWT.create(UserPropertiesViewUiBinder.class);

	interface UserPropertiesViewUiBinder extends UiBinder<Widget, UserPropertiesView> {
	}

	@UiField Button saveButton;
	@UiField Button closeButton;
	@UiField ListBox deletedStates;
	@UiField TextBox nameBox;
	@UiField LongBox uidBox;
	@UiField Label statusText;
	
	private AdministrationPresenter presenter;
	private SimpleUser user;

	public UserPropertiesView(AdministrationPresenter presenter, SimpleUser user, String[] deletedStates) {
		initWidget(uiBinder.createAndBindUi(this));
		this.presenter = presenter;
		this.user = user;
		
		for(String del : deletedStates)
			this.deletedStates.addItem(del);
		
		init();
	}

	private void init() {
		nameBox.setText(user.getName());
		uidBox.setText(String.valueOf(user.getUid()));
		deletedStates.setSelectedIndex(user.getDeleted());
		statusText.getElement().getStyle().setColor("#980000");
		
		if(user.isAdmin()) {
			statusText.setText("Admin");
			deletedStates.setEnabled(false);
			saveButton.setEnabled(false);
		}
	}

	@UiHandler("closeButton")
	void onCloseButtonClick(ClickEvent event) {
		presenter.hideDialog();
	}
	
	@UiHandler("saveButton")
	void onSaveButtonClick(ClickEvent event) {
		presenter.onSaveUserStateClicked(user, deletedStates.getSelectedIndex());
		statusText.setText("Bitte warten...");
	}

	public void showSaveSuccess(boolean success) {
		if(success) {
			statusText.setText("Status gespeichert!");
			Timer timer = new Timer() {
				@Override
				public void run() {
					statusText.setText("");
				}
			};
			timer.schedule(1200);
		} else {
			statusText.setText("Fehler."); // yeah
		}
	}
}