package de.tud.kom.socom.web.client.baseelements;

import de.tud.kom.socom.web.client.AppController;
import de.tud.kom.socom.web.client.HistoryToken;

/** parent class for all MAIN presenters that are loaded in main page area
 * 
 * @author jkonert
 *
 */
public abstract class AbstractMainPresenter extends AbstractPresenter implements MainPresenter
{
	
	public static final String DEFAULT_TITLE = "";

	public AbstractMainPresenter(AppController appController)
	{
		super(appController);
	}
	
	@Override public String getPageTitle(HistoryToken token)
	{
		return DEFAULT_TITLE;
	}

}
