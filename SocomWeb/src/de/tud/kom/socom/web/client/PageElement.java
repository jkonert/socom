package de.tud.kom.socom.web.client;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RootPanel;

import de.tud.kom.socom.web.client.AppController.PageElementIDs;
import de.tud.kom.socom.web.client.events.ViewChangeOfPageElementEvent;



public class PageElement
{
	private static Map<PageElementIDs, PageElement> instances = new HashMap<PageElementIDs, PageElement>();
	
	private PageElementIDs elementID;
	private AppController appController;
	private RootPanel rootPanel;
	private IsWidget oldIsWidgetContent;

	private PageElement(AppController appController, PageElementIDs elementID, RootPanel rootPanel)
	{
		this.elementID = elementID;
		this.appController = appController;
		this.rootPanel = rootPanel;
		if (rootPanel.getWidgetCount() >0) this.oldIsWidgetContent = rootPanel.getWidget(0); // if there are more than 1, we ignore..
	}	
	

	public static PageElement getInstance(AppController appController, PageElementIDs elementID,
		RootPanel rootPanel) {
		PageElement instance = instances.get(elementID);
		// this works as it is single threaded anyway..otherwise synchronize!
		if (instance != null) return instance;
		PageElement newElement = new PageElement(appController,elementID, rootPanel);
		instances.put(elementID, newElement);
		return newElement;
	}

	
	/**
	 * 
	 * @param image can be null to clear the background-image, otherwise the new source
	 * @param autoFitting  if true the width and height of element is adjusted to fit the image; otherwise it is simply set as background
	 */
	public void setBackgroundImage(Image image, boolean autoFitting)
	{		
		if (autoFitting)
		{
			// do the same calculation of animate and fade as with content#
			// if no content exists use the image and current values; otherwise use the content values..?
			// TODO only set width/height if rootPanel does not have childs and content itself..
			int newH = (image==null)?0:image.getHeight();
			rootPanel.getElement().getStyle().setHeight(newH, Unit.PX);
		}
		// fade out..in
		String imageURL = (image == null)?"none":image.getUrl();
		rootPanel.getElement().getStyle().setBackgroundImage(imageURL);		
	}
	
	public void setContent(String innerHTML)
	{
		setContent(new HTMLPanel(SafeHtmlUtils.fromString(innerHTML)));
	}
	
	public void setContent(IsWidget widget)
	{
		
		appController.getEventHandler().fireEvent(new ViewChangeOfPageElementEvent(elementID, oldIsWidgetContent, widget));
		// decide fade or scroll animation
		// find out new to old value in sizes
		// trigger the animate start of old element
		// add new element
		// trigger animation of new element
		
		removeOldContent();
		
		if (widget != null) addNewContent(widget);
		else {}; //  maybe set whole element invisible?
		
		int oldH = rootPanel.getOffsetHeight();
		int oldW = rootPanel.getOffsetWidth();
		int newW= 0, newH = 0; 
		if (widget != null)
		{
			newH = widget.asWidget().getOffsetHeight();
			newW = widget.asWidget().getOffsetWidth();
		}
		if (oldH != newH && newH == 0)
		{
			// only scroll away
		}
		else if (oldH != newH)
		{
			// scroll to new Height, then fade out, fade in
		}
		else
		{
			// fade out, fade in
		}				
		rootPanel.setHeight(newH+"px");
		rootPanel.setWidth(newW+"px");				
		
		
		
		this.oldIsWidgetContent = widget;
	}

	/** convenience method setting content to null and shrinking the PageElement away
	 * 
	 */
	public void hide()
	{
		setContent((IsWidget)null);	
		setBackgroundImage(null, true);
	}


	private void addNewContent(IsWidget widget) {
		rootPanel.add(widget.asWidget());		
	}

	private void removeOldContent() {
		while(rootPanel.getWidgetCount() > 0)
		{			
			rootPanel.remove(0);
		}
		// DOM based removal of (static template) elements...
		Element e = rootPanel.getElement();
		while(e.hasChildNodes())
		{					
			e.removeChild(e.getFirstChild());
		}		
	}


}
