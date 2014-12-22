package de.tud.kom.socom.web.client;

import com.google.gwt.user.client.History;

import de.tud.kom.socom.web.client.AppController.Presenters;

/** provides more encapsulated and convenient access to History handling 
 *  
 * @author jkonert
 *
 */
public final class HistoryManager extends History
{
	
	public static final HistoryToken getHistoryToken()
	{
		return HistoryToken.fromUrlHistoryToken(History.getToken()); 
	}
	
	public static final void newItem(HistoryToken token)
	{
		newItem(token, false);
	}
	
	public static final void newItem(String gamePart, Presenters appPart)
	{
		newItem(new HistoryToken(gamePart, appPart), false);
	}
	
	public static final void newItem(HistoryToken token, boolean issueEvent )
	{
		History.newItem(token.toHistoryTokenString(), issueEvent);
	}
	
	/**
	 * @deprecated better use encapsulation newItem(HistoryToken token, ..)  or newItem(AppParts appPart)
	 * @param item
	 */
	@Deprecated public static final void newItem(String item)
	{
		History.newItem(item);
	}

	public static boolean isTokenEmpty() {
		return History.getToken().equals("");
	}
	
}
