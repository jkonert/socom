package de.tud.kom.socom.web.client.administration;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

import de.tud.kom.socom.web.client.administration.AdministrationPresenter.AdministrationViewInterface;
import de.tud.kom.socom.web.client.baseelements.viewerrors.ErrorNotAnAdminView;
import de.tud.kom.socom.web.client.htmlelements.ErrorList;
import de.tud.kom.socom.web.client.htmlelements.ErrorListItemView;

public class IllegalAccessAdministrationView extends Composite  implements AdministrationViewInterface<Object>{

	private static IllegalAccessAdministrationViewUiBinder uiBinder = GWT.create(IllegalAccessAdministrationViewUiBinder.class);

	interface IllegalAccessAdministrationViewUiBinder extends UiBinder<Widget, IllegalAccessAdministrationView> {
	}
	
	private static ErrorNotAnAdminView notAdminErrorView = new ErrorNotAnAdminView();

	public IllegalAccessAdministrationView() {
		initWidget(uiBinder.createAndBindUi(this));
		showError(notAdminErrorView);
	}
	
	@UiField
	ErrorList errorList;

	@Override
	public void showError(ErrorListItemView error) {
		this.errorList.addError(error);
	}

	@Override
	public void hideErrors() {
		this.errorList.clear();
	}

	@Override
	public void hideError(ErrorListItemView error) {
		this.errorList.removeError(error);
	}

	@Override
	public void updateInformation(List<Object> res, UIObject parent) {
	}
}
