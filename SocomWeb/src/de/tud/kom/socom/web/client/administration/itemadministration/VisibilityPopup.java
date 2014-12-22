package de.tud.kom.socom.web.client.administration.itemadministration;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import de.tud.kom.socom.web.client.administration.itemadministration.ItemAdministration.ChangeVisibilityButtonCallback;
import de.tud.kom.socom.web.client.util.Visibility;

public class VisibilityPopup extends Composite {

	private static VisibilityPopupUiBinder uiBinder = GWT
			.create(VisibilityPopupUiBinder.class);

	interface VisibilityPopupUiBinder extends UiBinder<Widget, VisibilityPopup> {
	}
	
	@UiField Button buttonClose;
	@UiField Anchor publicLink;
	@UiField Anchor friendLink;
	@UiField Anchor privateLink;
	@UiField Label publicImage;
	@UiField Label friendImage;
	@UiField Label privateImage;
	@UiField Label socomImage;
	@UiField Anchor socomLink;
	private ChangeVisibilityButtonCallback callback;

	public VisibilityPopup() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	@UiHandler("buttonClose")
	public void onClose(ClickEvent e) {
		this.addStyleName("hidden");
	}

	public void setCallback(ChangeVisibilityButtonCallback callback) {
		this.callback = callback;
	}
	
	@UiHandler(value={"publicLink", "publicImage"})
	public void onPublicVisibilityClicked(ClickEvent e) {
		onVisibilityChange(Visibility.PUBLIC);
	}

	@UiHandler(value={"privateLink", "privateImage"})
	public void onPrivateVisibilityClicked(ClickEvent e) {
		onVisibilityChange(Visibility.PRIVATE);
	}
	
	@UiHandler(value={"friendLink", "friendImage"})
	public void onFriendVisibilityClicked(ClickEvent e) {
		onVisibilityChange(Visibility.FRIENDS);
	}
	
	@UiHandler(value={"socomLink", "socomImage"})
	public void onSocomVisibilityClicked(ClickEvent e) {
		onVisibilityChange(Visibility.SOCOM_INTERN);
	}
	
	private void onVisibilityChange(int newV) {
		callback.onClicked(newV);
		this.addStyleName("hidden");
	}

	public void setCurrent(int visibility) {
		publicLink.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
		friendLink.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
		privateLink.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
		socomLink.getElement().getStyle().setFontWeight(FontWeight.NORMAL);
		
		switch(visibility){
			case Visibility.PUBLIC : publicLink.getElement().getStyle().setFontWeight(FontWeight.BOLD);break;
			case Visibility.PRIVATE : privateLink.getElement().getStyle().setFontWeight(FontWeight.BOLD);break;
			case Visibility.FRIENDS : friendLink.getElement().getStyle().setFontWeight(FontWeight.BOLD);break;
			case Visibility.SOCOM_INTERN : socomLink.getElement().getStyle().setFontWeight(FontWeight.BOLD);break;
		}
	}
}