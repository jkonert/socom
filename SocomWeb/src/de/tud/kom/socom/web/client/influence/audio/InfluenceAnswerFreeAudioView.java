package de.tud.kom.socom.web.client.influence.audio;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import de.tud.kom.socom.web.client.administration.itemadministration.ItemAdministration;
import de.tud.kom.socom.web.client.eventhandler.FormSubmitCompleteEventHandler;
import de.tud.kom.socom.web.client.eventhandler.FormSubmitEventHandler;
import de.tud.kom.socom.web.client.events.FormSubmitCompleteEvent;
import de.tud.kom.socom.web.client.events.FormSubmitEvent;
import de.tud.kom.socom.web.client.events.FormValueChangeEvent;
import de.tud.kom.socom.web.client.events.FormValueChangeEvent.ChangeType;
import de.tud.kom.socom.web.client.influence.InfluencePresenter;
import de.tud.kom.socom.web.client.reporting.ItemReportView;
import de.tud.kom.socom.web.client.sharedmodels.SimpleEntry;

/** 
 * @author jkonert
 *
 */
public class InfluenceAnswerFreeAudioView extends InfluenceAnswerAudioView implements InfluencePresenter.InfluenceFreeAnswerWithFileViewInterface<AnswerAudio> {

	@UiTemplate("InfluenceAnswerAudioView.ui.xml")  // same! template like TextAnswer without Free
	interface InfluenceAnswerFreeTextViewUiBinder extends UiBinder<Widget, InfluenceAnswerFreeAudioView> { }
	
	private static InfluenceAnswerFreeTextViewUiBinder uiBinder = GWT.create(InfluenceAnswerFreeTextViewUiBinder.class);			
	
	// @UiField  inherited plus:
	@UiField TextBox answerTextBox;
	@UiField FormPanel answerForm; // needed to submit the file
	@UiField HTMLPanel answerFormHiddenContainer; // needed to add/remove some Hidden Inputs for submit
	@UiField FileUpload answerFileUpload;  // do NOT give the FileUpload and Form a hidden IFRAME yourself. They do it themselves
	@UiField Button answerFileUploadSubmit;
	
	@UiField protected ItemAdministration itemAdministration;
	@UiField protected ItemReportView itemReportView;
	
	String oldTextBoxValue;
	String oldFileUploadValue;

	private List<Hidden> hiddenParams; // a temprorary memory of items added to form..will be removed on next submit before new params are added
	

	/** 
	 * @param isResultView  if set true, this view displays itself as a result view and does not report/display any inputs for a form
	 * @param expectedAnswerLength  number of chars expected for answer length (helps view to layout the input properly)
	 */
	public InfluenceAnswerFreeAudioView(boolean isResultView, int expectedAnswerLength) 
	{				
		super(isResultView);
		if (!isResultView)
		{		
			this.answerLabel.addClassName("hidden");
			this.itemReportView.addStyleName("hidden");
			this.audioControl.addStyleName("hidden");
			this.answerForm.removeStyleName("hidden");
			this.itemAdministration.addStyleName("hidden");
			if (expectedAnswerLength <= 10) expectedAnswerLength = 10;
			else if (expectedAnswerLength > 130) expectedAnswerLength = 130;
			this.answerTextBox.getElement().getStyle().setProperty("width", expectedAnswerLength, Unit.EX);
			this.oldTextBoxValue = this.answerTextBox.getValue();
			this.oldFileUploadValue = this.answerFileUpload.getFilename();	
			this.setSubmitEnabled((this.oldFileUploadValue != null && !this.oldFileUploadValue.equals("")));		
			this.answerFileUpload.setName("data"); // not possible via XML...but server looks for this field
		}
	}
	
