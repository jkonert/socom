package de.tud.kom.socom.web.client.services.reporting;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import de.tud.kom.socom.web.client.sharedmodels.Report;
import de.tud.kom.socom.web.client.util.exceptions.IllegalAccessException;
import de.tud.kom.socom.web.client.util.exceptions.IllegalTypeException;

@RemoteServiceRelativePath("report")
public interface SoComReportingService extends RemoteService {

	boolean sendReport(long reference, String reference2, String influenceReportTypeIdentifier, String report, String sid)
			throws IllegalTypeException;

	List<Report> getReports(String[] types, boolean alreadyReviewed, String fromInformant, int limit, int offset,
			int sortPolicy, boolean ascending, String sid) throws IllegalAccessException;

	boolean closeReport(long id, String text, String sessionID) throws IllegalAccessException;

}
