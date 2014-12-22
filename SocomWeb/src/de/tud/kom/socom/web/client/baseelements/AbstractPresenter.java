package de.tud.kom.socom.web.client.baseelements;

import com.google.gwt.user.client.ui.RootPanel;

import de.tud.kom.socom.web.client.AppController;
import de.tud.kom.socom.web.client.baseelements.viewerrors.ErrorView;

/** parent class for all presenters managing some common view-handling and error-handling
 * 
 * @author jkonert
 *
 */
public abstract class AbstractPresenter implements Presenter
{

	private ViewWithErrorsInterface view;   // on purpose no getView() as Presenter needs to handle it anyway specificaly with own Interfaces..
	private RootPanel targetPanel;
	private AppController appController;
	
	public AbstractPresenter(AppController appController)
	{
		this.appController = appController;
	}
	
	
	/** sets the main View Widget for this presenter. It is not automatically added to TargetPanel
	 *  it is set here to enable central methods like showError  and hideErrors to directly operate with the View.
	 *  and to enable an automatic management of show/hide view from targetPanel and automatically re-attach if TargetPanel changes etc.
	 *  
	 *  Means: if View has been set before and has been shown before, it is here automatically replaced by the new one.
	 *  */
	protected final void setView(ViewWithErrorsInterface  view)
	{
		if (this.view != null && this.targetPanel != null)
		{
			if (this.targetPanel.getWidgetIndex(this.view.asWidget()) >= 0)
			{
				this.targetPanel.remove(this.view.asWidget());
			}
			this.targetPanel.add(view.asWidget());
		}
		this.view = view;
	}
	

	protected final RootPanel getTargetPanel() {
		return targetPanel;
	}

	/** sets or exchanges the targetPanel for view. if a view has been set and attached to former targetPanel
	 *  this replaces the targetpanel and adds the view automatically to the new panel
	 * 
	 * @param targetPanel
	 */
	protected final void setTargetPanel(RootPanel targetPanel) {
				
		if (this.view != null && this.targetPanel != null)
		{
			if (this.targetPanel.getWidgetIndex(this.view.asWidget()) >= 0)
			{
				this.targetPanel.remove(this.view.asWidget());
			}			
		}
		this.targetPanel = targetPanel;
		if (this.view != null) targetPanel.add(this.view.asWidget());
	}
	
	public final AppController getAppController() //FIXME protected instaed of public
	{
		return appController;
	}
	
	/** displays given error with the set view of Presenter */
	public final void showError(ErrorView error)
	{
		this.view.showError(error);
	}
	
	/** tells the view to hide the errors */
	protected final void hideErrors()
	{
		this.view.hideErrors();
	}

}
