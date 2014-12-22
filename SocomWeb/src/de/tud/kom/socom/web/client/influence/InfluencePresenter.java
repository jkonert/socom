package de.tud.kom.socom.web.client.influence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.json.client.JSONException;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

import de.tud.kom.socom.web.client.AppController;
import de.tud.kom.socom.web.client.AppController.PageElementIDs;
import de.tud.kom.socom.web.client.administration.itemadministration.ViewWithItemAdministration;
import de.tud.kom.socom.web.client.baseelements.AbstractMainPresenter;
import de.tud.kom.socom.web.client.baseelements.MainPresenter;
import de.tud.kom.socom.web.client.baseelements.ViewInterface;
import de.tud.kom.socom.web.client.baseelements.ViewWithErrorsInterface;
import de.tud.kom.socom.web.client.baseelements.viewerrors.ErrorCodeUnknownTypeView;
import de.tud.kom.socom.web.client.baseelements.viewerrors.ErrorIllegalDurationNumberView;
import de.tud.kom.socom.web.client.baseelements.viewerrors.ErrorLoginNeededView;
import de.tud.kom.socom.web.client.baseelements.viewerrors.ErrorNotYetStartedView;
import de.tud.kom.socom.web.client.baseelements.viewerrors.ErrorSelectionRestrictionView;
import de.tud.kom.socom.web.client.baseelements.viewerrors.ErrorServerUnknownErrorView;
import de.tud.kom.socom.web.client.baseelements.viewerrors.ErrorView;
import de.tud.kom.socom.web.client.baseelements.viewerrors.ErrorViewFactory;
import de.tud.kom.socom.web.client.baseelements.viewerrors.ErrorVisibilityViolationView;
import de.tud.kom.socom.web.client.baseelements.viewinfos.InfoInfluenceAlreadyFinishedView;
import de.tud.kom.socom.web.client.eventhandler.FormSubmitCompleteEventHandler;
import de.tud.kom.socom.web.client.eventhandler.FormSubmitEventHandler;
import de.tud.kom.socom.web.client.eventhandler.FormValueChangeEventHandler;
import de.tud.kom.socom.web.client.eventhandler.HasFormSubmitCompleteHandlers;
import de.tud.kom.socom.web.client.eventhandler.HasFormSubmitHandlers;
import de.tud.kom.socom.web.client.eventhandler.HasFormValueChangeHandlers;
import de.tud.kom.socom.web.client.eventhandler.LoginEventHandler;
import de.tud.kom.socom.web.client.events.FormSubmitCompleteEvent;
import de.tud.kom.socom.web.client.events.FormSubmitEvent;
import de.tud.kom.socom.web.client.events.FormValueChangeEvent;
import de.tud.kom.socom.web.client.events.LoginErrorNetworkUserNotFoundEvent;
import de.tud.kom.socom.web.client.events.LoginErrorWrongUserIDPasswortEvent;
import de.tud.kom.socom.web.client.events.LoginEvent;
import de.tud.kom.socom.web.client.events.LogoutEvent;
import de.tud.kom.socom.web.client.events.FormValueChangeEvent.ChangeType;
import de.tud.kom.socom.web.client.influence.image.AnswerImage;
import de.tud.kom.socom.web.client.influence.image.InfluenceAnswerImageView;
import de.tud.kom.socom.web.client.influence.strategies.AudioInfluenceStrategy;
import de.tud.kom.socom.web.client.influence.strategies.ImageInfluenceStrategy;
import de.tud.kom.socom.web.client.influence.strategies.InfluenceStrategy;
import de.tud.kom.socom.web.client.influence.strategies.TextInfluenceStrategy;
import de.tud.kom.socom.web.client.reporting.ViewWithItemReporting;
import de.tud.kom.socom.web.client.services.influence.SoComInfluenceServiceAsync;
import de.tud.kom.socom.web.client.sharedmodels.Influence;
import de.tud.kom.socom.web.client.sharedmodels.InfluenceAnswer;
import de.tud.kom.socom.web.client.sharedmodels.SimpleEntry;
import de.tud.kom.socom.web.client.util.FileUtils.FileState;
import de.tud.kom.socom.web.client.util.exceptions.NotVisibleException;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class InfluencePresenter extends AbstractMainPresenter implements MainPresenter, LoginEventHandler,
FormValueChangeEventHandler, FormSubmitEventHandler, FormSubmitCompleteEventHandler
{

	private static final String INFLUENCE_COOKIE = "influences";

	public interface InfluenceViewInterface extends ViewWithErrorsInterface {
		public void showCountdown(Date timeout);

		public void setQuestion(String question);
		
		public void setAnswerColumns(int maxcolumns);

		public <T extends Object> void setPossibleAnswers(List<InfluenceAnswerViewInterface<T>> answers);

		public <T extends Object> List<InfluenceAnswerViewInterface<T>> getSelectedAnswers();

		public void disableSubmit();

		public void enableSubmit();

		public void showSubmit(boolean show);

		public void setAdministrationVisible(boolean visible);

		public <T extends Object> void appendAnswer(InfluenceAnswerViewInterface<T> freeAnswerView);

		public void replaceAnswers(InfluenceFreeAnswerViewInterface<AnswerImage> oldAnswerView,
				InfluenceAnswerImageView newAnswerView, boolean appendOld, boolean selected);

	}

	public interface InfluenceAnswerViewInterface<T> extends ViewInterface, HasFormValueChangeHandlers,
	ViewWithItemAdministration, ViewWithItemReporting {
		public void setAnswer(T answer);

		public void setFromUser(long userId, String userName, Date created);

		/** this calls switchResultView(true) as well automatically */
		public void setResultInformation(int totalnumberOfVotes, int numberOfVotes, boolean isWinner);

		public boolean isSelected();

		public <H extends EventHandler> HandlerRegistration addHandler(final H handler, GwtEvent.Type<H> type);

		/** toggles View to show the Resultview or Vote View */
		void switchResultView(boolean showResultView);

		/**
		 * returns the current state of loading/displaying the answer. mostly
		 * useful if a binary file is type of Answer which may be not found or
		 * still loading etc.
		 */
		FileState getReadyState();

		public void setVotable(boolean votable);

		public void setSelected(boolean selected);
		
		public void animate();
	}

	public interface InfluenceFreeAnswerViewInterface<T> extends InfluenceAnswerViewInterface<T>,
	HasFormSubmitHandlers, HasFormSubmitCompleteHandlers {
		public T getFreeAnswerValue();

		public void disableAddAnswer();
	}

	public interface InfluenceFreeAnswerWithFileViewInterface<T> extends InfluenceFreeAnswerViewInterface<T>
	{
		/**
		 * if the InfluenceFreeAnswer has an associated file it uploads the file
		 * via form. Caller can register an FormSubmitHandler and/or
		 * FormSubmitCompleteEventHandler to be informed of
		 * started/finished/ended uploads
		 * 
		 * @param action
		 *            the URL to post the file to
		 * @param hiddenParameters
		 *            (optional) can be NULL. if set, each key=value pair is
		 *            submitted as hidden parameters with form on submit
		 * @param type 
		 * 				change type to distinguish between file and file_drag if necessary
		 */
		public void submitFile(SafeUri action, SimpleEntry<String, String>... hiddenParameters);
		
		public void submitFile(ChangeType type, SafeUri action, SimpleEntry<String, String>... hiddenParameters);

		/**
		 * enables or disables the Submit element for user; submitFile() can
		 * still be called and will submit though
		 */
		public void setSubmitEnabled(boolean enabled);

	}

	/** which types of answers are currently presented and displayed to user */
	// package protected for use with other InfluencePresenters and Views..
	enum MODE
	{
		text, image, audio
	}

	protected static final long PERIOD_MILLIS_FOR_COUNTDOWN_VISIBLE = 10 * 60 * 1000; // 10minutes

	private static InfluencePresenter instance;
	private InfluenceViewInterface view;
	private SoComInfluenceServiceAsync influenceService;
	private InfluenceStrategy strategy;

	private Influence influence;
	public short minOptions = 1;
	public short maxOptions = 1;
	private short currentlySelectedOptions = 0;
	private long influenceId;
	private String externalId;
	private int totalNumberOfVotes = 0;
	private InfluenceAnswer mostVoted;

	private MODE currentMode = MODE.text;
	private ErrorServerUnknownErrorView communiationError;
	private ErrorIllegalDurationNumberView illegalDurationError;
	private ErrorNotYetStartedView notStartedView;
	private InfoInfluenceAlreadyFinishedView timeoutInfo;

	private Date timeout;
	private Timer countDownTimer;
	private Timer answerRefreshTimer;
	public boolean showResults;

	// remember the EventListeners we registered to be able to remove them..
	private Map<InfluenceAnswerViewInterface<?>, HandlerRegistration> valueChangeHandlerRegistered = new HashMap<InfluenceAnswerViewInterface<?>, HandlerRegistration>();
	private Map<InfluenceAnswerViewInterface<?>, HandlerRegistration> formSubmitHandlerRegistered = new HashMap<InfluenceAnswerViewInterface<?>, HandlerRegistration>();
	private Map<InfluenceAnswerViewInterface<?>, HandlerRegistration> formSubmitCompleteHandlerRegistered = new HashMap<InfluenceAnswerViewInterface<?>, HandlerRegistration>();


	private InfluencePresenter(AppController appController)
	{
		super(appController);
		init();
	}

	public static InfluencePresenter getInstance(AppController appController)
	{
		if (instance == null) instance = new InfluencePresenter(appController);
		return instance;
	}

	@Override
	public void init()
	{
		this.view = new InfluenceView(this);
		this.setView(view);
		this.influenceService = getAppController().getRPCFactory().getInfluenceService();
		this.communiationError = new ErrorServerUnknownErrorView();
		this.illegalDurationError = new ErrorIllegalDurationNumberView();
		this.notStartedView = new ErrorNotYetStartedView();
		this.timeoutInfo = new InfoInfluenceAlreadyFinishedView();
		this.getAppController().getEventHandler().addHandler(LoginEvent.TYPE, this);
	}

	@Override
	public void go(RootPanel targetPanel)
	{
		setTargetPanel(targetPanel);

		final String influenceId = getAppController().getCurrentHistoryToken().getPresenterModule();

		// fade out the header
		getAppController().getPageElement(PageElementIDs.teaserImage).hide();
		getAppController().getPageElement(PageElementIDs.headerText).setContent("Nimm teil an der Abstimmung und beinflusse das Spiel!");

		// fetch the influence with its answers
		String sid = getAppController().getLoginManager().getSessionID();
		this.influenceService.getInfluence(sid, influenceId, true, new AsyncCallback<Influence>()
		{
			@Override
			public void onFailure(Throwable caught)
			{/* show error */
				if (caught instanceof NotVisibleException)
				InfluencePresenter.this.view.showError(new ErrorVisibilityViolationView());
				else if (caught instanceof NullPointerException)
				InfluencePresenter.this.view.showError(new ErrorServerUnknownErrorView());
			}

			@Override
			public void onSuccess(Influence result)
			{
				if (result == null)
					onFailure(new NullPointerException("Influence: " + influenceId + " not found."));
				else
					applyInfluence(result);
			}
		});
	}

	protected void applyInfluence(Influence infl)
	{		
		// save essential information
		this.influence = infl;
		this.influenceId = infl.getId();
		this.externalId = infl.getExternalId();
		this.minOptions = infl.getMinChoices();
		this.maxOptions = infl.getMaxChoices();
		this.timeout = infl.getTimeout();
		// if is finished, OR not yet started but is admin
		this.showResults = determineTimeoutState() > 0 || (determineTimeoutState() < 0 && userIsAdmin());
		this.showResults = this.showResults || alreadyVisited(infl);
		this.view.showSubmit(!showResults);
		if (this.showResults)
		{
			this.totalNumberOfVotes = infl.getTotalNumberOfGivenVotes();
			this.mostVoted = infl.getWinner();
		}
		this.currentMode = initializeMode(infl.getType());
		if (currentMode == null) // if mode is not supported
		{
			showError(new ErrorCodeUnknownTypeView(infl.getType()));
			return;
		}
		this.setView(view);
		// set question and answers information
		this.view.setQuestion(infl.getQuestion());
		
		showAnswers(infl);
	}

	private void showAnswers(Influence influence)
	{
		addPredefinedAnswers(influence.getPredefinedAnswers());
		if (influence.allowFreeAnswers())
		{
			if (influence.freeAnswersVotable() || showResults || userIsAdmin())
			{
				addFreeAnswers(influence.getFreeAnswers(), influence.getFreeAnswersVotable());
			}
			if (!InfluencePresenter.this.showResults) {
				//if free answers & not results
				scheduleAnswerRefreshmentPolling();
				if(isLoggedIn())
					addFreeAnswerForm();
			}
		}
		preventNoAnswers(influence);
		paintAnswers();
	}

	private void preventNoAnswers(Influence infl)
	{
		if (!showResults && !this.isLoggedIn() && infl.allowFreeAnswers() && infl.getPredefinedAnswers().size() == 0
			&& (infl.freeAnswersVotable() ? infl.getFreeAnswers().size() == 0 : true))
		{
			// in case we have nothing to show..
			showError(new ErrorLoginNeededView());
		}
	}

	public void paintAnswers()
	{
		// load answers to view
		providePossibleAnswers();
		initChangeHandler();
	}
	
	public void lazyPaintAdditionalAnswers(List<InfluenceAnswer> newAnswers) {
		//paint answers which came from refreshment additionally to the already shown answers which
		//should result in NOT touching the other views (especially the already typed content)
		boolean votable = influence.freeAnswersVotable();
		for(InfluenceAnswer a : newAnswers) {
			InfluenceAnswerViewInterface freeAnswerView = strategy.addFreeAnswer(a, votable);
			this.view.appendAnswer(freeAnswerView);
		}
		if (userIsAdmin() || showResults) strategy.appendItemAdministration(getUserId(), userIsAdmin());
		strategy.appendItemReporting(influence.getExternalId(), getUserId());
	}
	

	public void replaceAnswerViews(InfluenceFreeAnswerViewInterface<AnswerImage> oldAnswerView,
			InfluenceAnswerImageView newAnswerView, boolean appendOld, boolean selected) {
		 this.view.replaceAnswers(oldAnswerView, newAnswerView, appendOld, selected);
	}
	
	/**
	 * shows whether the currently logged in users is attendent of this
	 * influence or if no user is logged if a correspondent cookie-value can be
	 * found
	 */
	private boolean alreadyVisited(Influence influence)
	{
		if (isLoggedIn())
		{
			// look for attendees (for multiple users using one pc)
			return influence.isAttendee(getUserId());
		}
		else
		{
			// otherwise check cookies..
			String cookies = Cookies.getCookie(INFLUENCE_COOKIE);
			if (cookies == null) return false;
			String[] visited = cookies.split(",");
			for (String visitedIds : visited)
			{
				if (visitedIds.equals(String.valueOf(this.influenceId)))
				{
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * determine whether the influence is started and if its still running
	 * 
	 * @return -1 iff not yet started, 0 iff actually runnning, 1 iff in the
	 *         past (timed out)
	 */
	private byte determineTimeoutState()
	{
		if (this.timeout == null)
		{
			return 0;
		}
		else if (this.timeout.getTime() == 0){
			this.view.showError(notStartedView);
			return -1;
		}
		else if (this.timeout.after(new Date()))
		{// user still has some time
			scheduleCountDownTimer();
			// show the form.
			return 0;
		}
		else
		{ // time is over. show results and no form.
			this.view.showError(timeoutInfo);
			return 1;
		}
	}

	private MODE initializeMode(String type)
	{
		MODE foundMode = MODE.valueOf(type);
		if (foundMode == null) return null;
		// the not-so-nice determination of the type
		switch (foundMode)
		{
		case text:
			strategy = new TextInfluenceStrategy(getAppController(), this);
			break;
		case image:
			strategy = new ImageInfluenceStrategy(getAppController(), this);
			this.view.setAnswerColumns(3); // change to three answer in a column for images
			break;
		case audio:
			strategy = new AudioInfluenceStrategy(getAppController(), this);
			break;
		}
		return foundMode;
	}

	private void addPredefinedAnswers(List<InfluenceAnswer> predefinedAnswers)
	{
		for (InfluenceAnswer answer : predefinedAnswers)
		{
			strategy.addPredefinedAnswer(answer);
		}
	}

	private void addFreeAnswers(List<InfluenceAnswer> freeAnswers, boolean votable)
	{
		for (InfluenceAnswer answer : freeAnswers)
		{
			strategy.addFreeAnswer(answer, votable);
		}
	}

	private void addFreeAnswerForm()
	{
		strategy.addFreeAnswerForm();
	}

	private void providePossibleAnswers()
	{
		List<InfluenceAnswerViewInterface<Object>> possibleAnswers = 
				(List<InfluenceAnswerViewInterface<Object>>) strategy.getPossibleAnswers();
		if (this.showResults)
		{
			// FIXME: what to do with deleted answers and their votings? (if set
			// to 0, also in administration mode?)
			sortResultList(possibleAnswers);
		}
		else
		{
			shuffleResultList(possibleAnswers);
		}
		if (userIsAdmin() || showResults) strategy.appendItemAdministration(getUserId(), userIsAdmin());
		strategy.appendItemReporting(influence.getExternalId(), getUserId());
		view.setAdministrationVisible(userIsAdmin());
		view.setPossibleAnswers(possibleAnswers);
	}

	private void sortResultList(List<InfluenceAnswerViewInterface<Object>> possibleAnswers)
	{
		final Map<InfluenceAnswerViewInterface<Object>, InfluenceAnswer> answerMap = this.strategy.getAnswerMap();
		Collections.sort(possibleAnswers, new Comparator<InfluenceAnswerViewInterface>()
		{
			@Override
			public int compare(InfluenceAnswerViewInterface o1, InfluenceAnswerViewInterface o2)
			{
				if (o1 == null || o2 == null || o1.equals(o2) || !answerMap.containsKey(o1)
					|| !answerMap.containsKey(o2))
				return 0;
				else
				return answerMap.get(o2).getResult().getVotes() - answerMap.get(o1).getResult().getVotes();
			}
		});
	}

	private void shuffleResultList(List<InfluenceAnswerViewInterface<Object>> possibleAnswers)
	{
		if (possibleAnswers.isEmpty()) return;
		InfluenceAnswerViewInterface<Object> freeFormField = possibleAnswers.get(possibleAnswers.size() - 1);
		boolean hasFreeFormField = freeFormField instanceof InfluenceFreeAnswerWithFileViewInterface
									|| freeFormField instanceof InfluenceFreeAnswerViewInterface;
		if (hasFreeFormField) possibleAnswers.remove(freeFormField);

		shuffleList(possibleAnswers);

		if (hasFreeFormField) possibleAnswers.add(freeFormField);
	}

	private void shuffleList(List<InfluenceAnswerViewInterface<Object>> list)
	{
		int n = list.size();
		for (int i = 0; i < n; i++)
		{
			int change = i + Random.nextInt(n - i);
			InfluenceAnswerViewInterface<Object> helper = list.get(i);
			list.set(i, list.get(change));
			list.set(change, helper);
		}
	}

	private void initChangeHandler()
	{
		List<InfluenceAnswerViewInterface<?>> answerList = getAnswerList();
		for (InfluenceAnswerViewInterface<?> a : answerList)
		{
			if (!this.valueChangeHandlerRegistered.containsKey(a))
			{// only register handlers if not already done
				this.valueChangeHandlerRegistered.put(a,
						a.addValueChangeHandler(this));
			}

			if (a instanceof InfluenceFreeAnswerViewInterface<?>)
			{// this includes both: InfluenceFreeAnswerViews and InfluenceFreeAnswerWithFileViews
				if (!this.formSubmitHandlerRegistered.containsKey(a))
				{// only register handlers if not already done
					this.formSubmitHandlerRegistered.put(a,((InfluenceFreeAnswerViewInterface<?>) a).addFormSubmitHandler(this));
				}
				if (!this.formSubmitCompleteHandlerRegistered.containsKey(a))
				{// only register handlers if not already done
					this.formSubmitCompleteHandlerRegistered.put(a,
							((InfluenceFreeAnswerViewInterface<?>) a)
									.addFormSubmitCompleteHandler(this));
				}
			}
		}
	}

	private List<InfluenceAnswerViewInterface<?>> getAnswerList()
	{
		return (List<InfluenceAnswerViewInterface<?>>) strategy.getPossibleAnswers();
	}

	private void scheduleCountDownTimer()
	{
		if (countDownTimer != null)
		{
			countDownTimer.cancel();
		}
		else
		{
			countDownTimer = new Timer()
			{
				public void run()
				{
					if (InfluencePresenter.this.timeout == null)
					return; // not yet started..

					Date future = new Date();
					future.setTime(future.getTime() + InfluencePresenter.PERIOD_MILLIS_FOR_COUNTDOWN_VISIBLE);
					if (InfluencePresenter.this.timeout.after(future))
					return;
					if (InfluencePresenter.this.timeout.before(new Date()))
					{
						InfluencePresenter.this.onCountDownTimeout();
						return;
					}
					// show the count down
					InfluencePresenter.this.view.showCountdown(InfluencePresenter.this.timeout);
					// fetch numbers and percent
					// strategy.switchtoResultView(
				}
			};
		}
		// Schedule the timer to run every 250ms
		countDownTimer.scheduleRepeating(250);

	}

	private void onCountDownTimeout()
	{
		this.view.showCountdown(null);
		this.view.disableSubmit();
		this.countDownTimer.cancel();

		InfluencePresenter.this.showResults = true;
		refresh();
		this.hideErrors();
		this.showError(new ErrorNotYetStartedView());
	}

	@Override
	public void onFormValueChangeEvent(FormValueChangeEvent event)
	{
		InfluenceAnswerViewInterface<String> parent = strategy.findParentViewForElement(event.getSourceWidget());
		if (parent == null)
		{
			return;
		}
		switch (event.getChangeType())
		{
		case select:
			if (parent.isSelected()) currentlySelectedOptions++;
			else currentlySelectedOptions--;

			if (currentlySelectedOptions > maxOptions)
			{
				hideErrors();
				showError(new ErrorSelectionRestrictionView(minOptions, maxOptions, currentlySelectedOptions));
				enableSubmit(false);
			}
			else
			{
				hideErrors();
				enableSubmit(true);
			}
			break;

		case message:
			// we have a value change..free answer
			if (parent instanceof InfluenceFreeAnswerViewInterface)
			{
				// maybe update the displayed label if desired..
				// very costly when used for each character..
				strategy.updateFreeAnswerOnChange((InfluenceFreeAnswerViewInterface) parent, event.getValue());
			}
			break;

		case file_drop:
			//switch case statement should go now into the file case..
		case file:
			if (parent instanceof InfluenceFreeAnswerWithFileViewInterface)
			{
				if (!this.isLoggedIn()) return; // should not be able
				String filename = event.getValue();
				if (filename == null || filename.isEmpty())
				{
					Window.alert("Filename is empty or null (InfluencePresenter#623)");
					return;
				}
				if (!this.strategy.hasValidFileEnding(filename))
				{
					Window.alert("Falscher Dateityp! (InfluencePresenter#628)");
					return;
				}
				String host = GWT.getHostPageBaseURL().replaceAll("web/", "");
				// String host = "http://localhost:7999/";
				String uploadUrl = host + "servlet/influence/uploadInfluenceData"; // use resource manager
				String message = event.getMessage()==null?filename:event.getMessage();
				
				SimpleEntry[] parameter = new SimpleEntry[] {
																new SimpleEntry<String, String>("influenceid", this.externalId),
																new SimpleEntry<String, String>("message", message),
																new SimpleEntry<String, String>("uid", String.valueOf(this.getUserId())),
				// FIXME: password not anymore saved on userside, how can we ensure an authentication
				// new SimpleEntry<String, String>("secret",this.getAppController().getRequestInformation().getUserSecret())
				};
				this.strategy.updateFreeAnswerOnChange((InfluenceFreeAnswerViewInterface) parent, message);
				((InfluenceFreeAnswerWithFileViewInterface) parent).submitFile(event.getChangeType(), UriUtils.fromSafeConstant(uploadUrl),
																				parameter);
			}
		}
	}

	public void enableSubmit(boolean enable) {
		if(enable)
			this.view.enableSubmit();
		else
			this.view.disableSubmit();
	}

	public void showErrorMinMax(int currentSize)
	{
		hideErrors();
		showError(new ErrorSelectionRestrictionView(minOptions, maxOptions, currentlySelectedOptions));
		this.view.disableSubmit();
	}

	public long getUserId()
	{
		return getAppController().getRequestInformation().getUserId();
	}

	public String getUserName()
	{
		return getAppController().getRequestInformation().getUserName();
	}

	private boolean isLoggedIn()
	{
		return getUserId() >= 0;
	}

	public boolean userIsAdmin()
	{
		return getAppController().getRequestInformation().getUserIsAdmin();
	}

	/**
	 * to be called by View if a submit happens...maybe add lots of parameters
	 * if Presenter cannot determine himself.
	 */
	public void onSubmitForm()
	{
		this.view.hideErrors();
		this.view.disableSubmit();

		if (this.timeout == null)
		{
			this.view.showError(new ErrorNotYetStartedView());
			return;
		}

		List<InfluenceAnswerViewInterface<Object>> selected = this.view.getSelectedAnswers();
		List<InfluenceAnswer> selectedAnswers = strategy.collectSelectedAnswers(selected);
		if (selectedAnswers == null || selectedAnswers.size() == 0) return;

		String sid = getAppController().getLoginManager().getSessionID();
		getAppController().getRPCFactory().getInfluenceService()
							.answerInfluence(sid, this.influenceId, selectedAnswers, new AsyncCallback<Boolean>()
							{

								@Override
								public void onFailure(Throwable caught)
								{
									Window.alert("Error: " + (caught != null ? caught.getMessage() : "Result was false."));
								}

								@SuppressWarnings("deprecation")
								@Override
								public void onSuccess(Boolean result)
								{
									if (!result)
									{
										onFailure(null);
										return;
									}
									String infCookies = Cookies.getCookie(INFLUENCE_COOKIE);
									Cookies.setCookie(INFLUENCE_COOKIE, (infCookies == null ? "" : infCookies + ",")
																		+ InfluencePresenter.this.influenceId, new Date(200, 1, 1));
									InfluencePresenter.this.showResults = true;
									refresh();
								}
							});
	}
	
	private void refresh()
	{
		refresh(false);
	}

	private void refresh(boolean log)
	{
		String sid = getAppController().getLoginManager().getSessionID();
		influenceService.getInfluence(sid, this.externalId, log, new AsyncCallback<Influence>()
		{

			@Override
			public void onFailure(Throwable caught)
			{
				if (caught instanceof NotVisibleException)
				InfluencePresenter.this.view.showError(new ErrorVisibilityViolationView());
				else if (caught instanceof NullPointerException)
				Window.alert("Influence was null");
			}

			@Override
			public void onSuccess(Influence result)
			{
				if (result == null)
				Window.alert("Influence was null");

				InfluencePresenter.this.influence = result;
				InfluencePresenter.this.strategy.clearAnsweres();
				InfluencePresenter.this.hideErrors();
				InfluencePresenter.this.currentlySelectedOptions = 0;
				InfluencePresenter.this.timeout = result.getTimeout();
				// if is finished, OR not yet started but is admin
				InfluencePresenter.this.showResults = determineTimeoutState() > 0
														|| (determineTimeoutState() < 0 && userIsAdmin());
				InfluencePresenter.this.showResults = InfluencePresenter.this.showResults || alreadyVisited(result);
				InfluencePresenter.this.view.showSubmit(!showResults);
				if (InfluencePresenter.this.showResults)
				{
					InfluencePresenter.this.totalNumberOfVotes = result.getTotalNumberOfGivenVotes();
					InfluencePresenter.this.mostVoted = result.getWinner();
				}

				showAnswers(result);
			}
		});
	}
	

	private void scheduleAnswerRefreshmentPolling() {
		if(answerRefreshTimer != null) {
			answerRefreshTimer.cancel();
		}
		else {
			answerRefreshTimer = new Timer()
			{
				public void run()
				{
					String sid = getAppController().getLoginManager().getSessionID();
					influenceService.getInfluence(sid, InfluencePresenter.this.externalId, new AsyncCallback<Influence>()
					{
						public void onFailure(Throwable caught) {						}

						@Override
						public void onSuccess(Influence freshInfl) {
							List<InfluenceAnswer> newFreeAnswers = compareAnswers(freshInfl, InfluencePresenter.this.influence);
							
							if(newFreeAnswers.size() > 0) {
								lazyPaintAdditionalAnswers(newFreeAnswers);
								InfluencePresenter.this.influence = freshInfl;
							}
						}
					});
				}

				private List<InfluenceAnswer> compareAnswers(Influence freshInfl, Influence influence) {
					List<InfluenceAnswer> newAnswers = new ArrayList<InfluenceAnswer>();
					for(InfluenceAnswer a : freshInfl.getFreeAnswers()) {
						if(influence.getFreeAnswer(a.getId()) == null && a.getOwnerId() != getUserId())
						{
							newAnswers.add(a);
						}
					}
					return newAnswers;
				}
			};
		}
		// Schedule the timer to run every 2s
		answerRefreshTimer.scheduleRepeating(2000);
	}

	@Override
	public void onFormSubmitCompleteEvent(FormSubmitCompleteEvent event)
	{
		// only called by events from form options
		// of interest if files are completed to be uploaded
		InfluenceAnswerViewInterface<?> answer = this.strategy.findParentViewForElement(event.getSourceWidget());
		JSONValue result = null;
		try
		{
			result = JSONParser.parseStrict(event.getResult());
		}
		catch (NullPointerException e)
		{
		}
		catch (JSONException e)
		{
			result = null;
		}
		if (result == null || result.isObject() == null)
		{
			hideErrors();
			showError(communiationError);
		}
		else if (result.isObject().containsKey("success") && result.isObject().get("success").isBoolean() != null
					&& result.isObject().get("success").isBoolean().booleanValue()
					&& answer instanceof InfluenceFreeAnswerViewInterface && result.isObject().containsKey("message")
					&& result.isObject().containsKey("file"))
		{
			this.strategy.updateFreeAnswerOnUploadSuccess((InfluenceFreeAnswerViewInterface) answer,
															event.getSourceParameter(), result);
		}
		else
		{// handle all other errors
			JSONObject resultObj = result.isObject();
			if (resultObj != null && resultObj.containsKey("error"))
			{
				hideErrors();
				showError(ErrorViewFactory.getErrorView(event.getResult()));
			}
		}
	}

	@Override
	public void onFormSubmitEvent(FormSubmitEvent event)
	{
		InfluenceAnswerViewInterface<?> answer = this.strategy.findParentViewForElement(event.getSourceWidget());
		// FIXME: is this right?
		switch (currentMode)
		{
		case text: {
			this.strategy.updateFreeAnswerOnUploadSuccess((InfluenceFreeAnswerViewInterface) answer, null, null);
			break;
		}
		default:
			;
		}
	}

	public int getTotalNumberOfVotes()
	{
		return this.totalNumberOfVotes;
	}

	public Object getWinner()
	{
		return mostVoted;
	}
	
	public Influence getInfluenceObject(){
		return influence;
	}

	@Override
	public void onLoginSuccessEvent(LoginEvent event)
	{
		// if(this.view.asWidget().isAttached())
		if (externalId != null) // prevent trigger on direct load // TODO RH:
								// you could as well just check if presenters
								// view is anywhere attached
		refresh(true);
	}

	@Override
	public void onLogoutEvent(LogoutEvent event)
	{
		refresh();
	}

	@Override
	public void onLoginNetworkSuccessEvent(LoginEvent event)
	{
		refresh(true);
	}

	@Override
	public void onLoginErrorNetworkUserNotFound(LoginErrorNetworkUserNotFoundEvent loginErrorNetworkUserNotFound)
	{
	}

	@Override
	public void onLoginErrorWrongUserIDPasswortEvent(
		LoginErrorWrongUserIDPasswortEvent loginErrorWrongUserIDPasswortEvent)
	{
	}

	public void hideError(ErrorView errorView)
	{
		this.view.hideErrors(); // TODO JK: support hiding given error.
	}

	public void onStartInfluence(Long value)
	{
		if (value == null) return;
		String sid = getAppController().getLoginManager().getSessionID();
		getAppController().getRPCFactory().getAdministrationService()
							.startInfluence(sid, influenceId, value, new AsyncCallback<Boolean>()
							{

								@Override
								public void onFailure(Throwable caught)
								{
									view.showError(communiationError);
								}

								@Override
								public void onSuccess(Boolean result)
								{
									if (!result)
									{
										onFailure(null);
										return;
									}
									refresh();
								}
							});
	}

	public void onStopInfluence()
	{
		String sid = getAppController().getLoginManager().getSessionID();
		getAppController().getRPCFactory().getAdministrationService()
							.stopInfluence(sid, influenceId, new AsyncCallback<Boolean>()
							{

								@Override
								public void onFailure(Throwable caught)
								{
									view.showError(communiationError);
								}

								@Override
								public void onSuccess(Boolean result)
								{
									if (!result)
									{
										onFailure(null);
										return;
									}
									refresh();
								}
							});
	}

	public void adminIllegalTimeValue(boolean show)
	{
		if (show) showError(illegalDurationError);
		else hideError(illegalDurationError);
	}
}