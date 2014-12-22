package de.tud.kom.socom.web.client.services.reporting;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import de.tud.kom.socom.web.client.sharedmodels.Report;


public interface SoComReportingServiceAsync {

	void sendReport(long reference, String reference2, String influenceReportTypeIdentifier, String report, String sid,
			AsyncCallback<Boolean> callback);

	void getReports(String[] types, boolean alreadyReviewed, String fromInformant, int limit, int offset, int sortPolicy,
			boolean ascending, String sid, AsyncCallback<List<Report>> asyncCallback);

	void closeReport(long id, String text, String sessionID, AsyncCallback<Boolean> asyncCallback);

	
}
