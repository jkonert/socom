package de.tud.kom.socom.web.client.htmlelements;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.ComplexPanel;

/** provides the GWT implementation of an Unordered List (UL).
 * Add Instances of ListItem (LI) to it.
 * @author jkonert
 *
 */
public class UnorderedList extends ComplexPanel {

	public UnorderedList() {
	        setElement(DOM.createElement("UL"));
	}
	
	public void add(ListItem w) {
	        super.add(w, getElement());
	}
	
	public void insert(ListItem w, int beforeIndex) {
	        super.insert(w, getElement(), beforeIndex, true);
	}

}
