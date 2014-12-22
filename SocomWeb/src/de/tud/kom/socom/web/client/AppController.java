package de.tud.kom.socom.web.client;


import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

import de.tud.kom.socom.web.client.achievements.AchievementPresenter;
import de.tud.kom.socom.web.client.administration.AdministrationPresenter;
import de.tud.kom.socom.web.client.baseelements.AbstractMainPresenter;
import de.tud.kom.socom.web.client.baseelements.MainPresenter;
import de.tud.kom.socom.web.client.baseelements.Presenter;
import de.tud.kom.socom.web.client.content.ContentPresenter;
import de.tud.kom.socom.web.client.eventhandler.CommunicationFailureEventHandler;
import de.tud.kom.socom.web.client.events.CommunicationFailureEvent;
import de.tud.kom.socom.web.client.events.GameChangeEvent;
import de.tud.kom.socom.web.client.events.ViewChangePresenterEvent;
import de.tud.kom.socom.web.client.events.ViewChangeWithinPresenterEvent;
import de.tud.kom.socom.web.client.games.GamesPresenter;
import de.tud.kom.socom.web.client.influence.InfluenceListPresenter;
import de.tud.kom.socom.web.client.influence.InfluencePresenter;
import de.tud.kom.socom.web.client.login.LoginManager;
import de.tud.kom.socom.web.client.login.LoginOAuthWindowPresenter;
import de.tud.kom.socom.web.client.login.LoginPresenter;
import de.tud.kom.socom.web.client.navigation.NavigationPresenter;
import de.tud.kom.socom.web.client.profile.ProfilePresenter;
import de.tud.kom.socom.web.client.util.RequestInformation;
import de.tud.kom.socom.web.client.util.notfound.PageNotFoundPresenter;

/** central class that provides the access to Handlers and  Manager instances to callers
 * 
 * @author jkonert
 *
 */
public class AppController implements ValueChangeHandler<String>, CommunicationFailureEventHandler {
	
	public static final boolean DEBUG = true;

	/** used for binding Presenters/Views to RootPanel IDs **/
	protected enum RootIDs 
	{
		// ids of "helper" elements for navigation etc. (not to be set by Presenters normally) 
		navigation,
		login,
		breadcrumb,
		footerContent1,
		footerContent2,
		footerContent3,
		
		// IDs of main interest for Presenters to set content to via AppController.setPageElement(..)
		headerText,
		headerLogo,
		teaserImage,
		main		
	}
	
	/** these are the parts of the page/template that can be replaced, etc. by Presenters via AppController **/
	public enum PageElementIDs
	{
		breadcrumb(RootIDs.breadcrumb),
		headerText(RootIDs.headerText),
		headerLogo(RootIDs.headerLogo),
		teaserImage(RootIDs.teaserImage);
		
		private RootIDs rootID;

		PageElementIDs(RootIDs id)
		{
			this.rootID = id;
		}
		
		RootIDs getRootID()
		{
			return this.rootID;
		}
	}
	
	/** used for Browser History event handling and parameters to/from AppController **/
	public enum Presenters
	{	
		games,
		admin,
		networklogin,
		content,
		influences, // make sure to always sort this list by having Presenters prefix-matches being "later" than the longer ones before...eg. influences before influence!
		influence,		
		achievements,
		profiles, 
		pagenotfound;

		/** tries to find the AppParts value the given value starts with. If not found defaultValue as given is returned
		 * 
		 * @param value  the String value to check for prefix of one AppParts ID
		 * @param defaultValue to return in case value does not contain any AppPArts value as prefix
		 * @return the found AppParts or the default as given
		 */
		public static Presenters findAppPart(String value, Presenters defaultValue) {
			if (value == null || value.equals("")) return defaultValue;
			for (Presenters ap: Presenters.values())
			{
				if (value.startsWith(ap.name())) return ap;
			}
			return Presenters.pagenotfound;
		}	
	}
	
	//if it should easily be used the game part which was used before it can be identified with a dot instead
	public static final String GAME_PART_USE_PREVIOUS = ".";
	public static final String GAME_PART_DEFAULT = "all";
	private static final Presenters appPartsDefault = Presenters.content;
	private static final Presenters appPartsNotFound= Presenters.pagenotfound;
	
	
	/* private members */
	private HandlerManager eventHandler;
	private ServerCallFactory serverCallFactory;
	private LoginManager loginManager;

	private RequestInformation requestInformation; // replaced with new instance each time a new website call / reload is setup.

	private Presenter currentPresenter;
	private HistoryToken currentHistoryValue;


