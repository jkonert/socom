package de.tud.kom.socom.web.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;

import de.tud.kom.socom.web.client.services.administration.SoComAdministrationService;
import de.tud.kom.socom.web.client.services.administration.SoComAdministrationServiceAsync;
import de.tud.kom.socom.web.client.services.core.SocomWebCoreService;
import de.tud.kom.socom.web.client.services.core.SocomWebCoreServiceAsync;
import de.tud.kom.socom.web.client.services.game.SoComGameService;
import de.tud.kom.socom.web.client.services.game.SoComGameServiceAsync;
import de.tud.kom.socom.web.client.services.influence.SoComInfluenceService;
import de.tud.kom.socom.web.client.services.influence.SoComInfluenceServiceAsync;
import de.tud.kom.socom.web.client.services.login.SoComLoginService;
import de.tud.kom.socom.web.client.services.login.SoComLoginServiceAsync;
import de.tud.kom.socom.web.client.services.reporting.SoComReportingService;
import de.tud.kom.socom.web.client.services.reporting.SoComReportingServiceAsync;
import de.tud.kom.socom.web.client.services.social.fb.SocialNetworkService;
import de.tud.kom.socom.web.client.services.social.fb.SocialNetworkServiceAsync;
import de.tud.kom.socom.web.client.services.statistic.SoComStatisticService;
import de.tud.kom.socom.web.client.services.statistic.SoComStatisticServiceAsync;

/** this factory is instanciated by EntryPoint/AppController and gets the needed config to provide to all components centrally the 
 * needed Async CallbackService classes.
 * @author jkonert
 *
 */
public class ServerCallFactory {

	private HandlerManager eventHandler;
	private SocomWebCoreServiceAsync coreService;
	private SoComGameServiceAsync gameService;
	private SoComInfluenceServiceAsync influenceService;
	private SocialNetworkServiceAsync socialNetworkingService;
	private SoComAdministrationServiceAsync adminService;
	private SoComLoginServiceAsync loginService;
	private SoComStatisticServiceAsync statsService;
	private SoComReportingServiceAsync reportService;

	protected ServerCallFactory(HandlerManager eventHandler)
	{
		this.eventHandler = eventHandler;
	}
	
	/** the GWT Async Call Service to send/receive from Server (SocomCore) **/
	public SocomWebCoreServiceAsync getCoreService()
	{
		if (coreService == null) coreService = GWT.create(SocomWebCoreService.class);
		return coreService;
	}
	
	public SoComInfluenceServiceAsync getInfluenceService()
	{
		if(influenceService == null) influenceService = GWT.create(SoComInfluenceService.class);
		return influenceService;
	}
	
	public SocialNetworkServiceAsync getSocialNetworkService() 
	{
		if(socialNetworkingService == null) socialNetworkingService = GWT.create(SocialNetworkService.class);
		return socialNetworkingService;
	}

	public SoComAdministrationServiceAsync getAdministrationService() 
	{
		if(adminService == null) adminService = GWT.create(SoComAdministrationService.class);
		return adminService;
	}
	
	public SoComLoginServiceAsync getLoginService() 
	{
		if(loginService == null) loginService = GWT.create(SoComLoginService.class);
		return loginService;
	}
	
	public SoComStatisticServiceAsync getStatisticService() 
	{
		if(statsService == null) statsService = GWT.create(SoComStatisticService.class);
		return statsService;
	}
	
	public SoComGameServiceAsync getGameService() 
	{
		if(gameService == null) gameService = GWT.create(SoComGameService.class);
		return gameService;
	}

	public SoComReportingServiceAsync getReportingService() {
		if(reportService == null) reportService = GWT.create(SoComReportingService.class);
		return reportService;
	}

	/// more such method to come here...
}
