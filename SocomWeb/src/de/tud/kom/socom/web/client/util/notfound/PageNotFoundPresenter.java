package de.tud.kom.socom.web.client.util.notfound;

import com.google.gwt.user.client.ui.RootPanel;

import de.tud.kom.socom.web.client.AppController;
import de.tud.kom.socom.web.client.baseelements.AbstractMainPresenter;
import de.tud.kom.socom.web.client.baseelements.viewerrors.ErrorPageNotFoundView;

public class PageNotFoundPresenter extends AbstractMainPresenter {

	private static PageNotFoundPresenter instance;
	private PageNotFoundView view;


	private PageNotFoundPresenter(AppController appController) {
		super(appController);
		init();
	}

	public static PageNotFoundPresenter getInstance(AppController appController) {
		if (instance == null)
			instance = new PageNotFoundPresenter(appController);
		return instance;
	}

	@Override
	public void init() {
		view = new PageNotFoundView(this);
		setView(view);
	}

	@Override
	public void go(RootPanel targetPanel) {
		setTargetPanel(targetPanel);
	}

	public void showErrorPageNotFound() {
		view.showError(new ErrorPageNotFoundView());
	}
}