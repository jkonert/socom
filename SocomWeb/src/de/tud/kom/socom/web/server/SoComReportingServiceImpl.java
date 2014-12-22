package de.tud.kom.socom.web.server;

import java.sql.SQLException;
import java.util.List;

import de.tud.kom.socom.web.client.services.reporting.SoComReportingService;
import de.tud.kom.socom.web.client.sharedmodels.LoginResult;
import de.tud.kom.socom.web.client.sharedmodels.Report;
import de.tud.kom.socom.web.client.util.exceptions.IllegalAccessException;
import de.tud.kom.socom.web.client.util.exceptions.IllegalTypeException;
import de.tud.kom.socom.web.server.database.reporting.HSQLReportDatabaseAccess;
import de.tud.kom.socom.web.server.database.reporting.ReportDatabaseAccess;
import de.tud.kom.socom.web.server.util.Logger;
import de.tud.kom.socom.web.server.util.LoggerFactory;

@SuppressWarnings("serial")
public class SoComReportingServiceImpl extends SoComService implements SoComReportingService {

	private Logger logger = LoggerFactory.getLogger();
	private ReportDatabaseAccess db = HSQLReportDatabaseAccess.getInstance();

	@Override
	public boolean sendReport(long reference, String reference2, String reportTypeIdentifier, String report, String sid) throws IllegalTypeException {
		LoginResult user = getCurrentUser(sid);
		if(!user.isSuccess()) return false;
		boolean success;
		try {
			success = db.createReport(user.getUid(), reference, reference2, reportTypeIdentifier, report);
		} catch (SQLException e) {
			logger.Error(e);
			e.printStackTrace();
			return false;
		}
		return success;
	}

	@Override
	public List<Report> getReports(String[] types, boolean alreadyReviewed, String fromInformant, int limit, int offset,
			int sortPolicy, boolean ascending, String sid) throws IllegalAccessException {
		if(!getCurrentUser(sid).isAdmin())
			throw new IllegalAccessException();
		try {
			return db.fetchReports(types, alreadyReviewed, fromInformant, limit, offset, sortPolicy, ascending); 
		} catch (SQLException e) {
			logger.Error(e);
			return null;
		}
	}

	@Override
	public boolean closeReport(long id, String text, String sid) throws IllegalAccessException {
		LoginResult currentUser = getCurrentUser(sid);
		if(!currentUser.isAdmin())
			throw new IllegalAccessException();
		try {
			return db.closeReport(id, currentUser.getUid(), text);
		} catch (SQLException e) {
			e.printStackTrace();
			logger.Error(e);
			return false;
		}
	}
}
