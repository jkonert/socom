package de.tud.kom.socom.web.client.content;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import de.tud.kom.socom.web.client.htmlelements.ErrorListItemView;

/** 
 * @author jkonert
 *
 */
public class ContentView extends Composite implements ContentPresenter.ContentViewInterface {

	@UiTemplate("ContentView.ui.xml")
	interface ContentViewUiBinder extends UiBinder<Widget, ContentView> {
	}
	
	private static ContentViewUiBinder uiBinder = GWT.create(ContentViewUiBinder.class);
		
	private ContentPresenter presenter;

	public ContentView(ContentPresenter presenter) {
		initWidget(uiBinder.createAndBindUi(this));
		this.presenter = presenter;
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