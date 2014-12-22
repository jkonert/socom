package de.tud.kom.socom.web.client.util.notfound;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import de.tud.kom.socom.web.client.baseelements.ViewWithErrorsInterface;
import de.tud.kom.socom.web.client.htmlelements.ErrorList;
import de.tud.kom.socom.web.client.htmlelements.ErrorListItemView;

public class PageNotFoundView extends Composite implements ViewWithErrorsInterface  {

	private static PageNotFoundUiBinder uiBinder = GWT.create(PageNotFoundUiBinder.class);
	private PageNotFoundPresenter presenter;

	interface PageNotFoundUiBinder extends UiBinder<Widget, PageNotFoundView> {
	}
	
	public PageNotFoundView(PageNotFoundPresenter presenter) {
		this.presenter = presenter;
		initWidget(uiBinder.createAndBindUi(this));
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
}
