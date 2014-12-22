package de.tud.kom.socom.web.client.login;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import de.tud.kom.socom.web.client.htmlelements.ErrorListItemView;

public class LoginWindowView extends Composite implements LoginWindowPresenter.LoginWindowViewInterface {

	
	@UiTemplate("LoginWindowView.ui.xml")
	interface LoginWindowViewUiBinder extends UiBinder<Widget, LoginWindowView> {
	}
	private static LoginWindowViewUiBinder uiBinder = GWT.create(LoginWindowViewUiBinder.class);

	@UiField TextBox userName;		
	@UiField PasswordTextBox password;
	@UiField Button buttonLogin;
	@UiField Button buttonClose;
	@UiField InlineHyperlink passwordForgotten;

	@UiField Button buttonFacebook;		// provide a generic network independent solution (networkLoginButton + popup for selection of network to use)	
	
	private LoginWindowPresenter presenter;

	public LoginWindowView(LoginWindowPresenter presenter) {
		this.presenter = presenter;
		//...
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	@UiHandler("userName")
	void onKeyPressUserNameTextfield (KeyPressEvent event)
	{
		// decided to do this logic here and NOT forward it to Presenter as it is a simple GUI-focussed behaviour
		int keyCode = event.getCharCode();
		if (keyCode == 0) {
			// Probably Firefox
			keyCode = event.getNativeEvent().getKeyCode();
		}
		if (keyCode == KeyCodes.KEY_ENTER) {
			password.setFocus(true);
		}
	}
	
	@UiHandler("password")
	public void onKeyPress(KeyPressEvent event) {
		int keyCode = event.getCharCode();
		if (keyCode == 0) {
			// Probably Firefox
			keyCode = event.getNativeEvent().getKeyCode();
		}
		if (keyCode == KeyCodes.KEY_ENTER) {
			presenter.onLoginButtonClicked();
		}
	}

	
	@UiHandler("buttonLogin")
	void onLoginButtonClicked(ClickEvent event)
	{
		presenter.onLoginButtonClicked();
	}
	
	@UiHandler("buttonClose")
	void onCloseButtonClicked(ClickEvent event)
	{
		presenter.onCloseButtonClicked();
	}
	
	@UiHandler("buttonFacebook")
	void onFacebookButtonClicked(ClickEvent event)
	{
		presenter.onFacebookButtonClicked();
	}
	
	@UiHandler("passwordForgotten")
	void onPasswordForgottenClicked(ClickEvent event)
	{
		presenter.onPasswordForgottenClicked();
	}

	@Override
	public void setLoginFocus() {
		userName.setFocus(true);		
	}
	
	@Override
	public void setVisibility(boolean visibility)
	{
		this.setVisible(visibility);		
	}
	
	@Override
	public Widget asWidget()
	{
		return this;
	}

	@Override
	public String getUserNameText() {
		return userName.getValue();
	}

	@Override
	public String getPasswordText() {
		return password.getValue();
	}

	@Override
	public void clear() {
		userName.setText("");
		password.setText("");
		
	}

	@Override
	public void showError(ErrorListItemView error) {
		// TODO JK: add a Widget with errorDisplay
		
	}

	@Override
	public void hideErrors() {
		// TODO JK: add a Widget with errorDisplay
		
	}

	@Override
	public void hideError(ErrorListItemView error) {
		// // TODO JK: add a Widget that will display errrors and handle them 
		
	}

	@Override
	public void setSNLoginButtonEnabled(boolean enable) {
		buttonFacebook.setEnabled(enable);
		if(!enable) {
			buttonFacebook.setTitle("Facebook Login nur für konkrete Spiele möglich.");
		}
	}
}