	public AppController(HandlerManager eventBus, ServerCallFactory serverCallFactory) 
	{
		this.eventHandler = eventBus;
		this.serverCallFactory = serverCallFactory;
		this.eventHandler.addHandler(CommunicationFailureEvent.TYPE, this);
		this.requestInformation = new RequestInformation();
		this.loginManager = new LoginManager(this);
		//this.accessRightsManager = new AccessRightsManager(rpcSocomService);
		HistoryManager.addValueChangeHandler(this);
	}

	/* getter and setter */
	
	
	public RequestInformation getRequestInformation() {
		return requestInformation;
	}
	
	public HandlerManager getEventHandler()
	{
		return eventHandler;
	}
	
	/** a convenience object providing access to key/value pairs and substring parameters of HistoryToken parameters
	 *  of pattern #AppPart/Module/Action/parameter1/parameter2/key1=value1|key2=value2|key3=value3/parameter3/...
	 *  
	 *  If you want to change and fire to a new History state, use HistoryManager instead!
	 * @return
	 */
	public HistoryToken getCurrentHistoryToken()
	{
		return currentHistoryValue;
	}
	
	public ServerCallFactory getRPCFactory()
	{
		return serverCallFactory;
	}
	
	public LoginManager getLoginManager()
	{
		return loginManager;
	}
	
	
	/** call this with an ID (a slot in the HTML page identified by a RootID) and the Widget to set there
	 * The widget is only set, if not already a child of the RootPanel with the RootID.
	 * widget can be null, then the PageElement with the given RootID is cleared of ALL childs!
	 * 	 * 
	 * @param widget
	 */
	@Deprecated private void setPageElement(RootIDs rootID, Widget widget)
	{
		RootPanel p = getRootPanel(rootID);
		if (widget == null)
		{
			p.clear();
			return;
		}
		if (p.getWidgetIndex(widget) < 0)
		{
			p.clear();
			p.add(widget);
		}
	}
	
	/** returns the first (and only?) Widget that is in this RootPanel with the given ID  or null, if not Widget found
	 * 
	 * @param rootID
	 * @return
	 */
	@Deprecated private Widget getPageElementWidget(RootIDs rootID)
	{
		RootPanel p = getRootPanel(rootID);
		if (p.getWidgetCount() > 0)
		{
			return p.getWidget(0);  // current this implementation only supports ONE widget to be child....
		}
		return null;
		
	}
	
	/** returns all key=value&key=value parts of the url  (not decoded)
	 * In case of no parameters an empty String is returned
	 * 
	 * This method is to prevent direct calls to Location.getHref()
	 * due to encapsulation and testing reasons
	 * @return
	 */
	public String getLocationParameterString() {
		
		String full = Location.getHref();
		int i = full.indexOf("?");
		if (i >= 0) return full.substring(i+1);
		return "";
		
	}

	/** simply returns the Location parameter value for the given key. 
	 * This method is to prevent direct calls to Location.getParameter() 
	 * due to encapsulation and testing reasons
	 * 
	 * @param paramName
	 * @return
	 */
	public String getLocationParameter(String paramName) {
		return Location.getParameter(paramName);
	}

	public PageElement getPageElement(PageElementIDs elementID)
	{
		return PageElement.getInstance(this, elementID, getRootPanel(elementID.getRootID()));
	}
	public void setPageElement(PageElement pageElement, IsWidget viewOrWirdget)
	{
		
	}

	/** Same as getRootPanel(RootIDs ID), but removes all DOM-based html childs of element.
	 * Useful for removing all static template based childs of an element
	 * 
	 * @param ID
	 * @return
	 */
	public static RootPanel getClearRootPanel(RootIDs ID)
	{
		RootPanel panel = getRootPanel(ID);			
		if (panel == null) return panel;
		
		while(panel.getWidgetCount() > 0)
		{
			panel.remove(0);
		}
		// DOM based removal of (static template) elements...
		Element e = panel.getElement();
		while(e.hasChildNodes())
		{					
			e.removeChild(e.getFirstChild());
		}
		return panel;
	}

	/** simply calls RootPanel.get() with String of the given ID
	 * 
	 * @param ID  RootID enum identifier used as # id element identifier
	 * @return
	 */
	private static RootPanel getRootPanel(RootIDs ID) {
		return RootPanel.get(ID.toString());	
	}

