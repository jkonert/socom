package de.tud.kom.socom.web.client.content;

import com.google.gwt.user.client.ui.RootPanel;

import de.tud.kom.socom.web.client.AppController;
import de.tud.kom.socom.web.client.baseelements.AbstractMainPresenter;
import de.tud.kom.socom.web.client.baseelements.ViewWithErrorsInterface;


public class ContentPresenter extends AbstractMainPresenter { 
	
	public interface ContentViewInterface extends ViewWithErrorsInterface
	{
		
	}
	
	// FIXME this class is only a skeleton..not finished YET  + it's View as well
	
	private static ContentPresenter instance;	
	private ContentViewInterface view;
	
	private ContentPresenter(AppController appController) 
	{
		super(appController);
		init();
	}
	
	public static ContentPresenter getInstance(AppController appController)
	{
		if (instance == null) instance = new ContentPresenter(appController);		
		return instance;
	}
	
	@Override 
	public void init()
	{
		this.view = new ContentView(this); 
		setView(view);
	}
	
	@Override
	public void go(RootPanel targetPanel)
	{
		setTargetPanel(targetPanel);				
	}

}
