package de.tud.kom.socom.web.client.achievements;

import com.google.gwt.user.client.ui.RootPanel;

import de.tud.kom.socom.web.client.AppController;
import de.tud.kom.socom.web.client.baseelements.AbstractMainPresenter;
import de.tud.kom.socom.web.client.baseelements.ViewWithErrorsInterface;


public class AchievementPresenter extends AbstractMainPresenter { 
	
	public interface AchievementViewInterface extends ViewWithErrorsInterface
	{
		
	}
	
	// FIXME this class is only a skeleton..not finished YET  + it's View as well
	
	private static AchievementPresenter instance;
	private AchievementViewInterface view;
	
	private AchievementPresenter(AppController appController) 
	{
		super(appController);		
		init();
	}
	
	public static AchievementPresenter getInstance(AppController appController)
	{
		if (instance == null) instance = new AchievementPresenter(appController);		
		return instance;
	}
	
	@Override 
	public void init()
	{
		this.view = new AchievementView(this);
		setView(view);
		
	}
	
	@Override
	public void go(RootPanel targetPanel)
	{	
		setTargetPanel(targetPanel);
	}

}
