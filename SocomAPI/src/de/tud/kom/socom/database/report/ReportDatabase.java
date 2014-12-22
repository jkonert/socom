package de.tud.kom.socom.database.report;

import java.sql.SQLException;
import java.util.List;

import de.tud.kom.socom.util.datatypes.Report;
import de.tud.kom.socom.util.exceptions.IllegalParameterException;


public interface ReportDatabase {

	public boolean createReport(long informant, String type, long referenceId, String report) throws SQLException, IllegalParameterException;

	public List<Report> getReports(int limit, int offset, boolean includereviewed) throws SQLException;

	public boolean makeReview(long reportid, long uid, String review) throws SQLException;

	public Report getReport(long reportid) throws SQLException;
}
