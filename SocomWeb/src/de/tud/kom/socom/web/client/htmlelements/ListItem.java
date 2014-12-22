package de.tud.kom.socom.web.client.htmlelements;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

/** Provides the GWT implementation for HTML ListItems (LI).
 * Can be used with UnorderedList (UL)  or OrderedList (not yet implemented)
 * 
 * @author jkonert
 *
 */
public class ListItem extends HTMLPanel {

	public ListItem(String html) {
		super("LI", html);
	}

	public ListItem(SafeHtml html) {
		super("LI", html.toString());
	}

	public ListItem(Widget w)
	{
		super("LI", "");
		add(w);
	}
	
//	public void setDir(String dir)
//	{
//        // Set an attribute specific to this tag
//        ((UListElement) getElement().cast()).setDir(dir);
//	}


        
}
