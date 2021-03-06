package de.tud.kom.socom.web.client.influence.text;

import java.util.Date;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.SimpleCheckBox;
import com.google.gwt.user.client.ui.Widget;

import de.tud.kom.socom.web.client.administration.itemadministration.ItemAdministration;
import de.tud.kom.socom.web.client.administration.itemadministration.ItemAdministration.ChangeVisibilityButtonCallback;
import de.tud.kom.socom.web.client.administration.itemadministration.ItemAdministrationState;
import de.tud.kom.socom.web.client.eventhandler.FormValueChangeEventHandler;
import de.tud.kom.socom.web.client.events.FormValueChangeEvent;
import de.tud.kom.socom.web.client.influence.InfluencePresenter;
import de.tud.kom.socom.web.client.reporting.ItemReportView;
import de.tud.kom.socom.web.client.reporting.ItemReportView.SendReportCallback;
import de.tud.kom.socom.web.client.util.DateTimeUtils;
import de.tud.kom.socom.web.client.util.FileUtils.FileState;

/** 
 * @author jkonert
 *
 */
public class InfluenceAnswerTextView extends Composite implements InfluencePresenter.InfluenceAnswerViewInterface<String> {

	@UiTemplate("InfluenceAnswerTextView.ui.xml")
	interface InfluenceAnswerViewUiBinder extends UiBinder<Widget, InfluenceAnswerTextView> { }
	
	private static InfluenceAnswerViewUiBinder uiBinder = GWT.create(InfluenceAnswerViewUiBinder.class);			
	
	@UiField protected SimpleCheckBox answerCheckbox;
	@UiField protected LabelElement answerLabel;
	@UiField protected DivElement fromUserContainer;
	@UiField protected SpanElement fromTime;
	@UiField protected AnchorElement fromLink;
	
	@UiField protected DivElement resultBar;
	@UiField protected SpanElement number;
	@UiField protected SpanElement total;
	@UiField protected DivElement bar;
	@UiField protected DivElement percent;
	
	@UiField protected ItemAdministration itemAdministration;
	@UiField protected ItemReportView itemReportView;
	
	protected boolean isResultView;
	protected boolean oldCheckboxValue;

	/** 
	 * @param isResultView if set true, this view displays itself as a result view and does not report/display any inputs for a form
	 */
	public InfluenceAnswerTextView(boolean isResultView) 
	{				
		initWidget();		
		//initialize form elements 	
		switchResultView(isResultView);
	}

	/** can be overridden by childclass
	 * 
	 */
	void initWidget() {
		initWidget(uiBinder.createAndBindUi(this));		
	}

	@UiHandler("answerCheckbox")
	public void onClickCheckbox(ClickEvent event)
	{
		checkAndProcessValueChange();		
	}
	
	private void checkAndProcessValueChange() {
		if (oldCheckboxValue  == answerCheckbox.getValue()) return;		
		oldCheckboxValue = answerCheckbox.getValue();
		this.fireEvent(new FormValueChangeEvent(this, true, answerCheckbox.getValue(), null, null));
	}
	
	@Override
	public void switchResultView(boolean showResultView) {
		this.isResultView = showResultView;
		if (!isResultView)
		{		
			if (answerCheckbox.getElement().getId() == null || answerCheckbox.getElement().getId() == "")
			{
				String id = HTMLPanel.createUniqueId();		
				// link them together
				answerCheckbox.getElement().setId(id); // works because we use a SimpleCheckBox
				answerLabel.setHtmlFor(id);
			}
			oldCheckboxValue = answerCheckbox.getValue();
			
			answerCheckbox.setEnabled(true);
			answerCheckbox.getElement().getStyle().setProperty("opacity", "1");
			resultBar.addClassName("hidden");
		}
		else	// in case this is a result display, just show results and hide form stuff
		{
			answerCheckbox.setEnabled(false);
			answerCheckbox.getElement().getStyle().setProperty("opacity", "0");
			resultBar.removeClassName("hidden");
		}
	}

