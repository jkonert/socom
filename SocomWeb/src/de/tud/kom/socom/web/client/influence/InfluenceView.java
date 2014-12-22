package de.tud.kom.socom.web.client.influence;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.gargoylesoftware.htmlunit.javascript.host.Event;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.ParagraphElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

import de.tud.kom.socom.web.client.eventhandler.FormValueChangeEventHandler;
import de.tud.kom.socom.web.client.events.FormValueChangeEvent;
import de.tud.kom.socom.web.client.htmlelements.ErrorList;
import de.tud.kom.socom.web.client.htmlelements.ErrorListItemView;
import de.tud.kom.socom.web.client.influence.InfluencePresenter.InfluenceAnswerViewInterface;
import de.tud.kom.socom.web.client.influence.InfluencePresenter.InfluenceFreeAnswerViewInterface;
import de.tud.kom.socom.web.client.influence.administration.InfluenceAdministrationView;
import de.tud.kom.socom.web.client.influence.image.AnswerImage;
import de.tud.kom.socom.web.client.influence.image.InfluenceAnswerImageView;
import de.tud.kom.socom.web.client.util.DateTimeUtils;

/** 
 * @author jkonert
 *
 */
public class InfluenceView extends Composite implements InfluencePresenter.InfluenceViewInterface, FormValueChangeEventHandler {

	@UiTemplate("InfluenceView.ui.xml")
	interface InfluenceViewUiBinder extends UiBinder<Widget, InfluenceView> { }
	
	private static InfluenceViewUiBinder uiBinder = GWT.create(InfluenceViewUiBinder.class);		
	private InfluencePresenter presenter;
	
	@UiField HeadingElement question;
	@UiField DivElement timer;
	@UiField ParagraphElement time;
	@UiField FlexTable formElements;
	
	@UiField HTMLPanel submitWrapper;
	@UiField Button buttonSubmit;   
	
	@UiField ErrorList errorList;
	@UiField InfluenceAdministrationView adminView;
	
	private Map<InfluenceAnswerViewInterface<?>, HandlerRegistration[]> answers;
	private int columnsMax;
	private int currentRow;
	private int currentColumn;
	
	public InfluenceView(InfluencePresenter presenter) {
		this.presenter = presenter;
		this.answers = new HashMap<InfluenceAnswerViewInterface<?>,HandlerRegistration[]>();
		//InfluencePanel panel = new InfluencePanel(SocomWebEntryPoint.getInstance(), Location.getParameter("influence"));
		//initWidget(panel);
		
		initWidget(uiBinder.createAndBindUi(this));
//		this.adminView.setVisible(false);
		this.adminView.setPresenter(presenter);
		
		this.columnsMax = 1;
		this.currentRow = 0;
		this.currentColumn = 0;
	}
	
	
	@UiHandler("buttonSubmit")
	public void onButtonSubmit(ClickEvent event)
	{
		presenter.onSubmitForm();
	}
	
	@Override
	public void setAnswerColumns(int c) {
		this.columnsMax = c;
	}

	@Override
	public void showCountdown(Date timeout) {		
		if (timeout != null)
		{
			this.time.setInnerText(DateTimeUtils.toShortStringRelative(timeout));
			this.timer.removeClassName("hidden");
		}
		else
		{
			this.time.setInnerText("..");
			this.timer.addClassName("hidden");
		}
		
	}

	@Override
	public void setQuestion(String question) 
	{
		if (question != null && question.length() > 0)
//			this.question.setInnerText(SafeHtmlUtils.htmlEscape(addPunctationmarkAppendix(question)));
			this.question.setInnerText(addPunctationmarkAppendix(question));
		else this.question.setInnerText("");
		
	}
	
	private String addPunctationmarkAppendix(String question) {
		if (question == null || question.length() == 0) return "";
		question = question.trim();
		// check if question already has a PunctationMark at end, otherwise add a question mark (?)
		if ("?!.;,".indexOf(question.charAt(question.length()-1))==-1)
		{
			return question+"?";
		}
		return question;
	}


