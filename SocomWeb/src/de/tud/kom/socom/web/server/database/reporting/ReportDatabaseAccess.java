package de.tud.kom.socom.web.server.database.reporting;

import java.sql.SQLException;
import java.util.List;

import de.tud.kom.socom.web.client.sharedmodels.Report;
import de.tud.kom.socom.web.client.util.exceptions.IllegalTypeException;

public interface ReportDatabaseAccess {

	public boolean createReport(long uid, long reference, String reference2, String reportTypeIdentifier, String report) throws SQLException, IllegalTypeException;

	public List<Report> fetchReports(String[] types, boolean alreadyReviewed, String fromInformant, int limit, int offset,
			int sortPolicy, boolean ascending) throws SQLException;

	public boolean closeReport(long id, long uid, String text) throws SQLException;

}
