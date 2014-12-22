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
import de.tud.kom.socom.web.client.influence.image.AnswerImage;
import de.tud.kom.socom.web.client.influence.image.InfluenceAnswerFreeImageView;
import de.tud.kom.socom.web.client.influence.image.InfluenceAnswerImageView;
import de.tud.kom.socom.web.client.sharedmodels.InfluenceAnswer;
import de.tud.kom.socom.web.client.sharedmodels.SimpleEntry;

public class ImageInfluenceStrategy extends InfluenceStrategy<AnswerImage> {

	private static final String[] VALID_ENDINGS = new String[] { "png", "jpg", "jpeg", "gif" };

	private InfluencePresenter presenter;
	private List<InfluenceAnswerViewInterface<AnswerImage>> answerImageViews;
	private Map<InfluenceAnswerViewInterface<AnswerImage>, InfluenceAnswer> answersPerAnswerView;
	private Timer fileDetectionTimer;
	private ErrorMediaNotReadyView notReadyError;
	private ErrorServerUnknownErrorView unkownError;
	private int freeAnswerFormCount;

	public ImageInfluenceStrategy(AppController appController, InfluencePresenter influencePresenter) {
		super(appController);
		presenter = influencePresenter;
		answerImageViews = new LinkedList<InfluenceAnswerViewInterface<AnswerImage>>();
		answersPerAnswerView = new HashMap<InfluenceAnswerViewInterface<AnswerImage>, InfluenceAnswer>();
		notReadyError = new ErrorMediaNotReadyView();
		unkownError = new ErrorServerUnknownErrorView();
		freeAnswerFormCount = 0;
	}

	@Override
	public void addPredefinedAnswer(InfluenceAnswer answer) {
		InfluenceAnswerImageView imageanswer = new InfluenceAnswerImageView(presenter.showResults);
		String[] split = answer.getAnswer().split(";");
		AnswerImage ai = new AnswerImage(split[1], split[0]);
		imageanswer.setAnswer(ai);
		if (presenter.showResults)
		{
			imageanswer.setResultInformation(presenter.getTotalNumberOfVotes(), answer.getResult().getVotes(), presenter
					.getWinner().equals(answer));
		}
		answerImageViews.add(imageanswer);
		answersPerAnswerView.put(imageanswer, answer);
		imageanswer.enableReporting(false);
	}

	@Override
	public InfluenceAnswerViewInterface<AnswerImage> addFreeAnswer(InfluenceAnswer answer, boolean votable) {
		InfluenceAnswerImageView imageanswer = new InfluenceAnswerImageView(presenter.showResults);
		String[] split = answer.getAnswer().split(";");
		AnswerImage ai = new AnswerImage(split[1], split[0]);
		imageanswer.setAnswer(ai);
		imageanswer.setFromUser(answer.getOwnerId(), answer.getOwnerName(), answer.getResult().getTimestamp());
		if (presenter.showResults)
		{
			imageanswer.setResultInformation(presenter.getTotalNumberOfVotes(), answer.getResult().getVotes(), presenter
					.getWinner().equals(answer));
		}
		imageanswer.setVotable(votable);
		answerImageViews.add(imageanswer);
		answersPerAnswerView.put(imageanswer, answer);
		return imageanswer;
	}

	@Override
	public void addFreeAnswerForm() {
		if (presenter.showResults)
			return;
		InfluenceAnswerFreeImageView imageanswer = new InfluenceAnswerFreeImageView(false, 60);
		answerImageViews.add(imageanswer);
		answersPerAnswerView.put(imageanswer, new InfluenceAnswer(this.presenter.getUserId(), true, 2));
		this.freeAnswerFormCount++;
	}

	@Override
	public List<InfluenceAnswerViewInterface<AnswerImage>> getPossibleAnswers() {
		return answerImageViews;
	}

