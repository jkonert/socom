package de.tud.kom.socom.web.client.administration;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

import de.tud.kom.socom.web.client.administration.AdministrationPresenter.AdministrationViewInterface;
import de.tud.kom.socom.web.client.htmlelements.ErrorList;
import de.tud.kom.socom.web.client.htmlelements.ErrorListItemView;
import de.tud.kom.socom.web.client.sharedmodels.SimpleUser;

public class UserAdministrationView extends Composite implements AdministrationViewInterface<SimpleUser> {

	private static UserAdministrationViewUiBinder uiBinder = GWT.create(UserAdministrationViewUiBinder.class);
	@UiField
	TextBox textBox;
	@UiField
	Tree tree;
	@UiField
	Button lookupButton;
	@UiField
	ErrorList errorList;

	interface UserAdministrationViewUiBinder extends UiBinder<Widget, UserAdministrationView> {
	}

	private AdministrationPresenter presenter;

	public UserAdministrationView(AdministrationPresenter presenter) {
		this.presenter = presenter;
		initWidget(uiBinder.createAndBindUi(this));
	}

	public UserAdministrationView(String firstName) {
		initWidget(uiBinder.createAndBindUi(this));
	}

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

	@UiHandler("textBox")
	void onTextBoxKeyPress(KeyPressEvent event) {
		int keyCode = event.getCharCode();
		if (keyCode == 0) {
			// Probably Firefox
			keyCode = event.getNativeEvent().getKeyCode();
		}
		if (keyCode == KeyCodes.KEY_ENTER) {
			onLookupButtonClick(null);
		}
	}

	@UiHandler("lookupButton")
	void onLookupButtonClick(ClickEvent event) {
		presenter.loadUsers(textBox.getText(), tree.getItem(0));
	}
	
	@UiHandler("tree")
	void onTreeOpen(OpenEvent<TreeItem> event) {
		TreeItem item = event.getTarget();
		if(item.getText().length() > 1) return; // search field hack
		String first = item.getText();
		presenter.loadUsers(first, item);
	}

	@Override
	public void updateInformation(List<SimpleUser> information, UIObject parent) {
		if(information == null) {
			//..
		} else {
			List<SimpleUser> userlist = information;
			TreeItem item = (TreeItem) parent;
			if(item.getText().length() == 1) item.removeItems(); // remove "Loading..." (w/ search field hack)
			else
			{
				TreeItem seachField = item.getChild(0);
				item.removeItems();
				item.addItem(seachField);
			}
			
			if(userlist.isEmpty()) // if search field AND no user is found
			{
				Label noUserFoundLabel = new Label("Keinen Nutzer gefunden..");
				item.addItem(noUserFoundLabel);
			}
			for(final SimpleUser user : userlist) 
			{
				Panel p = new HorizontalPanel();
				Anchor a = new Anchor(user.getName());
				a.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						presenter.onUserClicked(user);
					}
				});
				p.add(a);
				if(user.isAdmin()) p.add(new Label(" (A)"));
//				p.addStyleName();
				item.addItem(p);
			}
		}
	}
}