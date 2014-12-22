package de.tud.kom.socom.web.client.influence.strategies;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Widget;

import de.tud.kom.socom.web.client.AppController;
import de.tud.kom.socom.web.client.baseelements.viewerrors.ErrorMediaNotReadyView;
import de.tud.kom.socom.web.client.baseelements.viewerrors.ErrorServerUnknownErrorView;
import de.tud.kom.socom.web.client.influence.InfluencePresenter;
import de.tud.kom.socom.web.client.influence.InfluencePresenter.InfluenceAnswerViewInterface;
import de.tud.kom.socom.web.client.influence.InfluencePresenter.InfluenceFreeAnswerViewInterface;
import de.tud.kom.socom.web.client.influence.audio.AnswerAudio;
import de.tud.kom.socom.web.client.influence.audio.InfluenceAnswerAudioView;
import de.tud.kom.socom.web.client.influence.audio.InfluenceAnswerFreeAudioView;
import de.tud.kom.socom.web.client.sharedmodels.InfluenceAnswer;
import de.tud.kom.socom.web.client.sharedmodels.SimpleEntry;

public class AudioInfluenceStrategy extends InfluenceStrategy<AnswerAudio> {

	private static final String[] VALID_ENDINGS = new String[] {"mp3", "ogg"};
	private InfluencePresenter presenter;
	private List<InfluenceAnswerViewInterface<AnswerAudio>> answersAudio;
	private HashMap<InfluenceAnswerViewInterface<AnswerAudio>, InfluenceAnswer> answers;
	private Timer fileDetectionTimer;
	private ErrorMediaNotReadyView notReadyError;
	private ErrorServerUnknownErrorView unkownError;
	private int freeAnswerFormCount;
	
	public AudioInfluenceStrategy(AppController appController, InfluencePresenter influencePresenter) {
		super(appController);
		presenter = influencePresenter;
		answersAudio = new LinkedList<InfluenceAnswerViewInterface<AnswerAudio>>();
		answers = new HashMap<InfluenceAnswerViewInterface<AnswerAudio>,InfluenceAnswer>();
		notReadyError = new ErrorMediaNotReadyView();
		unkownError = new ErrorServerUnknownErrorView();
		freeAnswerFormCount = 0;
	}

	@Override
	public void addPredefinedAnswer(InfluenceAnswer answer) {
		InfluenceAnswerAudioView audioanswer = new InfluenceAnswerAudioView(false);
		String filePath = getDataPath(answer.getAnswer().split(";")[1]);
		audioanswer.setAnswer(new AnswerAudio(filePath, answer.getAnswer().split(";")[0]));
		if(presenter.showResults){
			audioanswer.setResultInformation(presenter.getTotalNumberOfVotes(), answer.getResult().getVotes(), presenter.getWinner().equals(answer));
		}
		answersAudio.add(audioanswer);
		answers.put(audioanswer, answer);
	}

	@Override
	public InfluenceAnswerViewInterface<AnswerAudio> addFreeAnswer(InfluenceAnswer answer, boolean votable) {
		InfluenceAnswerAudioView audioanswer = new InfluenceAnswerAudioView(presenter.showResults);
		String filePath = getDataPath(answer.getAnswer().split(";")[1]);
		audioanswer.setAnswer(new AnswerAudio(filePath, answer.getAnswer().split(";")[0]));
		audioanswer.setFromUser(answer.getOwnerId(), answer.getOwnerName(), answer.getResult().getTimestamp());
		if(presenter.showResults){
			audioanswer.setResultInformation(presenter.getTotalNumberOfVotes(), answer.getResult().getVotes(), presenter.getWinner().equals(answer));
		}
		audioanswer.setVotable(votable);
		answersAudio.add(audioanswer);
		answers.put(audioanswer, answer);
		return audioanswer;
	}

	@Override
	public void addFreeAnswerForm() {
		if(presenter.showResults) return;
		/**an answer with free audio...changes to this are reflected in FormValueChangeEvent **/
		InfluenceAnswerFreeAudioView audioanswer = new InfluenceAnswerFreeAudioView(false, 60);			
		answersAudio.add(audioanswer);
		//TODO visibility
		answers.put(audioanswer, new InfluenceAnswer(this.presenter.getUserId(), true, 2));
		this.freeAnswerFormCount++;
	}

