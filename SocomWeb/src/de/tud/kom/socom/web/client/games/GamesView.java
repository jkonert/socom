package de.tud.kom.socom.web.client.games;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import de.tud.kom.socom.web.client.games.GamesPresenter.GamesViewInterface;
import de.tud.kom.socom.web.client.htmlelements.ErrorList;
import de.tud.kom.socom.web.client.htmlelements.ErrorListItemView;

public class GamesView extends Composite implements GamesViewInterface<Object> {

	private static GamesViewUiBinder uiBinder = GWT.create(GamesViewUiBinder.class);
	private GamesPresenter presenter;

	interface GamesViewUiBinder extends UiBinder<Widget, GamesView> {
	}
	
	@UiField
	Label loadingText;
	@UiField
	ErrorList errorList;
	@UiField
	HTMLPanel gameElements;
	
	public GamesView(GamesPresenter presenter) {
		this.presenter = presenter;
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

	@Override
	public void addGame(String name, String ident, String image, String description) {
		GameView gameView = new GameView(presenter, name, ident, image, description);
		gameElements.add(gameView.asWidget());
	}

	@Override
	public void showLoading(boolean show) {
		if(show)
			loadingText.removeStyleName("hidden");
		else
			loadingText.addStyleName("hidden");
	}
}
