package de.tud.kom.socom.web.client.login;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;

import de.tud.kom.socom.web.client.htmlelements.ErrorListItemView;

public class LoginView extends Composite implements LoginPresenter.LoginViewInterface {

	
	@UiTemplate("LoginView.ui.xml")
	interface LoginViewUiBinder extends UiBinder<Widget, LoginView> {}
	private static LoginViewUiBinder uiBinder = GWT.create(LoginViewUiBinder.class);

	@UiField InlineLabel userNamePrefix;
	@UiField InlineLabel userName;
	@UiField Button buttonLogin;
	@UiField Button buttonLogout;
	//@UiField PasswordTextBox password;

	//@UiField Button facebookButton;		// provide a generic network independent solution (networkLoginButton + popup for selection of network to use)	
	
	private LoginPresenter presenter;

	public LoginView(LoginPresenter presenter) {
		this.presenter = presenter;
		initWidget(uiBinder.createAndBindUi(this));		
	}

	
	@UiHandler("buttonLogin")
	void onLoginButtonClicked(ClickEvent event)
	{
		presenter.onLoginButtonClicked();
	}
	
	@UiHandler("buttonLogout")
	void onLogoutButtonClicked(ClickEvent event)
	{
		presenter.onLogoutButtonClicked();
	}


	@Override
	public void setLoginVisible() {
		userNamePrefix.setVisible(false);
		userName.setVisible(false);
		buttonLogout.setVisible(false);
		
		buttonLogin.setVisible(true);				
	}


	@Override
	public void setLogoutVisible(String username) {		
		buttonLogin.setVisible(false);	
		
		if (username != null)
		{	
			userNamePrefix.setVisible(true);			
			userName.setText(SafeHtmlUtils.htmlEscapeAllowEntities(username));
			userName.setVisible(true);
		}
		buttonLogout.setVisible(true);			
		
	}


	@Override
	public void showError(ErrorListItemView error) {
		// TODO JK: add a Widget that will display errrors and handle them 
		
	}


	@Override
	public void hideErrors() {
		// TODO JK: add a Widget that will display errrors and handle them 
		
	}


	@Override
	public void hideError(ErrorListItemView error) {
		// TODO JK: add a Widget that will display errrors and handle them 
		
	}
}
