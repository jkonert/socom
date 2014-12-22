package de.tud.kom.socom.web.client.profile;


import java.util.LinkedList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import de.tud.kom.socom.web.client.htmlelements.ErrorList;
import de.tud.kom.socom.web.client.htmlelements.ErrorListItemView;

/** 
 * @author jkonert
 *
 */
public class ProfileView extends Composite implements ProfilePresenter.ProfileViewInterface {

	@UiTemplate("ProfileView.ui.xml")
	interface ProfileViewUiBinder extends UiBinder<Widget, ProfileView> {
	}
	
	private static ProfileViewUiBinder uiBinder = GWT.create(ProfileViewUiBinder.class);
		
	private ProfilePresenter presenter;
	private ErrorList errorL;

	public ProfileView(ProfilePresenter presenter) {
		this.presenter = presenter;
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void showError(ErrorListItemView error) {
		errorL.addError(error);
	}


	@Override
	public void hideErrors() {
		errorL.setErrors(new LinkedList<ErrorListItemView>());
	}


	@Override
	public void hideError(ErrorListItemView error) {
		errorL.setErrors(new LinkedList<ErrorListItemView>());
	}
	
}
