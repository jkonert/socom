package de.tud.kom.socom.web.client.baseelements;

import de.tud.kom.socom.web.client.htmlelements.ErrorListItemView;

/** for MAIN Views that are managed by Presenters this is the interface to control error message displaying in Views
 * 
 * @author jkonert
 *
 */
public interface ViewWithErrorsInterface extends ViewInterface
{	
	void showError(ErrorListItemView error);
	/** use the same object instance as in showError()*/
	void hideError(ErrorListItemView error);
	void hideErrors();
}
