package de.tud.kom.socom.web.client.navigation;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;

import de.tud.kom.socom.web.client.htmlelements.ListItem;

public class NavigationView extends Composite implements NavigationPresenter.NavigationViewInterface {

	@UiTemplate("NavigationView.ui.xml")
	interface NavigationViewUiBinder extends UiBinder<Widget, NavigationView> {
	}
	private static NavigationViewUiBinder uiBinder = GWT.create(NavigationViewUiBinder.class);

	@UiField ListItem liGames;
	@UiField ListItem liContent;
	@UiField ListItem liInfluences;
	@UiField ListItem liAchievements;
	@UiField ListItem liProfile;
	@UiField ListItem liAdmin;
	
	@UiField InlineHyperlink games;
	@UiField InlineHyperlink content;
	@UiField InlineHyperlink influences;
	@UiField InlineHyperlink profile;
	@UiField InlineHyperlink achievements;
	@UiField InlineHyperlink admin;
	
	private NavigationPresenter presenter;

	public NavigationView(NavigationPresenter presenter) {
		this.presenter = presenter;

		// old code, now XML-based via UI-Binder
		// UnorderedList navList = new UnorderedList();
		// ...
		// initWidget(navList);

		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void setGamesVisibility(boolean visibility) {
		liGames.setVisible(visibility);
	}
	
	@Override
	public void setProfileVisibility(boolean visibility) {
		liProfile.setVisible(visibility);
	}

	@Override
	public void setAdminVisibility(boolean visibility) {
		liAdmin.setVisible(visibility);
	}
}
