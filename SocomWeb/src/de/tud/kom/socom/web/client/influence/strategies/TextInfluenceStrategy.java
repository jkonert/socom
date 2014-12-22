package de.tud.kom.socom.web.client.influence.strategies;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.Widget;

import de.tud.kom.socom.web.client.AppController;
import de.tud.kom.socom.web.client.baseelements.viewerrors.ErrorInfluenceFreeAnswerCountView;
import de.tud.kom.socom.web.client.baseelements.viewerrors.ErrorInfluenceFreeAnswerLengthView;
import de.tud.kom.socom.web.client.baseelements.viewerrors.ErrorView;
import de.tud.kom.socom.web.client.influence.InfluencePresenter;
import de.tud.kom.socom.web.client.influence.InfluencePresenter.InfluenceAnswerViewInterface;
import de.tud.kom.socom.web.client.influence.InfluencePresenter.InfluenceFreeAnswerViewInterface;
import de.tud.kom.socom.web.client.influence.text.InfluenceAnswerFreeTextView;
import de.tud.kom.socom.web.client.influence.text.InfluenceAnswerTextView;
import de.tud.kom.socom.web.client.sharedmodels.InfluenceAnswer;
import de.tud.kom.socom.web.client.sharedmodels.SimpleEntry;

public class TextInfluenceStrategy extends InfluenceStrategy<String> {

	private InfluencePresenter presenter;
	private List<InfluenceAnswerViewInterface<String>> answersText;
	private Map<InfluenceAnswerViewInterface<String>, InfluenceAnswer> answers;
	private int freeAnswerFormCount, maxDigits, maxLines;
	private ErrorView answerLengthError;
	

	public TextInfluenceStrategy(AppController appController, InfluencePresenter influencePresenter) {
		super(appController);
		presenter = influencePresenter;
		maxDigits = presenter.getInfluenceObject().getMaxDigits();
		maxLines = presenter.getInfluenceObject().getMaxLines();
		answersText = new LinkedList<InfluenceAnswerViewInterface<String>>();
		answers = new HashMap<InfluenceAnswerViewInterface<String>, InfluenceAnswer>();
		freeAnswerFormCount = 1;
	}

	@Override
	public void addPredefinedAnswer(InfluenceAnswer answer) {
		//in sql fetch already checked if deleted answers are also welcome in case of admin is logged in..
		InfluenceAnswerTextView textanswer = new InfluenceAnswerTextView(presenter.showResults);
		textanswer.setAnswer(answer.getAnswer());
		if (presenter.showResults) {
			textanswer.setResultInformation(presenter.getTotalNumberOfVotes(), answer.getResult().getVotes(), presenter.getWinner().equals(answer));
		}
		answersText.add(textanswer);
		answers.put(textanswer, answer);
		textanswer.enableReporting(false);
	}

	@Override
	public InfluenceAnswerViewInterface<String> addFreeAnswer(InfluenceAnswer answer, boolean votable) {
		InfluenceAnswerTextView textanswer = new InfluenceAnswerTextView(presenter.showResults);
		textanswer.setAnswer(answer.getAnswer());
		textanswer.setFromUser(answer.getOwnerId(), answer.getOwnerName(), answer.getResult().getTimestamp());
		if (presenter.showResults) {
			textanswer.setResultInformation(presenter.getTotalNumberOfVotes(), answer.getResult().getVotes(), presenter.getWinner().equals(answer));
		}
		textanswer.setVotable(votable);
		answersText.add(textanswer);
		answers.put(textanswer, answer);
		textanswer.enableReporting(presenter.getUserId() != -1 && presenter.getUserId() != answer.getOwnerId());
		return textanswer;
	}

	@Override
	public void addFreeAnswerForm() {
		if (presenter.showResults)
			return;
		InfluenceAnswerFreeTextView freetextanswer = new InfluenceAnswerFreeTextView(presenter.showResults, 60);
		answersText.add(freetextanswer);
		//TODO visibility
		answers.put(freetextanswer, new InfluenceAnswer(this.presenter.getUserId(), true, 2));
		updateFreeAnswerOnChange(freetextanswer, freetextanswer.getFreeAnswerValue());
		if(this.presenter.maxOptions == 1) // only one answer allowed
			freetextanswer.disableAddAnswer();
	}

	@Override
	public List<InfluenceAnswerViewInterface<String>> getPossibleAnswers() {
		return answersText;
	}

	@Override
	public List<InfluenceAnswer> collectSelectedAnswers(List<InfluenceAnswerViewInterface<String>> selected) {
		List<InfluenceAnswer> selectedAnswers = new LinkedList<InfluenceAnswer>();
		for (InfluenceAnswerViewInterface<String> formelem : selected) {
			InfluenceAnswer a = this.answers.get(formelem);
			if (a != null) {
				if (a.isNewFreeAnswer()) {
					if(a.getAnswer().length() > maxDigits){
						presenter.showError(answerLengthError);
						return null;
					}
					a.setOwnerId(presenter.getUserId());
				}
				selectedAnswers.add(a);
			}
		}
		if (selectedAnswers.size() < presenter.minOptions || selectedAnswers.size() > presenter.maxOptions) {
			presenter.showErrorMinMax(selectedAnswers.size());
			return null;
		}
		return selectedAnswers;
	}

	@Override
	public InfluenceAnswerViewInterface<String> findParentViewForElement(Widget formOptionWidget) {
		for (InfluenceAnswerViewInterface<String> a : answersText) {
			if (a.equals(formOptionWidget))
				return a;
		}
		return null;
	}

	@Override
	public void updateFreeAnswerOnChange(InfluenceFreeAnswerViewInterface<String> parent, String message) {
		if(answerLengthError != null) {
			presenter.hideError(answerLengthError);
			answerLengthError = null;
			presenter.enableSubmit(true);
		}
		int currLength = message.length();
		if(currLength > maxDigits) {
			if(message.equals(InfluenceAnswerFreeTextView.defaultTextBoxValue)) return;
			presenter.showError((answerLengthError = new ErrorInfluenceFreeAnswerLengthView(maxDigits, currLength)));
			presenter.enableSubmit(false);
		}
		if(currLength > 0) parent.setSelected(true);
		answers.get(parent).setAnswer(message);
	}

	@Override
	public void updateFreeAnswerOnUploadSuccess(InfluenceFreeAnswerViewInterface<String> answer, SimpleEntry<String, String>[] sourceParameter, JSONValue result) {
		answer.disableAddAnswer();
		// texts do not have an upload of stuff via form. ignore
		// new free answer button is something like "successfull upload", so we
		// use this function in this case
		if (this.presenter.maxOptions > this.freeAnswerFormCount) {
			this.addFreeAnswerForm();
			this.presenter.paintAnswers();
			this.freeAnswerFormCount++;
		} else {
			this.presenter.showError(new ErrorInfluenceFreeAnswerCountView(this.presenter.maxOptions, this.freeAnswerFormCount));
		}
	}

	@Override
	public void clearAnsweres() {
		answersText.clear();
		answers.clear();
	}

	@Override
	public Map<InfluenceAnswerViewInterface<String>, InfluenceAnswer> getAnswerMap() {
		return answers;
	}

	@Override
	public boolean hasValidFileEnding(String filename) {
		return false;
	}

	@Override
	public String[] getValidFileEndings() {
		return null;
	}
}