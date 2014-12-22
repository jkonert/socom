package de.tud.kom.socom.web.client.administration;

import java.util.List;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

import de.tud.kom.socom.web.client.AppController;
import de.tud.kom.socom.web.client.AppController.PageElementIDs;
import de.tud.kom.socom.web.client.AppController.Presenters;
import de.tud.kom.socom.web.client.HistoryManager;
import de.tud.kom.socom.web.client.HistoryToken;
import de.tud.kom.socom.web.client.baseelements.AbstractMainPresenter;
import de.tud.kom.socom.web.client.baseelements.ViewInterface;
import de.tud.kom.socom.web.client.baseelements.ViewWithErrorsInterface;
import de.tud.kom.socom.web.client.baseelements.viewerrors.ErrorServerUnknownErrorView;
import de.tud.kom.socom.web.client.eventhandler.LoginEventHandler;
import de.tud.kom.socom.web.client.eventhandler.ViewChangeWithinPresenterEventHandler;
import de.tud.kom.socom.web.client.events.LoginErrorNetworkUserNotFoundEvent;
import de.tud.kom.socom.web.client.events.LoginErrorWrongUserIDPasswortEvent;
import de.tud.kom.socom.web.client.events.LoginEvent;
import de.tud.kom.socom.web.client.events.LogoutEvent;
import de.tud.kom.socom.web.client.events.ViewChangeWithinPresenterEvent;
import de.tud.kom.socom.web.client.sharedmodels.GameInstance;
import de.tud.kom.socom.web.client.sharedmodels.Report;
import de.tud.kom.socom.web.client.sharedmodels.SimpleUser;
import de.tud.kom.socom.web.client.util.ShortNotification;

