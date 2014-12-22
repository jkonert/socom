package de.tud.kom.socom.web.client.administration.itemadministration;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import de.tud.kom.socom.web.client.util.Visibility;

public class ItemAdministration extends Composite {

	@UiTemplate("ItemAdministrationView.ui.xml")
	interface inf extends UiBinder<Widget, ItemAdministration> {
	}
	private static inf uiBinder = GWT.create(inf.class);

	public interface DeleteButtonCallback {
		void onClicked(ItemAdministrationState state);
	}
	
	public interface ChangeVisibilityButtonCallback {
		void onClicked(int newVisibility);
	}

	public ItemAdministration() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField Button deleteButton;
	@UiField Button undeleteButton;
	@UiField Button visibilityButton;
	@UiField VisibilityPopup visibilityDialog;
	
	private DeleteButtonCallback callback;
	private boolean onimage;

	public void setState(ItemAdministrationState state) {
		switch (state) {
		case none:
			deleteButton.setVisible(false);
			undeleteButton.setVisible(false);
			visibilityButton.setVisible(false);
			break;
		case undelete:
			deleteButton.setVisible(false);
			undeleteButton.setVisible(true);
			visibilityButton.setVisible(true);
			break;
		case delete:
			deleteButton.setVisible(true);
			undeleteButton.setVisible(false);
			visibilityButton.setVisible(true);
			break;
		default:
			deleteButton.setVisible(false);
			undeleteButton.setVisible(false);
			visibilityButton.setVisible(false);
			break;
		}
	}
	
	public void setCurrentVisibility(int visibility) {
		visibilityDialog.setCurrent(visibility);
		if(onimage) {
			visibilityButton.setStyleName("visibility-onimage");
			return;
		}
		switch(visibility) {
		case -1 : visibilityButton.addStyleName("hidden"); break;
		case Visibility.PUBLIC : visibilityButton.setStyleName("public-button"); break;
		case Visibility.PRIVATE : visibilityButton.setStyleName("private-button"); break;
		case Visibility.FRIENDS : visibilityButton.setStyleName("friend-button"); break;
		case Visibility.SOCOM_INTERN : visibilityButton.setStyleName("socom-mini-button"); break;
		default: visibilityButton.addStyleName("hidden"); break;
		}
	}

	public void setCallback(DeleteButtonCallback callback) {
		this.callback = callback;
	}

	public void setState(ItemAdministrationState state, DeleteButtonCallback callback) {
		setState(state);
		setCallback(callback);
	}
	
	public void setVisibilityCallback(ChangeVisibilityButtonCallback callback){
		visibilityDialog.setCallback(callback);
	}

	@UiHandler("deleteButton")
	public void onDeleteButtonClicked(ClickEvent event) {
		if (callback != null)
			callback.onClicked(ItemAdministrationState.delete);
	}

	@UiHandler("undeleteButton")
	public void onUndeleteButtonClicked(ClickEvent event) {
		if (callback != null)
			callback.onClicked(ItemAdministrationState.undelete);
	}
	
	@UiHandler("visibilityButton")
	public void onVisibilityButtonClicked(ClickEvent event) {
		visibilityDialog.removeStyleName("hidden");
	}
	
	public void changeToOnImageView(){
		this.onimage = true;
		deleteButton.addStyleName("onimage");
		undeleteButton.addStyleName("onimage");
	}
}