package de.tud.kom.socom.web.client.htmlelements;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import de.tud.kom.socom.web.client.baseelements.viewerrors.ErrorView;


/** a View Widget that displays given ErrorViews as a UL-List
 * 
 * @author jkonert
 *
 */
public class ErrorList extends Composite
{
	@UiTemplate("ErrorListView.ui.xml")
	interface inf extends UiBinder<Widget, ErrorList> {
	}
	private static inf uiBinder = GWT.create(inf.class);

	private Map<ErrorListItemView, ListItem> mapping = new HashMap<ErrorListItemView, ListItem>();
	
	@UiField UnorderedList errorList;
	
		
	public ErrorList() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	public void setErrors(List<ErrorListItemView> errors)
	{
		clear();
		addErrors(errors);	
	}
	
	public void removeErrors(List<ErrorListItemView> errors)
	{
		if (errors == null) return;
		for (ErrorListItemView v: errors)
		{
			ListItem item = mapping.get(v);
			if (item != null)
			{
				this.errorList.remove(v);				
			}
			mapping.remove(v); // remove even if not found to clean up inconsistencies..
		}
		checkVisibility();
	}
	
	public void removeError(ErrorListItemView error)
	{
		List<ErrorListItemView> l = new LinkedList<ErrorListItemView>();
		l.add(error);
		removeErrors(l);
	}
	
	/** is aware of formerly given objects and only adds errors not added before
	 * 
	 * @param errors
	 */
	public void addErrors(List<ErrorListItemView> errors)
	{
		if (errors == null) return;
		for (ErrorListItemView v: errors)
		{
			if (!this.mapping.containsKey(v))
			{
				ListItem item = new ListItem(v.asWidget());
				//XXX sort? first errors, then infos?
				if(v instanceof ErrorView)
					item.addStyleName("error");
				else 
					item.addStyleName("info");
				this.mapping.put(v, item);
				this.errorList.add(item);
			}
		}
		checkVisibility();
	}
	
	public void addError(ErrorListItemView error)
	{
		List<ErrorListItemView> l = new LinkedList<ErrorListItemView>();
		l.add(error);
		addErrors(l);
	}
	
	public boolean hasErrors()
	{
		return this.errorList.getWidgetCount() > 0;
	}
	
	/** removes all errors and hides
	 * 
	 */
	public void clear()
	{
		while(this.errorList.getWidgetCount() > 0)
		{
			this.errorList.remove(0);			
		}
		this.mapping.clear();
		hide();
	}
	
	/** manually hides the list (adds style class "hidden"). this is managed internally automatically depending on number of errors.
	 *  0 errors = hide, > 0 errors shows.
	 */
	public void hide()
	{
		this.errorList.addStyleName("hidden");
	}
	
	/** manually displays the list (removes style class "hidden"). this is managed internally automatically depending on number of errors.
	 *  0 errors = hide, > 0 errors shows.
	 */
	public void show()
	{
		this.errorList.removeStyleName("hidden");
	}

	private void checkVisibility() {
		if (hasErrors()) show();
		else hide();		
	}
}
