package de.tud.kom.socom.web.client.games;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Widget;

import de.tud.kom.socom.web.client.baseelements.ViewInterface;

public class GameView extends Composite implements ViewInterface {

	private static GameViewUiBinder uiBinder = GWT.create(GameViewUiBinder.class);
	private GamesPresenter presenter;

	interface GameViewUiBinder extends UiBinder<Widget, GameView> {
	}
	
	@UiField
	InlineHyperlink link;
	
	public GameView(GamesPresenter presenter, String name, String ident, String image, String description) {
		this.presenter = presenter;
		initWidget(uiBinder.createAndBindUi(this));
		applyGame(name, ident, image, description);
	}

	private void applyGame(String name, String ident, String image, String description) {
		//TODO show nicer w/ image and stuff
		link.setText(name);
		link.setTargetHistoryToken(ident + "/content");
	}
}