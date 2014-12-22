package de.tud.kom.socom.web.client.influence;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

import de.tud.kom.socom.web.client.influence.InfluenceListItemCellView.InfluenceRessources.Styles;
import de.tud.kom.socom.web.client.influence.InfluencePresenter.MODE;
import de.tud.kom.socom.web.client.util.DateTimeUtils;

public class InfluenceListItemCellView extends Composite
{

	private static InfluenceListItemCellViewUiBinder uiBinder = GWT.create(InfluenceListItemCellViewUiBinder.class);

	@UiTemplate("InfluenceListItemCellView.ui.xml") // BTW: this line is obsolete if name of template file matches interface name
	interface InfluenceListItemCellViewUiBinder extends UiBinder<Element, InfluenceListItemCellView> {	}	
	
	@UiField DivElement outerFrame;
	@UiField DivElement imageTeaser;
	@UiField DivElement question;
	@UiField SpanElement gameName;
	@UiField SpanElement playerName;
	@UiField SpanElement currentAnswersPrefix;
	@UiField SpanElement numAnswers;
	@UiField SpanElement timeLeft;

	@UiField Styles style;

	
	// resources for CSS in XML file.
	public interface InfluenceRessources extends ClientBundle
	{			
		/**  //unused...directly referenced in XML...thus wrapping interface is redundant as well..
		@Source("icon-aud20t.png")
		ImageResource iconAudio();
		
		@Source("icon-txt20t.png")
		ImageResource iconText();
		
		@Source("icon-img20t.png")
		ImageResource iconImage();
		
		**/
		
		// mapped by UIBinder
		interface Styles extends CssResource {
		    String odd();
		    String even();
		    String txt();
		    String img();
		    String aud();
		  }
		
	}
	
	
	
	private static String imageTeaserDefaultClasses;

	public InfluenceListItemCellView()
	{		
		setElement(uiBinder.createAndBindUi(this));	
		imageTeaserDefaultClasses = imageTeaser.getClassName();
	}
	
	public void setDataAndRender(int listPos, String imageTeaserStyleType, String question, String gameName, String playerName, boolean stillRunning, int numAnswersGiven, Date timeout, SafeHtmlBuilder out)
	{
		if ((listPos%2) == 0)
		{
			outerFrame.addClassName(style.even());
			outerFrame.removeClassName(style.odd());
		}
		else
		{
			outerFrame.addClassName(style.odd());
			outerFrame.removeClassName(style.even());
		}
		this.imageTeaser.setClassName(imageTeaserDefaultClasses);
		try
		{
			MODE m = InfluencePresenter.MODE.valueOf(imageTeaserStyleType);
			switch(m)
			{
				case audio: imageTeaserStyleType = style.aud();
				break;
				case image: imageTeaserStyleType = style.img();
				break;
				case text: imageTeaserStyleType = style.txt();
				break;
				default: imageTeaserStyleType = "none-unimplemented";
			}
		}
		catch (Exception e)
		{
			imageTeaserStyleType = "none";
		}		
		this.imageTeaser.addClassName(imageTeaserStyleType);
		
		this.question.setInnerText("Loading...");
		int maxWidth = this.question.getClientWidth();
		int maxHeight = this.question.getClientHeight(); // make sure it only is one line..
		this.question.setInnerText("\""+question+"\"");
		while ((this.question.getClientWidth() > maxWidth || this.question.getClientHeight() > maxHeight) && question.length() > 0)
		{// crop question until it fits the box
			question = question.substring(0, question.length()-1);
			this.question.setInnerText("\""+question+"..\"");
		}
		this.gameName.setInnerText(gameName);
		this.playerName.setInnerText(playerName);
		if (!stillRunning) currentAnswersPrefix.addClassName("hidden");
		else currentAnswersPrefix.removeClassName("hidden");
		this.numAnswers.setInnerText(String.valueOf(numAnswersGiven));
		this.timeLeft.setInnerText(DateTimeUtils.toShortStringRelative(timeout));
		// render 
		out.append(SafeHtmlUtils.fromTrustedString(this.getElement().getString()));
	}

}
