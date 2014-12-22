package de.tud.kom.socom.web.client.baseelements.presenters;

import java.util.LinkedList;
import java.util.List;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.AbstractDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

import de.tud.kom.socom.web.client.baseelements.presenters.ListPresenter.ListViewInterface;
import de.tud.kom.socom.web.client.htmlelements.ErrorList;
import de.tud.kom.socom.web.client.htmlelements.ErrorListItemView;

/** 
 * @author jkonert
 *
 */
public class ListView<LISTITEM extends ListItemInterface> extends Composite implements ListViewInterface<LISTITEM> {

	@UiTemplate("ListView.ui.xml")
	interface ListViewUiBinder extends UiBinder<Widget, ListView<? extends ListItemInterface>> { }
	
	private static ListViewUiBinder uiBinder = GWT.create(ListViewUiBinder.class);		
	private ListPresenter<LISTITEM> presenter;
		
			
	@UiField ErrorList errorList;
	@UiField(provided=true) SimplePager pagerTop;
	@UiField(provided=true) SimplePager pagerBottom;
	@UiField(provided=true) CellList<LISTITEM> cellList;
	private AbstractDataProvider<LISTITEM> dataProvider;
	
	
	public ListView(final ListPresenter<LISTITEM> presenter, Cell<LISTITEM> itemDisplayCell) {
		this.presenter = presenter;	
		this.cellList = new CellList<LISTITEM>(itemDisplayCell);
		
		cellList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);

	    // Add a selection model to handle user selection.
	    final SingleSelectionModel<LISTITEM> selectionModel = new SingleSelectionModel<LISTITEM>();
	    cellList.setSelectionModel(selectionModel);
	    selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
	      public void onSelectionChange(SelectionChangeEvent event) {
	        LISTITEM selected = selectionModel.getSelectedObject();
	        if (selected != null) {
	          presenter.onSelectionChanged(event, selectionModel);
	        }
	      }
	    });
		this.pagerTop = new SimplePager();
		this.pagerTop.setDisplay(this.cellList);
		this.pagerBottom = new SimplePager();
		this.pagerBottom.setDisplay(this.cellList);
		
		initWidget((Widget)uiBinder.createAndBindUi(this));		
	}
		
	/**
	 * Handling of Item display/remove is done by DataProvider directly calling the CellList for Updates.
	 * Paging event handling is done by presenter...informing the data-provider. 
	 */
	
	@Override 
	public void setDataProvider(AbstractDataProvider<LISTITEM> itemDataProvider)
	{		
		itemDataProvider.addDataDisplay(this.cellList);
		if (this.dataProvider != null) this.dataProvider.removeDataDisplay(this.cellList);
		this.dataProvider = itemDataProvider;		
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
	public void setAdministrationVisible(boolean visible) {		
		// FIXME JK: think about it....by extending Cell or find a way to let items display the adminpanel
	}



}