	private MainPresenter findOrCreatePresenter(Presenters appPart) {				
		
		// This could maybe be done more elegant with a Map associating each one with the instance
		MainPresenter p = null;
		
		switch (appPart)
		{ // A-Z order
		case games:
			p = GamesPresenter.getInstance(this);
			break;
		case achievements:
			p = AchievementPresenter.getInstance(this);
			break;
		case admin:
			p = AdministrationPresenter.getInstance(this);
			break;
		case content:
			p = ContentPresenter.getInstance(this);
			break;
		case influences:
			p = InfluenceListPresenter.getInstance(this);  
			break;
		case influence:
			p = InfluencePresenter.getInstance(this);  
			break;			
		case networklogin:
			p = LoginOAuthWindowPresenter.getInstance(this);
			break;
		case profiles:
			p = ProfilePresenter.getInstance(this);
			break;
		case pagenotfound:
			p = PageNotFoundPresenter.getInstance(this);
			break;
		default:
			// Logger warning is better than Exception; needed to know that one case is NOT implemented...
			throw new UnsupportedOperationException("Application Part '" + appPart + "' unknown");
		}
	
		if (p == null)
		{
			throw new UnsupportedOperationException("Application Part '" + appPart + "' cannot be loaded");
		}				
		return p;
	}