	@Override
	public void setAnswer(String answer) {
		answerLabel.setInnerText(SafeHtmlUtils.htmlEscape(answer));		
	}
	
	@Override
	public void setSelected(boolean selectNew) {
		if(!answerCheckbox.isEnabled() || answerCheckbox.getValue() == selectNew) return;
		answerCheckbox.setValue(selectNew);
	}

	@Override
	public boolean isSelected() {
		return answerCheckbox.isEnabled() && answerCheckbox.getValue();
	}
	
	@Override
	public void setFromUser(long userId, String userName, Date created) {
		this.fromLink.setHref("#profiles/"+userId);
		// TODO JK: generate and fire history token to user
		this.fromLink.setInnerText(SafeHtmlUtils.htmlEscape(userName));
		if(created != null)
			this.fromTime.setInnerText(DateTimeUtils.toStringRelative(created));
		this.fromUserContainer.removeClassName("hidden");
	}

	@Override
	public HandlerRegistration addValueChangeHandler(
		FormValueChangeEventHandler handler) {
		return addHandler(handler, FormValueChangeEvent.TYPE);
	}

	@Override
	public void setResultInformation(int totalnumberOfVotes, int numberOfVotes,
		boolean isWinner) {
		switchResultView(true);
		double percent = 0d;
		if (totalnumberOfVotes != 0 && numberOfVotes != 0) percent = (((double)numberOfVotes)/(totalnumberOfVotes)*100d);
		percent = Math.round(percent*10)/10d;
		String percentString = String.valueOf(percent)+"%";
		this.percent.setInnerText(percentString);
		this.total.setInnerText(String.valueOf(totalnumberOfVotes));
		this.number.setInnerText(String.valueOf(numberOfVotes));
		this.bar.getStyle().setProperty("width", (int)Math.round(percent), Unit.PCT);
		if (isWinner) resultBar.addClassName("winner");
		else resultBar.removeClassName("winner");			
	}

	/** only returns 'uninitialized' if no text for answer is set or success in all other cases
	 * 
	 */
	@Override
	public FileState getReadyState() {
		if (this.answerLabel.getInnerText() == null || this.answerLabel.getInnerText().equals(""))
		{
			return FileState.uninitialized;
		}
		else return FileState.success;
	}

	/**
	 * state always describes the state of the button, so if the state is delete the answer is currently NOT deleted..
	 */
	@Override
	public void setAdministrationState(ItemAdministrationState state, ItemAdministration.DeleteButtonCallback cb) {
		itemAdministration.setState(state,cb);
		disableAnswer(state == ItemAdministrationState.undelete);
	}

	@Override
	public void setAdministrationState(ItemAdministrationState state) {
		itemAdministration.setState(state);
		disableAnswer(state == ItemAdministrationState.undelete);
	}

	private void disableAnswer(boolean disable) {
		if(disable) answerLabel.addClassName("disabledtext");
		else answerLabel.removeClassName("disabledtext");
	}

	@Override
	public void setVotable(boolean votable) {
		answerCheckbox.setEnabled(votable);
	}

	@Override
	public void setItemAdministrationVisibility(int visibility) {
		itemAdministration.setCurrentVisibility(visibility);
	}

	@Override
	public void setItemAdministrationVisibilityChangeCallback(ChangeVisibilityButtonCallback cb) {
		itemAdministration.setVisibilityCallback(cb);
	}
	
	@Override
	public void enableReporting(boolean enable) {
		if(enable)
			this.itemReportView.removeStyleName("hidden");
		else
			this.itemReportView.addStyleName("hidden");
	}

	@Override
	public void animate() {
		Animation a = new Animation() {
			@Override
			protected void onUpdate(double progress) {
				Composite c = InfluenceAnswerTextView.this;
				c.getElement().getStyle().setProperty("opacity", String.valueOf(progress));
			}
		};
		a.run(3000);
	}

	@Override
	public void setSendReportCallback(SendReportCallback cb) {
		if(itemReportView != null)
			itemReportView.setSendReportCallback(cb);
	}
}