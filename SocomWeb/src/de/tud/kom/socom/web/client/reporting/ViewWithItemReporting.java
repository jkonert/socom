package de.tud.kom.socom.web.client.reporting;

import de.tud.kom.socom.web.client.reporting.ItemReportView.SendReportCallback;


public interface ViewWithItemReporting {
	
	public void enableReporting(boolean enable);
	public void setSendReportCallback(SendReportCallback cb);
	
}