	@Override
	public List<InfluenceAnswer> collectSelectedAnswers(List<InfluenceAnswerViewInterface<AnswerImage>> selected) {
		List<InfluenceAnswer> selectedAnswers = new LinkedList<InfluenceAnswer>();
		for (InfluenceAnswerViewInterface<AnswerImage> selectedAnswerView : selected)
		{
			InfluenceAnswer selectedAnswer = this.answersPerAnswerView.get(selectedAnswerView);
			if (selectedAnswer != null)
			{
				if (selectedAnswer.isNewFreeAnswer())
				{
					// TODO: filter if necessary
					selectedAnswer.setOwnerId(presenter.getUserId());
				}
				selectedAnswers.add(selectedAnswer);
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
	public InfluenceAnswerViewInterface<AnswerImage> findParentViewForElement(Widget sourceWidget) {
		for (InfluenceAnswerViewInterface<AnswerImage> a : answerImageViews)
		{
			if (a.equals(sourceWidget))
				return a;
		}
		return null;
	}

	@Override
	public void updateFreeAnswerOnChange(InfluenceFreeAnswerViewInterface<AnswerImage> parent, String message) {
	}

	@Override
	public void updateFreeAnswerOnUploadSuccess(final InfluenceFreeAnswerViewInterface<AnswerImage> answer,
			SimpleEntry<String, String>[] sourceParameter, final JSONValue result) {

		final String filePath = result.isObject().get("file").isString().stringValue();
		final boolean[] found = new boolean[] { false };
		fileDetectionTimer = new Timer() {

			@Override
			public void run() {
				RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, GWT.getHostPageBaseURL()
						+ getDataPath(filePath));

				try
				{
					builder.sendRequest(null, new RequestCallback() {
						public void onError(Request request, Throwable exception) {
							ImageInfluenceStrategy.this.presenter.showError(unkownError);
						}

						public void onResponseReceived(Request request, Response response) {
							if (found[0])
								return;
							if (response.getStatusCode() == 404)
							{
								ImageInfluenceStrategy.this.presenter.showError(notReadyError);
							} else
							{
								found[0] = true;
								fileDetectionTimer.cancel();
								onFreeAnswerFileReady(answer, result);
							}
						}
					});
				} catch (RequestException e)
				{
					ImageInfluenceStrategy.this.presenter.showError(unkownError);
				}
			}
		};

		fileDetectionTimer.scheduleRepeating(500);
	}

	protected void onFreeAnswerFileReady(InfluenceFreeAnswerViewInterface<AnswerImage> answer, JSONValue result) {
		this.presenter.hideError(notReadyError);
		String filePath = result.isObject().get("file").isString().stringValue();
		InfluenceAnswer newAnswer = createReceivedAnswer(result.isObject());
		AnswerImage newAnswerObject = new AnswerImage(filePath, newAnswer.getAnswer());
		
		boolean appendFreeAnswer = this.presenter.maxOptions > this.freeAnswerFormCount;
		replaceAnswers(answer, newAnswer, newAnswerObject, appendFreeAnswer);
	}

	private void replaceAnswers(InfluenceFreeAnswerViewInterface<AnswerImage> oldAnswerView, InfluenceAnswer newAnswer,
			AnswerImage newAnswerWrap, boolean appendOld) {
		InfluenceAnswerImageView newAnswerView = new InfluenceAnswerImageView(presenter.showResults);
		newAnswerView.setAnswer(newAnswerWrap);
		newAnswerView.setFromUser(this.presenter.getUserId(), this.presenter.getUserName(), new Date());
		if(!appendOld)
		{ // do not delete if it should also be appended
			answerImageViews.remove(oldAnswerView);
			answersPerAnswerView.remove(oldAnswerView);
		}
		answerImageViews.add(newAnswerView);
		answersPerAnswerView.put(newAnswerView, newAnswer);
		this.presenter.replaceAnswerViews(oldAnswerView, newAnswerView, appendOld, true);
		appendItemAdministration(presenter.getUserId(), presenter.userIsAdmin());
	}

	private InfluenceAnswer createReceivedAnswer(JSONObject object) {
		String message = object.get("message").isString().stringValue();
		long id = (long) object.get("id").isNumber().doubleValue();
		long ownerid = this.presenter.getUserId();
		String ownerName = this.presenter.getUserName();
		return new InfluenceAnswer(id, message, false, ownerid, ownerName, 0, 2);
	}

	@Override
	public void clearAnsweres() {
		answerImageViews.clear();
		answersPerAnswerView.clear();
	}

	@Override
	public Map<InfluenceAnswerViewInterface<AnswerImage>, InfluenceAnswer> getAnswerMap() {
		return answersPerAnswerView;
	}

	@Override
	public String[] getValidFileEndings() {
		return VALID_ENDINGS;
	}
}