	private void registerGWTUncaughtExceptionsHander() {
		// copied and edited from http://code.google.com/p/gwt-voices/wiki/GettingStarted#A_More_Elaborate_Example
		GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
		      public void onUncaughtException(Throwable throwable) {
		        String text = "Uncaught exception: ";
		        while (throwable != null) {
		          StackTraceElement[] stackTraceElements = throwable.getStackTrace();
		          text += throwable.toString() + "\n";
		          for (int i = 0; i < stackTraceElements.length; i++) {
		            text += "    at " + stackTraceElements[i] + "\n";
		          }
		          throwable = throwable.getCause();
		          if (throwable != null) {
		            text += "Caused by: ";
		          }
		        }
//		        DialogBox dialogBox = new DialogBox(true, false);
//		        //DOM.setStyleAttribute(dialogBox.getElement(), "backgroundColor", "#ABCDEF");
//		        System.err.print(text);
//		        text = text.replaceAll(" ", "&nbsp;");
//		        dialogBox.setHTML("<pre>" + text + "</pre>");
//		        dialogBox.center();
		      }
		    });
		
	}

	/**
	 * This is the MAIN method on top-level that actually starts glueing everything together (called by EntryPoint).
	 * It calls every component to render/add the widgets (view) to the responding RootPanel parts
	 * @param rootPanel
	 */
	public void go() {
		try
		{
			// Register a Handler for JS Exceptions
			registerGWTUncaughtExceptionsHander();

			// a new refresh of website means a new Request object
			this.requestInformation = new RequestInformation();
			
			// only handle an explicit go if application is called fresh without
			// any state. otherwise wait/listen to History events
			
			// do the basic initialization parts:
			
			// 1. check userState
			loginManager.checkIfLoggedIn(requestInformation);
			
			// 2. get for each anchor in the template the RootPanel and call the appropriate components to render into		
			NavigationPresenter navBar = NavigationPresenter.getInstance(this);
			navBar.go(getClearRootPanel(RootIDs.navigation));
			
			// 3. Login Field
			LoginPresenter loginPanel = LoginPresenter.getInstance(this);
			loginPanel.go(getClearRootPanel(RootIDs.login));
			
			// 3. Footer
			// TODO	JK: add footer support (JK)
			
			// try to find out which "mode" of page request this is:
			// 1. SocialMediaApplication redirect call (maybe in a popup?) to
			// process token
			// 2. normal page call
	
			String stateParameter = getLocationParameter(getLoginManager().getNetworkLoginManager().getURLParameterForOAuthTokenProcessing());
			boolean isNetworkLogin = stateParameter != null && stateParameter.startsWith(getLoginManager().getNetworkLoginManager().getURLParameterValueForOAuthTokenProcessing());
			if (isNetworkLogin)
			{
				String gamePart = stateParameter.substring(stateParameter.lastIndexOf('.') + 1);
				//do not remove game-part
				HistoryToken token = new HistoryToken(gamePart, Presenters.networklogin);
				HistoryManager.newItem(token);
			}
			else
			{
				if (HistoryManager.isTokenEmpty())
				{
					Presenters ap = appPartsDefault;			
					HistoryManager.newItem(new HistoryToken(GAME_PART_DEFAULT, ap));
				}	
			}
			HistoryManager.fireCurrentHistoryState();
		} catch (Exception e)
		{
			@SuppressWarnings("unused")
			Exception f = e;  // only for breakpoint reasons....  int i =1;
		}
	}

	/* implementation of EventHandler methods **/
	@Override
	public void onCommunicationFailureEvent(CommunicationFailureEvent event) {
		Window.alert("Communication Failure\n"+event.getException());
		
	}

	/** the main method called each time a new side page or page part is triggered by history. 
	 *  This is as well the case when new page is (re)loaded. so nearly all rendering and putting together happens here. 
	 */
	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		// determine by HistoryToken which presenter should be loaded in main-RootPanel.
		// if this is a fresh page build, clear out all dummy content from template and add Presenter
		// otherwise simply exchange the presenters (old out, new in)..
		try
		{
			HistoryToken oldHistoryValue = currentHistoryValue;
			HistoryToken newHistoryValue = HistoryToken.fromValueChangeEvent(event);
			
			//the presented game changed in the meantime
			if(currentHistoryValue == null || !currentHistoryValue.getGamePart().equals(newHistoryValue.getGamePart())) {
				//apply game settings
				if(!applyGame(newHistoryValue.getGamePart()))
					return;
			}

			HistoryManager.newItem(newHistoryValue, false);
			currentHistoryValue = newHistoryValue;
			Presenters presenterPart = newHistoryValue.getPresenter(appPartsNotFound); // the state of app to build; contains values of AppParts enum		
			
			// 1. first find AppPart and ask the instance which components of page not to load...
			MainPresenter presenter = findOrCreatePresenter(presenterPart);
			
			// 1.b Set the Title
			Document d = Document.get();
			String titleToBe = presenter.getPageTitle(newHistoryValue);
			if (d!= null && titleToBe != null && !titleToBe.equals(AbstractMainPresenter.DEFAULT_TITLE) && !titleToBe.equals(""))
			{
				d.setTitle(titleToBe);
			}
			
			// 2. check if any change is needed
			if (presenter.equals(currentPresenter))
			{
				if (!newHistoryValue.equals(oldHistoryValue) && !oldHistoryValue.equals(null))
				{
					eventHandler.fireEvent(new ViewChangeWithinPresenterEvent(presenter, oldHistoryValue, newHistoryValue));
				}			
				// do nothing?
				return;
			}
			
			// 3. inform old presenter to cleanup
			if (currentPresenter!= null)
			{
				eventHandler.fireEvent(new ViewChangePresenterEvent(currentPresenter, presenter));
			}
			
			// thanks to single threaded JavaScript we do not need to wait for any callback and exchange directly
			RootPanel mainPanel = requestInformation.isCompletePageBuildMode()?getClearRootPanel(RootIDs.main):getRootPanel(RootIDs.main);
			mainPanel.clear();
			presenter.go(mainPanel);
			currentPresenter = presenter;
			
			//  Main content area...handing over RootPanels for Subheader, Teaser-Image, Breadcrumb and content  to AppPart
			
		
			this.requestInformation.endCompletePageBuildMode();
		}
		catch (Throwable e)
		{
			Window.alert("Top-Level catch: " + e);
			GWT.log("Top-Level catch", e);
		}
	}

	private boolean applyGame(String gamePart) {
		
		if(gamePart.equals(GAME_PART_USE_PREVIOUS)) {
			/* overwrite dot with previous game	 */
			if(requestInformation.getCurrentGame() != null){
				gamePart = requestInformation.getCurrentGame();
				HistoryToken historyToken = HistoryManager.getHistoryToken();
				historyToken.setGamePart(gamePart, false);
				HistoryManager.newItem(historyToken, true);
				return false;
			}
			else {
				//previous not set - use default
				gamePart = GAME_PART_DEFAULT;
			}
		}
		
//		if(gamePart.equals(GAME_PART_DEFAULT)) {
//			requestInformation.setCurrentGame(gamePart);
//			getEventHandler().fireEvent(new GameChangeEvent(gamePart));
//			return true;
//		}
		
		//TODO template change
		getEventHandler().fireEvent(new GameChangeEvent(gamePart));
		requestInformation.setCurrentGame(gamePart);
		
		/*
		 * removed async checking of gamepart since it can be changed by the user either way
		 */
//		final String gameIdent = gamePart;
//		//update current game and validate
//		getRPCFactory().getGameService().isGameValid(gameIdent, new AsyncCallback<Boolean>() {
//			
//			@Override
//			public void onSuccess(Boolean valid) {
//				if(!valid) {
//					//if game string was not valid change to default and retry
//					HistoryToken historyToken = HistoryManager.getHistoryToken();
//					historyToken.setGamePart(GAME_PART_DEFAULT, false);
//					HistoryManager.newItem(historyToken, true);
//					requestInformation.setCurrentGame(GAME_PART_DEFAULT);
//				} else {
//					getEventHandler().fireEvent(new GameChangeEvent(gameIdent));
//				}
//			}
//			
//			@Override
//			public void onFailure(Throwable caught) {
//				Window.alert("Communication failure: " + caught.getLocalizedMessage());
//				requestInformation.setCurrentGame(GAME_PART_DEFAULT);
//			}
//		});
		return true;
	}
}
