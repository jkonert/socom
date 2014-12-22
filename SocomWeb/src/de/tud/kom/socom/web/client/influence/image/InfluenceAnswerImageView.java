package de.tud.kom.socom.web.client.influence.image;

import java.util.Date;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import de.tud.kom.socom.web.client.administration.itemadministration.ItemAdministration;
import de.tud.kom.socom.web.client.administration.itemadministration.ItemAdministration.ChangeVisibilityButtonCallback;
import de.tud.kom.socom.web.client.administration.itemadministration.ItemAdministration.DeleteButtonCallback;
import de.tud.kom.socom.web.client.administration.itemadministration.ItemAdministrationState;
import de.tud.kom.socom.web.client.eventhandler.FormValueChangeEventHandler;
import de.tud.kom.socom.web.client.events.FormValueChangeEvent;
import de.tud.kom.socom.web.client.events.FormValueChangeEvent.ChangeType;
import de.tud.kom.socom.web.client.influence.InfluencePresenter;
import de.tud.kom.socom.web.client.reporting.ItemReportView;
import de.tud.kom.socom.web.client.reporting.ItemReportView.SendReportCallback;
import de.tud.kom.socom.web.client.util.DateTimeUtils;
import de.tud.kom.socom.web.client.util.FileUtils.FileState;

/** 
 * @author rhaban
 *
 */
public class InfluenceAnswerImageView extends Composite implements InfluencePresenter.InfluenceAnswerViewInterface<AnswerImage>{

	private static final String COLOR_DEFAULT = "#787CBF";
	private static final String COLOR_SELECTED = "#2EB800";
	private static final String COLOR_HOVER = "#289E00";

	@UiTemplate("InfluenceAnswerImageView.ui.xml")
	interface InfluenceAnswerViewUiBinder extends UiBinder<Widget, InfluenceAnswerImageView> { }
	
	private static InfluenceAnswerViewUiBinder uiBinder = GWT.create(InfluenceAnswerViewUiBinder.class);			
	
	@UiField Label answerLabel;
	@UiField FocusPanel img;
	@UiField SpanElement textbox;

	//fullscreen image stuff
	@UiField Label magnifier; //lupe
	@UiField DivElement fullscreen;
	@UiField FocusPanel fullscreenimg;
	@UiField DivElement fullscreenimgcontainer;
	@UiField DivElement engreyimg;
	
	//result bar
	@UiField DivElement resultBar;
	@UiField SpanElement number;
	@UiField SpanElement total;
	@UiField DivElement bar;
	@UiField DivElement percent;
	
	//from user ..
	@UiField DivElement fromUserContainer;
	@UiField SpanElement fromTime;
	@UiField InlineHyperlink fromLink;
	
	//admin&report stuff
	@UiField ItemReportView itemReportView;
	@UiField ItemAdministration itemAdministration;
	
	protected  boolean isResultView;
	// central boolean if element is selected!
	private boolean isSelected;
	// if element should be selectable
	private boolean votable;
	//current answer
	private AnswerImage answer;

	/** 
	 * @param isResultView if set true, this view displays itself as a result view and does not report/display any inputs for a form
	 */
	public InfluenceAnswerImageView(boolean isResultView) 
	{				
		initWidget();
		itemAdministration.changeToOnImageView();
		// initialize form elements 			
		switchResultView(isResultView);
		isSelected = false;
		votable = true;
	}

	/** 
	 * can be overridden by childclass
	 */
	void initWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public HandlerRegistration addValueChangeHandler(FormValueChangeEventHandler handler) {
		return addHandler(handler, FormValueChangeEvent.TYPE);
	}

	@Override
	public void setAdministrationState(ItemAdministrationState state, DeleteButtonCallback cb) {
		itemAdministration.setState(state,cb);
		disableAnswer(state == ItemAdministrationState.undelete);
	}

	@Override
	public void setAdministrationState(ItemAdministrationState state) {
		itemAdministration.setState(state);
		disableAnswer(state == ItemAdministrationState.undelete);
	}

