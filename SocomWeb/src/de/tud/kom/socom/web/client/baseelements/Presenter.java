package de.tud.kom.socom.web.client.baseelements;

import com.google.gwt.user.client.ui.RootPanel;


/** Interface for all Socom presenters to keep a minimum common method set available at all presenters
 * 
 * @author jkonert
 *
 */
public interface Presenter {
	
	/** (re-)initialization of the View components and state of the Presenter. It creates the View objects and sets up the Event binding.
	 * Should be called by Presenters constructor, too.
	 * Can be called separately by AppController in case of reset of specific view components (Presenters).
	 */
	void init();	
	
	/** adds the current view-object of Presenter to the Panel.
	 * 
	 * @param targetPanel
	 */	
	void go(RootPanel targetPanel);	
	
	
}