	@Override
	void initWidget()
	{
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	
	@UiHandler("answerFileUpload")
	public void onValueChangeFileUpload(ChangeEvent event)
	{
		// nothing. do it on submit click..
		String value = this.answerFileUpload.getFilename();		
		this.setSubmitEnabled(( value!= null && value != ""));
	}
	
	@UiHandler("answerFileUploadSubmit")
	public void onFileSubmitButtonClicked(ClickEvent event)
	{
		if (oldFileUploadValue  == answerFileUpload.getFilename()) return;				
		this.fireEvent(new FormValueChangeEvent(this, ChangeType.file, answerCheckbox.getValue(), oldFileUploadValue, answerFileUpload.getFilename(), null));
		oldFileUploadValue = answerFileUpload.getFilename();
		this.setSubmitEnabled((oldFileUploadValue  != answerFileUpload.getFilename()));
	}
	
	@UiHandler("answerForm")
	public void onFileFormSubmitComplete(SubmitCompleteEvent event)
	{
		this.fireEvent(new FormSubmitCompleteEvent(this, event.getResults()));
	}
	
	@UiHandler("answerForm")
	public void onFileFormSubmitComplete(SubmitEvent event)
	{
		this.fireEvent(new FormSubmitEvent(this));
	}
	
	@UiHandler("answerTextBox")
	public void onValueChangeTextBox(KeyUpEvent event)
	{
		// update the label & userinfo
		// if first typing try to select this field automatically and if empty deselect automatically
		// all done by presenter
		checkAndProcessTextBoxValueChange();		
	}
	
	private void checkAndProcessTextBoxValueChange() {
		if (oldTextBoxValue  == answerTextBox.getValue()) return;				
		this.fireEvent(new FormValueChangeEvent(this, ChangeType.message, answerCheckbox.getValue(), oldTextBoxValue, answerTextBox.getValue(), null));
		oldTextBoxValue = answerTextBox.getValue();
	}
	
	@Override
	public void setAnswer(AnswerAudio answer) {
		super.setAnswer(answer);
		if (answer == null) answerLabel.addClassName("hidden"); // maybe animate this..
	}
	
	@Override
	public void setFromUser(long userId, String userName, Date created) {
		super.setFromUser(userId, userName, created);
		if (created == null && (userName == null || userName.equals(""))) fromUserContainer.addClassName("hidden");
	}

	@Override
	public AnswerAudio getFreeAnswerValue() {
		return new AnswerAudio(this.audioControl.getFilePath(),this.answerTextBox.getValue()); // TODO JK check if the path is not better read from input..
	}

	/** 
	 * the submitted binary-data will be called "data" field.  Thus prevent overriding this by given params..
	 */
	@Override
	public void submitFile(SafeUri action, SimpleEntry<String, String>... hiddenParameters) {
		submitFile(ChangeType.file, action, hiddenParameters);
	}
	

	@Override
	public void submitFile(ChangeType type, SafeUri action, SimpleEntry<String, String>... hiddenParameters) {
		this.answerForm.setAction(action);
		if (this.hiddenParams != null)
		{// remove old params from former upload..
			for(Hidden h: this.hiddenParams)
			{
				this.answerFormHiddenContainer.remove(h);
			}
			this.hiddenParams = null;			
		}
		// add new hidden params if given
		if (hiddenParameters != null)
		{
			this.hiddenParams = new LinkedList<Hidden>();
			for (int i = 0; i< hiddenParameters.length; i++)
			{
				SimpleEntry<String, String> e = hiddenParameters[i];
				Hidden ip = new Hidden(e.getKey(), e.getValue());
				hiddenParams.add(ip);
				this.answerFormHiddenContainer.add(ip);
			}
		}
		this.answerForm.submit();
	}
	
	@Override
	public void setSubmitEnabled(boolean enabled) {
		this.answerFileUploadSubmit.setEnabled(enabled);
	}

	@Override
	public HandlerRegistration addFormSubmitHandler(
		FormSubmitEventHandler handler) {
		return this.addHandler(handler, de.tud.kom.socom.web.client.events.FormSubmitEvent.TYPE);
	}

	@Override
	public HandlerRegistration addFormSubmitCompleteHandler(
		FormSubmitCompleteEventHandler handler) {
		return this.addHandler(handler, de.tud.kom.socom.web.client.events.FormSubmitCompleteEvent.TYPE);
	}

	@Override
	public void disableAddAnswer() {
		// ignore since we have no "add new answer"-Button here
	}
}
