package de.tud.kom.socom.web.client.influence.strategies;

import java.util.List;
import java.util.Map;

import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

import de.tud.kom.socom.web.client.AppController;
import de.tud.kom.socom.web.client.administration.itemadministration.ItemAdministration.ChangeVisibilityButtonCallback;
import de.tud.kom.socom.web.client.administration.itemadministration.ItemAdministration.DeleteButtonCallback;
import de.tud.kom.socom.web.client.administration.itemadministration.ItemAdministrationState;
import de.tud.kom.socom.web.client.influence.InfluencePresenter.InfluenceAnswerViewInterface;
import de.tud.kom.socom.web.client.influence.InfluencePresenter.InfluenceFreeAnswerViewInterface;
import de.tud.kom.socom.web.client.reporting.ItemReportView.SendReportCallback;
import de.tud.kom.socom.web.client.sharedmodels.InfluenceAnswer;
import de.tud.kom.socom.web.client.sharedmodels.SimpleEntry;
import de.tud.kom.socom.web.client.util.ShortNotification;

public abstract class InfluenceStrategy<T> {
	
	private static final String INFLUENCE_REPORT_TYPE_IDENTIFIER = "influenceanswer";
	
	private AppController appController;

	protected InfluenceStrategy(AppController appController)
	{
		this.appController = appController;
	}

	public abstract void addPredefinedAnswer(InfluenceAnswer answer);

	public abstract InfluenceAnswerViewInterface<T> addFreeAnswer(InfluenceAnswer answer, boolean votable);

	public abstract void addFreeAnswerForm();
	
	protected AppController getAppController()
	{
		return this.appController;
	}
	
	public void appendItemAdministration(long uid, boolean isAdmin) {
		Map<InfluenceAnswerViewInterface<T>, InfluenceAnswer> answers = getAnswerMap();
		
		for (final InfluenceAnswerViewInterface<T> view : answers.keySet()) {
			
			if(!(isAdmin || answers.get(view).getOwnerId() == uid) || answers.get(view).isNewFreeAnswer()) {
				continue;
			}
			
			final InfluenceAnswer answer = answers.get(view);
			ItemAdministrationState state = ItemAdministrationState.delete;
			if (answer.getDeletedFlag() > 0)
				state = ItemAdministrationState.undelete;
			
			view.setAdministrationState(state, generateDeleteCallback(view, answer));
			int visibility = answer.getVisibility();
			view.setItemAdministrationVisibility(visibility);
			view.setItemAdministrationVisibilityChangeCallback(generateVisibilityChangeCallback(view, answer));
		}
		
	}

	private ChangeVisibilityButtonCallback generateVisibilityChangeCallback(final InfluenceAnswerViewInterface<T> view,
			final InfluenceAnswer answer) {
		return new ChangeVisibilityButtonCallback() {
			
			@Override
			public void onClicked(final int newVisibility) {
				if(answer.getVisibility() == newVisibility) return;
				String sid = getAppController().getLoginManager().getSessionID();
				appController.getRPCFactory().getAdministrationService().changeInfluenceAnswerVisibilityState
					(sid, answer.isPredefined(), answer.getId(), newVisibility, new AsyncCallback<Boolean>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Error changing visibility. (" + caught.getMessage() + ")");
					}

					@Override
					public void onSuccess(Boolean result) {
						if(result){
							view.setItemAdministrationVisibility(newVisibility);
							answer.setVisibility(newVisibility);
						}
						else
							Window.alert("Error changing visibility.");
					}
				});
			}
		};
	}

	private DeleteButtonCallback generateDeleteCallback(final InfluenceAnswerViewInterface<T> view, 
			final InfluenceAnswer answer) {
		return new DeleteButtonCallback() {

			@Override
			public void onClicked(final ItemAdministrationState state) {
				int deleted = state == ItemAdministrationState.delete ? 1 : 0; /* only hide, or undelete */
				String sid = getAppController().getLoginManager().getSessionID(); 
				appController.getRPCFactory().getAdministrationService().changeInfluenceAnswerDeletedState
					(sid, answer.isPredefined(), answer.getId(), deleted, new AsyncCallback<Boolean>() {
					
					@Override
					public void onSuccess(Boolean result) {							
						if(result) {
							ItemAdministrationState newState = 
									(state == ItemAdministrationState.delete) ? 
											ItemAdministrationState.undelete: ItemAdministrationState.delete;
							view.setAdministrationState(newState);
							view.setVotable(newState != ItemAdministrationState.undelete);
						} else {
							Window.alert("Error changing deletion state.");
						}
					}
					
					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Error changing deletion state. (" + caught.getMessage() + ")");
					}
				});
			}
		};
	}
	
	public void appendItemReporting(final String externalid, final long userId) {
		final Map<InfluenceAnswerViewInterface<T>, InfluenceAnswer> answers = getAnswerMap();
		for(final InfluenceAnswerViewInterface<T> a : answers.keySet()) {
			//implement report callbacks and apply
			if(!answers.get(a).isNewFreeAnswer())
				a.setSendReportCallback(generateReportCallback(externalid, answers, a));
		}
	}

	private SendReportCallback generateReportCallback(final String externalid, final Map<InfluenceAnswerViewInterface<T>, 
			InfluenceAnswer> answers, final InfluenceAnswerViewInterface<T> a) {
		return new SendReportCallback() {
			@Override
			public void onSendReport(String report) {
				long reference = answers.get(a).getId();
				appController.getRPCFactory().getReportingService().sendReport
					(reference, externalid, INFLUENCE_REPORT_TYPE_IDENTIFIER, report, 
						getAppController().getLoginManager().getSessionID(), new AsyncCallback<Boolean>() {
					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Error sending report (" + (caught == null ? "false" : caught.getLocalizedMessage()) + ")");
					}
					@Override
					public void onSuccess(Boolean result) {
						if(result){
							ShortNotification.show("Report successfully submitted.", 2500, false);
						}
						else onFailure(null);
					}
				});
			}
		};
	}
	
	String getDataPath(String filePath) {
		//XXX use resource manager..?
		return "data/influence_data/" + filePath;
	}

	public abstract List<InfluenceAnswerViewInterface<T>> getPossibleAnswers();

	public abstract List<InfluenceAnswer> collectSelectedAnswers(List<InfluenceAnswerViewInterface<T>> selected);

	public abstract InfluenceAnswerViewInterface<T> findParentViewForElement(Widget sourceWidget);

	public abstract void updateFreeAnswerOnChange(InfluenceFreeAnswerViewInterface<T> parent, String message);

	public abstract void updateFreeAnswerOnUploadSuccess(InfluenceFreeAnswerViewInterface<T> answer, SimpleEntry<String, String>[] sourceParameter,
			JSONValue result);

	public abstract void clearAnsweres();

	public abstract Map<InfluenceAnswerViewInterface<T>, InfluenceAnswer> getAnswerMap();

	public boolean hasValidFileEnding(String filename) {
		for(String s:getValidFileEndings())
		{
			if(filename.endsWith("."+s)) return true;
		}
		return false;
	}
	
	public abstract String[] getValidFileEndings();
}
