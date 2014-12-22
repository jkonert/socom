package de.tud.kom.socom.database.report;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import de.tud.kom.socom.database.HSQLDatabase;
import de.tud.kom.socom.util.datatypes.Report;
import de.tud.kom.socom.util.exceptions.IllegalParameterException;

public class HSQLReportDatabase extends HSQLDatabase implements ReportDatabase {

	private static ReportDatabase instance = new HSQLReportDatabase();

	private HSQLReportDatabase() {
		super();
	}

	public static ReportDatabase getInstance() {
		return instance;
	}

	@Override
	public boolean createReport(long informant, String type, long referenceId, String report) throws SQLException, IllegalParameterException {
		PreparedStatement typeStatement = db.getPreparedStatement("SELECT id FROM reporttypes WHERE type = ?");
		typeStatement.setString(1, type);
		ResultSet rs = typeStatement.executeQuery();
		if(!rs.next())
			throw new IllegalParameterException("type");
		long typeid = rs.getLong(1);
		
		PreparedStatement statement = db.getPreparedStatement("INSERT INTO reports " +
				"(informant, type, reference, report) VALUES (" +
					"?, ?,?,?);");
		statement.setLong(1, informant);
		statement.setLong(2, typeid);
		statement.setLong(3, referenceId);
		statement.setString(4, report);
		return statement.executeUpdate() == 1;
	}

	@Override
	public List<Report> getReports(int limit, int offset, boolean includereviewed) throws SQLException {
		List<Report> result = new LinkedList<Report>();
		String limitStatement = limit != -1 ? 
				" LIMIT " + limit + (offset != -1 ? " OFFSET " + offset : "") 
					: "";
		String whereStatement = includereviewed ? "" : " WHERE reviewed = false ";
		String query = "SELECT " +
				"id, informant, reporttypes.type, reference, reference2, date, report, review, reviewed, reviewedby, reviewedon " +
				"FROM reports INNER JOIN reporttypes ON reports.type = reporttypes.id " +
				whereStatement + " ORDER BY date DESC " + limitStatement;

		PreparedStatement statement = db.getPreparedStatement(query);
		ResultSet rs = statement.executeQuery();
		
		while(rs.next()) {
			Report r = extractReport(rs);
			result.add(r);
		}
		
		return result;
	}

	@Override
	public boolean makeReview(long reportid, long uid, String review) throws SQLException {
		PreparedStatement statement = db.getPreparedStatement("UPDATE reports " +
				"SET reviewed = true, reviewedby = ?, review = ?, reviewedon = CURRENT_TIMESTAMP " +
				"WHERE id = ?;");
		statement.setLong(1, uid);
		statement.setString(2, review);
		statement.setLong(3, reportid);
		return statement.executeUpdate() == 1;
	}

	@Override
	public Report getReport(long reportid) throws SQLException {
		String query = "SELECT " +
				"id, informant, reporttypes.type, reference, reference2, date, report, review, reviewed, reviewedby, reviewedon " +
				"FROM reports INNER JOIN reporttypes ON reports.type = reporttypes.id " +
				"WHERE id = ?";
		PreparedStatement statement = db.getPreparedStatement(query);
		statement.setLong(1, reportid);
		ResultSet rs = statement.executeQuery();
		
		if(rs.next())
			return extractReport(rs);
		return null;
	}

	private Report extractReport(ResultSet rs) throws SQLException {
		String report = rs.getString("report");
		String reference2 = rs.getString("reference2");
		String type = rs.getString("type");
		long reference = rs.getLong("reference");
		long id = rs.getLong("id");
		Date timestamp = rs.getTimestamp("date");
		long informant = rs.getLong("informant");
		boolean reviewed = rs.getBoolean("reviewed");
		long reviewedby = rs.getLong("reviewedby");
		String review = rs.getString("review");
		Date reviewedon = rs.getTimestamp("reviewedon");
			
		Report r = new Report(id, informant, report, type, reference2, reference, timestamp);
		if(reviewed) r.addReview(reviewedby, review, reviewedon);
		return r;
	}
}