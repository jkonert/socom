package de.tud.kom.socom.web.client.influence.text;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import de.tud.kom.socom.web.client.administration.itemadministration.ItemAdministration;
import de.tud.kom.socom.web.client.administration.itemadministration.ItemAdministrationState;
import de.tud.kom.socom.web.client.eventhandler.FormSubmitCompleteEventHandler;
import de.tud.kom.socom.web.client.eventhandler.FormSubmitEventHandler;
import de.tud.kom.socom.web.client.events.FormSubmitEvent;
import de.tud.kom.socom.web.client.events.FormValueChangeEvent;
import de.tud.kom.socom.web.client.events.FormValueChangeEvent.ChangeType;
import de.tud.kom.socom.web.client.influence.InfluencePresenter;
import de.tud.kom.socom.web.client.reporting.ItemReportView;

/** This class will never fire an FormSubmitCompleteEvent as it only can fire FormSubmitEvents on case the "add" button is clicked
 * @author jkonert
 *
 */
public class InfluenceAnswerFreeTextView extends InfluenceAnswerTextView implements InfluencePresenter.InfluenceFreeAnswerViewInterface<String> {

	@UiTemplate("InfluenceAnswerTextView.ui.xml")  // same! template like TextAnswer without Free
	interface InfluenceAnswerFreeTextViewUiBinder extends UiBinder<Widget, InfluenceAnswerFreeTextView> { }
	
	private static InfluenceAnswerFreeTextViewUiBinder uiBinder = GWT.create(InfluenceAnswerFreeTextViewUiBinder.class);			
	
	// @UiField  inherited plus:	
	@UiField TextBox answerTextBox;
	@UiField Button addTextButton;
	
	@UiField protected ItemAdministration itemAdministration;
	@UiField protected ItemReportView itemReportView;
	
	String oldTextBoxValue;
	public static String defaultTextBoxValue;

	/** 
	 * @param isResultView  if set true, this view displays itself as a result view and does not report/display any inputs for a form
	 * @param expectedAnswerLength  number of chars expected for answer length (helps view to layout the input properly)
	 */
	public InfluenceAnswerFreeTextView(boolean isResultView, int expectedAnswerLength) 
	{				
		super(isResultView);

		this.answerLabel.addClassName("hidden");
		this.itemReportView.addStyleName("hidden");
		this.answerTextBox.removeStyleName("hidden");
		this.addTextButton.removeStyleName("hidden");
		if (expectedAnswerLength <= 10) expectedAnswerLength = 10;
		else if (expectedAnswerLength > 130) expectedAnswerLength = 130;
		this.answerTextBox.getElement().getStyle().setProperty("width", expectedAnswerLength, Unit.EX);
		this.oldTextBoxValue = this.answerTextBox.getValue();
		defaultTextBoxValue = this.oldTextBoxValue;
	}
	
	@Override
	void initWidget()
	{
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	@UiHandler("answerTextBox")
	public void onFocusTextBox(FocusEvent event)
	{
		if (this.answerTextBox.getValue().equals(defaultTextBoxValue))
		{
			this.answerTextBox.setValue("");
		}
	}
	
	@UiHandler("answerTextBox")
	public void onBlurTextBox(BlurEvent event)
	{
		if (this.answerTextBox.getValue().equals(""))
		{
			this.answerTextBox.setValue(defaultTextBoxValue);
		}
	}
	
	@UiHandler("answerTextBox")
	public void onValueChangeTextBox(KeyUpEvent event)
	{
		// update the label & userinfo
		// if first typing try to select this field automatically and if empty deselect automatically
		// all done by presenter
		checkAndProcessTextBoxValueChange();		
	}
	
	@UiHandler("addTextButton")
	public void onClickAddTextButton(ClickEvent event)
	{		
		this.fireEvent(new FormSubmitEvent(this));
		
	}
	
	private void checkAndProcessTextBoxValueChange() {
		if (oldTextBoxValue  == answerTextBox.getValue()) return;				
		this.fireEvent(new FormValueChangeEvent(this, ChangeType.message, answerCheckbox.getValue(), oldTextBoxValue, answerTextBox.getValue(), null));
		oldTextBoxValue = answerTextBox.getValue();
	}
	
	@Override
	public void setAnswer(String answer) {
		super.setAnswer(answer);
		if (answer == null || answer.equals("")) answerLabel.addClassName("hidden"); // maybe animate this..
	}

	
	@Override
	public void setFromUser(long userId, String userName, Date created) {
		super.setFromUser(userId, userName, created);
		if (created == null && (userName == null || userName.equals("")) && userId == -1) fromUserContainer.addClassName("hidden");
	}

	@Override
	public String getFreeAnswerValue() {
		return this.answerTextBox.getValue();
	}

	/**
	 *  does not really make sense to call/add here Hander, as FreeAnswers text are not uploaded via forms, but registered via ChangeEvents
	 */
	@Override
	public HandlerRegistration addFormSubmitHandler(
		FormSubmitEventHandler handler) {
		return this.addHandler(handler, de.tud.kom.socom.web.client.events.FormSubmitEvent.TYPE);
	}

	/**
	 *  does not really make sense to call/add here Hander, as FreeAnswers text are not uploaded via forms, but registered via ChangeEvents
	 */
	@Override
	public HandlerRegistration addFormSubmitCompleteHandler(
		FormSubmitCompleteEventHandler handler) {
		return this.addHandler(handler, de.tud.kom.socom.web.client.events.FormSubmitCompleteEvent.TYPE);
	}

	@Override
	public void disableAddAnswer() {
		this.addTextButton.setVisible(false);
	}
	
	@Override
	public void setAdministrationState(ItemAdministrationState state, ItemAdministration.DeleteButtonCallback cb) {
		itemAdministration.setState(ItemAdministrationState.none, null);
	}

	@Override
	public void setAdministrationState(ItemAdministrationState state) {
		itemAdministration.setState(ItemAdministrationState.none, null);
	}
}
