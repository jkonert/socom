package de.tud.kom.socom.components.report;

import java.sql.SQLException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.tud.kom.socom.SocomComponent;
import de.tud.kom.socom.database.report.HSQLReportDatabase;
import de.tud.kom.socom.database.report.ReportDatabase;
import de.tud.kom.socom.database.user.HSQLUserDatabase;
import de.tud.kom.socom.util.JSONUtils;
import de.tud.kom.socom.util.SocomRequest;
import de.tud.kom.socom.util.datatypes.Report;
import de.tud.kom.socom.util.exceptions.IllegalAccessException;
import de.tud.kom.socom.util.exceptions.SocomException;

/**
 * 
 * @author rhaban
 * 
 */
public class ReportManager extends SocomComponent {
	
	private static final String URL_PATTERN = "report";
	private static ReportManager instance = new ReportManager();
	private ReportDatabase db;
	private HSQLUserDatabase udb;

	private ReportManager() {
		this.db= HSQLReportDatabase.getInstance();
		this.udb = HSQLUserDatabase.getInstance();
	}

	public static ReportManager getInstance() {
		return instance;
	}
	
	/**
	 * reports an entity
	 * @param reference (unique id of the entity (comment-id, user-id, ...)
	 * @param type 'user', 'content', 'contentcomment', 'influenceanswer'
	 * @param report the message 
	 * @return success boolean
	 */
	public int report(SocomRequest req) throws NumberFormatException, SocomException, SQLException {
		long uid = req.getUid();
		long referenceId = Long.parseLong(req.getParam("reference"));
		String type = req.getParam("type");
		String report = req.getParam("report");
		boolean success = db.createReport(uid, type, referenceId, report);
		
		req.addOutput(JSONUtils.getSuccessJsonString(success));
		return 0;
	}

	/**
	 * Shows all reports within the given filter
	 * @param limit (optional, default: all
	 * @param offset (optional, default: 0, only used in combination with limit)
	 * @param includereviewed (optional, default: true) do also show reviewed reports 
	 * @return list of reports
	 */
	public int getReports(SocomRequest req) throws NumberFormatException, SocomException, JSONException, SQLException {
		int limit = req.getParam("limit", -1);
		int offset = req.getParam("offset", -1);
		boolean includereviewed = req.getParam("includereviewed", true);
		long uid = req.getUid();
		
		boolean admin = udb.userIsAdmin(uid);
		if(!admin) throw new IllegalAccessException();
		
		List<Report> result = db.getReports(limit, offset, includereviewed);
		JSONObject json = new JSONObject().put("reports", new JSONArray(result));
		req.addOutput(JSONUtils.JSONToString(json));
		return 0;
	}
	
	/**
	 * Shows a single report 
	 * @param reportid id referencing the report
	 * @return report
	 */
	public int getReport(SocomRequest req) throws NumberFormatException, SocomException, JSONException, SQLException {
		long uid = req.getUid();
		boolean admin = udb.userIsAdmin(uid);
		if(!admin) throw new IllegalAccessException();
		long reportid = Long.parseLong(req.getParam("reportid"));
		
		Report result = db.getReport(reportid);
		JSONObject json = new JSONObject().put("report", result);
		req.addOutput(JSONUtils.JSONToString(json));
		return 0;
	}
	
	/**
	 * Adds a review to a report
	 * @param review message from admin as a review to the report
	 * @param reportid id specifying the report 
	 * @return success boolean
	 */
	public int closeReport(SocomRequest req) throws SocomException, SQLException {
		long uid = req.getUid();
		boolean admin = udb.userIsAdmin(uid);
		if(!admin) throw new IllegalAccessException();
		String review = req.getParam("review");
		long reportid = Long.parseLong(req.getParam("reportid"));
		
		boolean success = db.makeReview(reportid, uid, review);
		req.addOutput(JSONUtils.getSuccessJsonString(success));
		return 0;
	}

	/**
	 * URL PATTERN IS "report"
	 */
	@Override
	public String getUrlPattern() {
		return URL_PATTERN;
	}
}