	private void disableAnswer(boolean disable) {
		if(disable)
		{
			engreyimg.removeClassName("hidden");
		} 
		else
		{
			engreyimg.addClassName("hidden");
		}
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
	public void setSendReportCallback(SendReportCallback cb) {
		if(itemReportView != null)
			itemReportView.setSendReportCallback(cb);
	}

	@Override
	public void setAnswer(AnswerImage answer) {
		this.answer = answer;
		String message = answer.getMessage();
		if(message.length() > 23) message = message.substring(0, 21) + "..";
		answerLabel.setText(message);
		img.getElement().getStyle().setProperty("background",
				"url(\"./data/influence_data/" + answer.getPath() + "\") no-repeat scroll center center / 250px auto #787CBF");
	}

	@Override
	public void setFromUser(long userId, String userName, Date created) {
			this.fromLink.setTargetHistoryToken("./profiles/"+userId);
			this.fromLink.setText(SafeHtmlUtils.htmlEscape(userName));
			this.fromTime.setInnerText(DateTimeUtils.toShortStringRelative(created));
			this.fromUserContainer.removeClassName("hidden");
	}

	@Override
	public void setResultInformation(int totalnumberOfVotes, int numberOfVotes, boolean isWinner) {
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

	@Override
	public boolean isSelected() {
		return isSelected;
	}

	@Override
	public void switchResultView(boolean showResultView) {
		this.isResultView = showResultView;
		if (isResultView)
		{
			resultBar.removeClassName("hidden");
		}
		else
		{
			resultBar.addClassName("hidden");
		}
	}

	@Override
	public FileState getReadyState() {
		if (this.img.getElement().getStyle().getBackgroundImage().isEmpty())
		{
			return FileState.uninitialized;
		}
		else return FileState.success;
	}

	@Override
	public void setVotable(boolean votable) {
		this.votable = votable;
		if(!votable)
		{
			img.getElement().getStyle().setProperty("cursor", "default");
			img.setTitle("Nicht wählbar");
		}
	}

	@Override
	public void animate() {
		// TODO
	}

	public void setSelected(boolean select) {
		if(!votable || isSelected == select) return;
		isSelected = select;
		highlightIfSelected();
		this.fireEvent(new FormValueChangeEvent(this, ChangeType.select, isSelected, null, null, null));
	}
	
	@UiHandler("img")
	public void onClickImage(ClickEvent event)
	{
		if(!votable) return;
		if(isResultView)
		{
			onClickMagnifier(null);
			return;
		}
		isSelected = !isSelected;
		highlightIfSelected();
		this.fireEvent(new FormValueChangeEvent(this, ChangeType.select, isSelected, null, null, null));
	}

	@UiHandler("img")
	public void onHoverImage(MouseOverEvent mov) {
		img.getElement().getStyle().setProperty("border", "thick solid " + COLOR_HOVER);
		textbox.getStyle().setProperty("background",COLOR_HOVER);
	}
	
	@UiHandler("img")
	public void onHoverImage(MouseOutEvent mov) {
		highlightIfSelected();
	}
	
	@UiHandler("magnifier")
	public void onClickMagnifier(ClickEvent event)
	{
		final Style bgstyle = fullscreen.getStyle();
		final Style bgimgcontainerstyle = fullscreenimgcontainer.getStyle();
		final Style bgimgstyle = fullscreenimg.getElement().getStyle();
		
		bgstyle.setVisibility(Visibility.VISIBLE);
		bgimgcontainerstyle.setVisibility(Visibility.VISIBLE);
		bgimgstyle.setVisibility(Visibility.VISIBLE);
		if(bgimgstyle.getBackgroundImage() == null || bgimgstyle.getBackgroundImage().trim().isEmpty())
		{
			bgimgstyle.setProperty("background",
				"url(\"./data/influence_data/" + answer.getPath() + "\") no-repeat scroll center center / contain rgba(0,0,0,0)");
			bgimgstyle.setWidth(100, Unit.PCT);
			bgimgstyle.setHeight(90, Unit.PCT);
		}
		
		new Animation() {
			@Override
			protected void onUpdate(double progress) {
				bgstyle.setOpacity(Math.min(0.9, progress*2));
				bgimgstyle.setOpacity(Math.max(0, progress*2-0.8));
			}
		}.run(1600);
	}
	
	@UiHandler("fullscreenimg")
	public void onFullscreenImgClicked(ClickEvent e) {
		final Style bgstyle = fullscreen.getStyle();
		final Style bgimgstyle = fullscreenimg.getElement().getStyle();
		new Animation() {
			@Override
			protected void onUpdate(double progress) {
				bgstyle.setOpacity(1-progress);
				bgimgstyle.setOpacity(Math.max(0, 1-progress*2.5));
				if(progress == 1) 
				{
					fullscreen.getStyle().setVisibility(Visibility.HIDDEN);
					fullscreenimg.getElement().getStyle().setVisibility(Visibility.HIDDEN);		
					fullscreenimgcontainer.getStyle().setVisibility(Visibility.HIDDEN);
				}
			}
		}.run(1000);
	}
	
	private void highlightIfSelected() {
		img.getElement().getStyle().setProperty("border", "thick solid " + (isSelected?COLOR_SELECTED:COLOR_DEFAULT));
		textbox.getStyle().setProperty("background", (isSelected?COLOR_SELECTED:COLOR_DEFAULT));
	}

}
