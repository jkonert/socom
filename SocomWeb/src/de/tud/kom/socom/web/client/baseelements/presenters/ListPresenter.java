package de.tud.kom.socom.web.client.baseelements.presenters;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.view.client.AbstractDataProvider;

import de.tud.kom.socom.web.client.AppController;
import de.tud.kom.socom.web.client.baseelements.AbstractMainPresenter;
import de.tud.kom.socom.web.client.baseelements.Presenter;
import de.tud.kom.socom.web.client.baseelements.ViewWithErrorsInterface;
import de.tud.kom.socom.web.client.eventhandler.LoginEventHandler;
import de.tud.kom.socom.web.client.events.LoginEvent;

import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;

/** this presenter shows a list of active and visible <Items> for current user  
 * @author jkonert
 *
 */

abstract public class ListPresenter<LISTITEM extends ListItemInterface> extends AbstractMainPresenter implements Presenter, LoginEventHandler
		{
	

	public interface ListViewInterface<LISTITEM> extends ViewWithErrorsInterface {
		public void setDataProvider(AbstractDataProvider<LISTITEM> itemDataProvider);
		public void setAdministrationVisible(boolean visible);
	}
	
	
	private ListViewInterface<LISTITEM> view;		
	private Cell<LISTITEM> itemCellPrototype = null;
			

	public ListPresenter(Cell<LISTITEM> itemCellPrototype, AppController appController)  {
		super(appController);
		this.itemCellPrototype = itemCellPrototype;
		init();
	}
	
	/** to be implemented to reach on SelectionChanges
	 *   get selectedItem by mode.getSelectedItem()
	 * @param event
	 * @param model
	 */
	public abstract void onSelectionChanged(SelectionChangeEvent event, SingleSelectionModel<LISTITEM> model);
	
	@Override
	public void init() {
		this.view = new ListView<LISTITEM>(this, itemCellPrototype);
		this.setListView(view);		
		this.getAppController().getEventHandler().addHandler(LoginEvent.TYPE, this);
	}
	
	@Override public void go(RootPanel targetPanel)		
	{
		setTargetPanel(targetPanel);
	}
	
	final protected  void setListView(ListViewInterface<LISTITEM> view)
	{
		this.setView(view);			
	}
	
	/** called by subclasses to "attach" this listview as display for their Dataproviders of <LISTITEM>s by calling ListViewInterface<LISTITEM>.setDataProvider()
	 * 
	 * @return
	 */
	final protected ListViewInterface<LISTITEM> getListView()
	{
		return this.view;
	}
	
}