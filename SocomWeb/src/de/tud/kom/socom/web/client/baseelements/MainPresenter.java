package de.tud.kom.socom.web.client.baseelements;


import de.tud.kom.socom.web.client.HistoryToken;


/** Interface for Socom presenters that are loaded as main page presenter
 * 
 * @author jkonert
 *
 */
public interface MainPresenter extends Presenter {
	
	
	/** called by AppController on each HistoryToken change to ask for a title to set; should be overridden by child classes*/
	public String getPageTitle(HistoryToken token);	
	
	
}