	@Override
	public <T> void setPossibleAnswers(List<InfluenceAnswerViewInterface<T>> answers) {
		this.currentColumn = this.currentRow = 0;
		//check stored answer
		for (InfluenceAnswerViewInterface<?> a: this.answers.keySet())
		{//if not already contained
			if (!answers.contains(a))
			{ // remove the element
				formElements.remove(a.asWidget());
			}
		}		
		for (InfluenceAnswerViewInterface<T> answer: answers)
		{
			if (!this.answers.containsKey(answer))
			{
				// only add to form, if not already there.  Then do as well listen for events and remember the handler ids.
				
				
				if(currentColumn == columnsMax) 
				{
					currentColumn = 0;
					currentRow++;
				}
				formElements.setWidget(this.currentRow, this.currentColumn++, answer.asWidget()); //next row next column
//				formElements.add(answer.asWidget());	
				registerViewHandler(answer);
			}
		}
	}
	
	@Override
	public <T> void appendAnswer(InfluenceAnswerViewInterface<T> answerView) {
		
		if(currentColumn == columnsMax)
		{
			currentColumn = 0;
			currentRow++;
		}
		formElements.setWidget(this.currentRow, this.currentColumn++, answerView.asWidget());
//		formElements.add(freeAnswerView.asWidget());

		registerViewHandler(answerView);
		
		answerView.animate();
	}


	private <T> void registerViewHandler(InfluenceAnswerViewInterface<T> answerView) {
		answerView.asWidget().sinkEvents(Event.CHANGE | Event.BLUR);
		HandlerRegistration[] handlers = new HandlerRegistration[0];
		this.answers.put(answerView, handlers);
	}
	
	@Override
	public void replaceAnswers(InfluenceFreeAnswerViewInterface<AnswerImage> oldAnswerView,
			InfluenceAnswerImageView newAnswerView, boolean appendOld, boolean selectNew) {
		 int col = 0, row = 0;
		 while(!formElements.getWidget(row, col).equals(oldAnswerView))
		 {
			 col++;
			 if(col == columnsMax)
			 {
				 col = 0;
				 row++;
			 }
		 }
		 registerViewHandler(newAnswerView);
		 
		 formElements.setWidget(row, col, newAnswerView);
		 if(appendOld) appendAnswer(oldAnswerView);
		 if(selectNew) newAnswerView.setSelected(selectNew);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<InfluenceAnswerViewInterface<T>> getSelectedAnswers() {
		List<InfluenceAnswerViewInterface<T>> result = new LinkedList<InfluenceAnswerViewInterface<T>>();
		for (InfluenceAnswerViewInterface<?> answer: answers.keySet())
		{
			if (answer.isSelected()) result.add((InfluenceAnswerViewInterface<T>)answer);  // the cast works as no other options can be passed into...
		}
		return result;
	}

	@Override
	public void disableSubmit() {
		this.buttonSubmit.setEnabled(false);
		
	}

	@Override
	public void enableSubmit() {
		this.buttonSubmit.setEnabled(true);		
	}


	@Override
	public void showSubmit(boolean show) {
		this.buttonSubmit.setVisible(show);
	}


	@Override
	public void onFormValueChangeEvent(FormValueChangeEvent event) {
		if (event.isSelectChange()) 
		{
//			Window.alert("Element own event FormValueChange catched");
			//inform presenter of a select/deselect
			return;
		}
//		Window.alert("Element own event FormValueChange catched a KeyxValue change to "+event.getValue());
		// what about text changes in ownanswers??
		
	}


	@Override
	public void showError(ErrorListItemView error) {
		this.errorList.addError(error);
	}

	@Override
	public void hideErrors() {
		this.errorList.clear();		
	}


	@Override
	public void hideError(ErrorListItemView error) {
		this.errorList.removeError(error);
		
	}
	
	@Override
	public void setAdministrationVisible(boolean visible) {
//		this.adminView.setVisible(visible);
		if(visible)
			this.adminView.removeStyleName("hidden");
		else
			this.adminView.addStyleName("hidden");
	}
}
