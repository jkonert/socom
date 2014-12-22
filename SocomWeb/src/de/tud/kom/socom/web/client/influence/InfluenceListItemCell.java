package de.tud.kom.socom.web.client.influence;

import java.util.Set;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

import de.tud.kom.socom.web.client.influence.InfluenceView.InfluenceViewUiBinder;
import de.tud.kom.socom.web.client.sharedmodels.Influence;

/** Flyweight cell implementation using a view template to render one list cell of an influence
 * 
 * @author jkonert
 *
 */
public class InfluenceListItemCell extends AbstractCell<Influence>
{
	private InfluenceListItemCellView view;
	
	public InfluenceListItemCell()
	{
		view = new InfluenceListItemCellView();
	}
	
	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context,
		Influence item, SafeHtmlBuilder sb)
	{
		// "context" could contain a unique key if on creation of CellList a keyprovider was given/implemented
		// if needed, see KeyProviderExample https://developers.google.com/web-toolkit/doc/latest/DevGuideUiCellWidgets?hl=fr#custom-cell
		
		view.setDataAndRender(context.getIndex(), item.getType(), item.getQuestion(), item.getGameName(), item.getOwnerName(),item.IsRunning(),item.getTotalNumberOfGivenVotes(), item.getTimeout(),sb);
		
	}

	
}
