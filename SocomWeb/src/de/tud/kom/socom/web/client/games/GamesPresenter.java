package de.tud.kom.socom.web.client.games;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

import de.tud.kom.socom.web.client.AppController;
import de.tud.kom.socom.web.client.baseelements.AbstractMainPresenter;
import de.tud.kom.socom.web.client.baseelements.ViewInterface;
import de.tud.kom.socom.web.client.baseelements.ViewWithErrorsInterface;
import de.tud.kom.socom.web.client.baseelements.viewerrors.ErrorServerUnknownErrorView;

public class GamesPresenter extends AbstractMainPresenter {

	public interface GamesViewInterface<T> extends ViewInterface, ViewWithErrorsInterface {
		public void addGame(String name, String ident, String image, String description);
		public void showLoading(boolean show);
	}

	private static GamesPresenter instance;
	private GamesViewInterface<?> view;


	private GamesPresenter(AppController appController) {
		super(appController);
		init();
	}

	public static GamesPresenter getInstance(AppController appController) {
		if (instance == null)
			instance = new GamesPresenter(appController);
		return instance;
	}

	@Override
	public void init() {
		this.view = new GamesView(this);
		setView(view);
		
		loadGames();
	}

	private void loadGames() {
		getAppController().getRPCFactory().getGameService().getAllGames(new AsyncCallback<String[][]>() {
			
			@Override
			public void onSuccess(String[][] result) {
				view.showLoading(false);
				if(result.length == 0 || result[0].length != 5) onFailure(null);
				
				for(int i = 0; i < result.length; i++) {
					String[] game = result[i];
					view.addGame(game[0], game[1], game[2], game[3]);
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				view.showError(new ErrorServerUnknownErrorView());
			}
		});
	}

	@Override
	public void go(RootPanel targetPanel) {
		setTargetPanel(targetPanel);
	}
}