public class AdministrationPresenter extends AbstractMainPresenter implements LoginEventHandler,
		ViewChangeWithinPresenterEventHandler {

	public interface AdministrationViewInterface<T> extends ViewInterface, ViewWithErrorsInterface {
		void updateInformation(List<T> res, UIObject parent);
	}

	public enum AdministrationSection {

		overview, illegalaccess, user, influence, content, statistic, report;

		public static AdministrationSection getEnum(String name) {
			if (name == null)
				return null;
			try {
				return valueOf(name);
			} catch (IllegalArgumentException e) {
				return null;
			}
		}
	}

	private static AdministrationPresenter instance;
	private AdministrationViewInterface<?> view;
	private String[] deletedStates;
	private DialogBox dialogBox;

	private AdministrationPresenter(AppController appController) {
		super(appController);
		getAppController().getEventHandler().addHandler(LoginEvent.TYPE, this);
		getAppController().getEventHandler().addHandler(ViewChangeWithinPresenterEvent.TYPE, this);
		init();
	}

	public static AdministrationPresenter getInstance(AppController appController) {
		if (instance == null)
			instance = new AdministrationPresenter(appController);
		return instance;
	}

	@Override
	public void init() {
		this.dialogBox = new DialogBox(true);
		HistoryToken currentHistoryToken = getAppController().getCurrentHistoryToken();
		String presenterModule = currentHistoryToken == null ? null : currentHistoryToken.getPresenterModule();
		selectModuleView(presenterModule);
	}

	@Override
	public void go(RootPanel targetPanel) {
		setTargetPanel(targetPanel);
		
		// fade out the header
		getAppController().getPageElement(PageElementIDs.teaserImage).hide();
	}

	public void openSection(AdministrationSection section) {
		HistoryManager.newItem(new HistoryToken(getAppController().getRequestInformation().getCurrentGame(),
				Presenters.admin, section.name()));
		HistoryManager.fireCurrentHistoryState();
	}

	@Override
	public void onViewChangeWithinPresenterEvent(ViewChangeWithinPresenterEvent event) {
		if(event.getCurrentPresenter().equals(this)){
			String presenterModule = event.getNewHistoryValue().getPresenterModule();
			selectModuleView(presenterModule);
		}
	}

	private void selectModuleView(String presenterModule) {
		AdministrationSection newSection = AdministrationSection.getEnum(presenterModule);

		boolean isAdmin = getAppController().getRequestInformation().getUserIsAdmin();
		if (newSection == null || (newSection.equals(AdministrationSection.illegalaccess) && isAdmin))
			newSection = AdministrationSection.overview;
		if (!isAdmin) {
			newSection = AdministrationSection.illegalaccess;
			HistoryManager.newItem(new HistoryToken(getAppController().getRequestInformation().getCurrentGame(),
					Presenters.admin, newSection.name()));
		}

		switch (newSection) {
		case overview:
			this.view = new AdministrationView(this);
			break;
		case user:
			this.view = new UserAdministrationView(this);
			break;
		case influence:
			this.view = new InfluenceAdministrationView(this);
			break;
		case content:
			this.view = new ContentAdministrationView(this);
			break;
		case statistic:
			this.view = new GameStatisticAdministrationView(this);
			break;
		case report:
			this.view = new ReportAdministrationView(this);
			break;
		case illegalaccess:
			this.view = new IllegalAccessAdministrationView();
			break;
		default:
			this.view = new AdministrationView(this);
		}
		setView(view);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void loadUsers(String startingWith, final UIObject parent) {
		// TODO: filter last treeitem (*) as it should receive 'all other' users
		getAppController().getRPCFactory().getAdministrationService()
				.getUsersStartingWith(startingWith, new AsyncCallback<List<SimpleUser>>() {

					@Override
					public void onSuccess(List<SimpleUser> result) {
						view.updateInformation((List) result, parent);
					}

					@Override
					public void onFailure(Throwable caught) {
						view.updateInformation(null, parent);
						view.showError(new ErrorServerUnknownErrorView());
					}
				});
	}

	public void loadGames(final ListBox gameList) {
		long uid = this.getAppController().getRequestInformation().getUserId();
		this.getAppController().getRPCFactory().getGameService()
				.getGameInstances(uid, -1, new AsyncCallback<List<GameInstance>>() {
	
					@Override
					public void onFailure(Throwable caught) {
						view.showError(new ErrorServerUnknownErrorView());
					}
	
					@SuppressWarnings({ "rawtypes", "unchecked" })
					@Override
					public void onSuccess(List<GameInstance> result) {
						view.updateInformation((List) result, gameList);
					}
				});
	}

	/**
	 * @param fromInformant
	 *            if null ignore
	 * @param sortPolicy
	 *            coding: 0=date, 1=type, 2=informant
	 * @param ascending
	 *            otherwise desc
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void loadReports(String[] types, boolean alreadyReviewed, String fromInformant, int limit, int offset,
			int sortPolicy, boolean ascending) {
		getAppController()
				.getRPCFactory()
				.getReportingService()
				.getReports(types, alreadyReviewed, fromInformant, limit, offset, sortPolicy, ascending,
						getAppController().getLoginManager().getSessionID(), new AsyncCallback<List<Report>>() {
	
							@Override
							public void onFailure(Throwable caught) {
								view.updateInformation(null, null);
								view.showError(new ErrorServerUnknownErrorView());
							}
	
							@Override
							public void onSuccess(List<Report> result) {
								view.updateInformation((List) result, null);
							}
						});
	}

	public void onUserClicked(final SimpleUser user) {
		if (deletedStates == null) {
			getAppController().getRPCFactory().getAdministrationService().getDeletedStates(new AsyncCallback<String[]>() {

				@Override
				public void onFailure(Throwable caught) {
					view.showError(new ErrorServerUnknownErrorView());
				}

				@Override
				public void onSuccess(String[] result) {
					AdministrationPresenter.this.deletedStates = result;
					showDialog(new UserPropertiesView(AdministrationPresenter.this, user,
							AdministrationPresenter.this.deletedStates));
				}
			});
		} else {
			showDialog(new UserPropertiesView(AdministrationPresenter.this, user,
					AdministrationPresenter.this.deletedStates));
		}
	}

	public void onGameGraphSelection(long parseLong) {
		String sid = getAppController().getLoginManager().getSessionID();
		this.getAppController().getRPCFactory().getStatisticService().getGraph(parseLong, sid, new AsyncCallback<String>() {
	
			@Override
			public void onFailure(Throwable caught) {
				view.showError(new ErrorServerUnknownErrorView());
			}
	
			@Override
			public void onSuccess(String result) {
				if (view instanceof GameStatisticAdministrationView) {
					JSONObject graph = JSONParser.parseStrict(result).isObject();
					((GameStatisticAdministrationView) view).setGraphData(graph);
				}
			}
		});
	}

	public void onReportClicked(Report report) {
		showDialog(new ReportReviewView(this, report));
	}

	public void onSaveUserStateClicked(final SimpleUser user, final int newDeletedState) {
		final UserPropertiesView userView = (UserPropertiesView) dialogBox.getWidget();
		if (userView == null)
			return;
		String sid = getAppController().getLoginManager().getSessionID();
		getAppController().getRPCFactory().getAdministrationService()
				.changeUserDeletedState(sid, user.getUid(), newDeletedState, new AsyncCallback<Boolean>() {
					@Override
					public void onFailure(Throwable caught) {
						userView.showSaveSuccess(false);
					}
	
					@Override
					public void onSuccess(Boolean result) {
						user.setDeleted(newDeletedState);
						userView.showSaveSuccess(true);
					}
				});
	}

	public void onSaveReportReviewClicked(final Report report, String text) {
		getAppController().getRPCFactory().getReportingService().closeReport(report.getId(), text, 
				getAppController().getLoginManager().getSessionID(), new AsyncCallback<Boolean>() {

					@Override
					public void onFailure(Throwable caught) {
						hideDialog();
						showError(new ErrorServerUnknownErrorView());
					}

					@Override
					public void onSuccess(Boolean result) {
						if(!result){
							ReportReviewView reviewView = (ReportReviewView) dialogBox.getWidget();
							reviewView.showUnsuccess();
						} else {
							((ReportAdministrationView) view).checkReviewed(report.getId());;
							hideDialog();
							ShortNotification.show("Bewertung abgegeben", 3000, false);
						}
					}
		});
	}

	private void showDialog(Widget w) {
		dialogBox.setWidget(w);
		dialogBox.getElement().getStyle().setZIndex(500);
		dialogBox.center();
	}

	public void hideDialog() {
		dialogBox.hide();
		dialogBox.remove(dialogBox.getWidget());
	}
	
	@Override
	public void onLoginSuccessEvent(LoginEvent event) {
		if (view.asWidget().isAttached())
			init();
	}

	@Override
	public void onLogoutEvent(LogoutEvent event) {
		if (view.asWidget().isAttached())
			init();
	}

	@Override
	public void onLoginNetworkSuccessEvent(LoginEvent event) {
		if (view.asWidget().isAttached())
			init();
	}

	@Override
	public void onLoginErrorNetworkUserNotFound(LoginErrorNetworkUserNotFoundEvent loginErrorNetworkUserNotFound) {
	}

	@Override
	public void onLoginErrorWrongUserIDPasswortEvent(LoginErrorWrongUserIDPasswortEvent loginErrorWrongUserIDPasswortEvent) {
	}
}