	@Override
	public List<InfluenceAnswer> collectSelectedAnswers(List<InfluenceAnswerViewInterface<AnswerAudio>> selected) {		
		List<InfluenceAnswer> selectedAnswers = new LinkedList<InfluenceAnswer>();
		for (InfluenceAnswerViewInterface<AnswerAudio> formelem: selected)
		{
			InfluenceAnswer a = this.answers.get(formelem);
			if(a != null){
				if(a.isNewFreeAnswer()){
					a.setOwnerId(presenter.getUserId());
				}
				selectedAnswers.add(a);
			}
		}
		if (selectedAnswers.size() < presenter.minOptions || selectedAnswers.size() > presenter.maxOptions)
		{
			presenter.showErrorMinMax(selectedAnswers.size());
			return null;
		}
		return selectedAnswers;
	}

	@Override
	public InfluenceAnswerViewInterface<AnswerAudio> findParentViewForElement(Widget sourceWidget) {
		for(InfluenceAnswerViewInterface<AnswerAudio> a: answersAudio)
		{			
			if (a.equals(sourceWidget)) return a;
		}
		return null;
	}

	@Override
	public void updateFreeAnswerOnChange(InfluenceFreeAnswerViewInterface<AnswerAudio> answer, String value) {
	}

	@Override
	public void updateFreeAnswerOnUploadSuccess(
		final InfluenceFreeAnswerViewInterface<AnswerAudio> answer,
		SimpleEntry<String, String>[] sourceParameter, final JSONValue result) {
		final String filePath = result.isObject().get("file").isString().stringValue();
		
		fileDetectionTimer = new Timer() {

			@Override
			public void run() {
				RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, GWT.getHostPageBaseURL() + getDataPath(filePath));

			    try {
			      builder.sendRequest(null, new RequestCallback() {
			        public void onError(Request request, Throwable exception) {
			          AudioInfluenceStrategy.this.presenter.showError(unkownError);
			        }

			        public void onResponseReceived(Request request, Response response) {
			          if(response.getStatusCode() == 404)
			          {
			        	  AudioInfluenceStrategy.this.presenter.showError(notReadyError);
			          }
			          else
			          {
			        	  fileDetectionTimer.cancel();
			        	  onFreeAnswerFileReady(answer, result);
			          }
			        }
			      });
			    } catch (RequestException e) {
			    	AudioInfluenceStrategy.this.presenter.showError(unkownError);
			    }
			}
		};
		
		fileDetectionTimer.scheduleRepeating(500);
	}
	
	protected void onFreeAnswerFileReady(InfluenceFreeAnswerViewInterface<AnswerAudio> answer, JSONValue result) {
		this.presenter.hideError(notReadyError);
		String filePath = result.isObject().get("file").isString().stringValue();
		InfluenceAnswer newAnswer = createReceivedAnswer(result.isObject());
		replaceFreeAnswerFormWithAnswer(answer, filePath, newAnswer);
		if(this.presenter.maxOptions > this.freeAnswerFormCount)
			this.addFreeAnswerForm();
		this.presenter.paintAnswers();
	}

	private InfluenceAnswer createReceivedAnswer(JSONObject object) {
		String message = object.get("message").isString().stringValue();
		long id = (long)object.get("id").isNumber().doubleValue();
		long ownerid = this.presenter.getUserId();
		String ownerName = this.presenter.getUserName();
		int deletedFlag = 0;
		int visibility = 2;
		return new InfluenceAnswer(id, message, false, ownerid, ownerName, deletedFlag, visibility);
	}

	private InfluenceAnswerAudioView replaceFreeAnswerFormWithAnswer(InfluenceFreeAnswerViewInterface<AnswerAudio> answer, String filePath, InfluenceAnswer newAnswer) {
		InfluenceAnswerAudioView newanswer = new InfluenceAnswerAudioView(presenter.showResults);	
		newanswer.setAnswer(new AnswerAudio(getDataPath(filePath), newAnswer.getAnswer()));
		newanswer.setFromUser(this.presenter.getUserId(), this.presenter.getUserName(), new Date());
		if(answersAudio.remove(answer) && answers.remove(answer) != null){
			answersAudio.add(newanswer);
			answers.put(newanswer, newAnswer);
		}
		return newanswer;
	}

	@Override
	public  List<InfluenceAnswerViewInterface<AnswerAudio>> getPossibleAnswers() {
		return answersAudio;
	}

	@Override
	public void clearAnsweres() {
		answersAudio.clear();
		answers.clear();
	}

	@Override
	public Map<InfluenceAnswerViewInterface<AnswerAudio>, InfluenceAnswer> getAnswerMap() {
		return answers;
	}

	@Override
	public String[] getValidFileEndings() {
		return VALID_ENDINGS;
	}
}