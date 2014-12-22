package de.tud.kom.socom.web.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;

import de.tud.kom.socom.web.client.services.achievements.SoComAchievementService;
import de.tud.kom.socom.web.client.services.achievements.SoComAchievementServiceAsync;
import de.tud.kom.socom.web.client.services.administration.SoComAdministrationService;
import de.tud.kom.socom.web.client.services.administration.SoComAdministrationServiceAsync;
import de.tud.kom.socom.web.client.services.content.SoComContentService;
import de.tud.kom.socom.web.client.services.content.SoComContentServiceAsync;
import de.tud.kom.socom.web.client.services.core.SocomWebCoreService;
import de.tud.kom.socom.web.client.services.core.SocomWebCoreServiceAsync;
import de.tud.kom.socom.web.client.services.game.SoComGameService;
import de.tud.kom.socom.web.client.services.game.SoComGameServiceAsync;
import de.tud.kom.socom.web.client.services.influence.SoComInfluenceService;
import de.tud.kom.socom.web.client.services.influence.SoComInfluenceServiceAsync;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class SoComWebEntryPoint implements EntryPoint {
	/**
	 * Create a remote service proxy to talk to the server-side services.
	 */
	private final SocomWebCoreServiceAsync coreService = GWT.create(SocomWebCoreService.class);
	private final SoComInfluenceServiceAsync influenceService = GWT.create(SoComInfluenceService.class);
	private final SoComContentServiceAsync contentService = GWT.create(SoComContentService.class);
	private final SoComGameServiceAsync gameService = GWT.create(SoComGameService.class);
	private final SoComAdministrationServiceAsync adminService = GWT.create(SoComAdministrationService.class);
	private final SoComAchievementServiceAsync achievementService = GWT.create(SoComAchievementService.class);
	private AppController appViewController;
	
	private static  SoComWebEntryPoint instance;

	public SoComWebEntryPoint()
	{
		// as well a hack for backwards compatibility to make the (old) views get access to EntryPoint 
		if (instance == null)  instance = this;
	}
	
	/**
	 * @deprecated Only for backwards compatibility to make the ported Views work"
	 * @return
	 */
	@Deprecated
	public static SoComWebEntryPoint getInstance()
	{
		return instance;
	}
	
	public void onModuleLoad() {
		// called on startup		
		HandlerManager eventBus = new HandlerManager(null);
		ServerCallFactory serverCallFactory = new ServerCallFactory(eventBus); // more state related and config related parameters in future here..
	    this.appViewController = new AppController(eventBus, serverCallFactory);	    
	    
	    appViewController.go();
	}
	
	@Deprecated
	public void setUserID(long userId) {
		this.appViewController.getRequestInformation().setUserID(userId);		
	}
	@Deprecated
	public void setUserIsAdmin(boolean isAdmin) {
		this.appViewController.getRequestInformation().setUserIsAdmin(isAdmin);
	}
	@Deprecated
	public long getUserId() {
		return this.appViewController.getRequestInformation().getUserId();
	}
	@Deprecated
	public boolean isLoggedIn() {
		return this.appViewController.getRequestInformation().isLoggedIn();
	}

	@Deprecated
	public boolean getUserIsAdmin() {
		return this.appViewController.getRequestInformation().getUserIsAdmin();
	}

	@Deprecated
	public String getCurrentPath() {
		return this.appViewController.getRequestInformation().getCurrentPath();
	}

	@Deprecated
	public SocomWebCoreServiceAsync getSocomService() {
		return coreService;
	}

	@Deprecated	
	public SoComInfluenceServiceAsync getInfluenceService() {
		return influenceService;
	}
	@Deprecated
	public SoComContentServiceAsync getContentService() {
		return contentService;
	}
	@Deprecated
	public SoComGameServiceAsync getGameService() {
		return gameService;
	}
	@Deprecated
	public SoComAdministrationServiceAsync getAdminService() {
		return adminService;
	}
	@Deprecated
	public SoComAchievementServiceAsync getAchievementService() {
		return achievementService;
	}
}
