package de.tud.kom.socom.web.server.database.reporting;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import de.tud.kom.socom.web.client.sharedmodels.Report;
import de.tud.kom.socom.web.client.util.exceptions.IllegalTypeException;
import de.tud.kom.socom.web.server.database.HSQLAccess;

public class HSQLReportDatabaseAccess implements ReportDatabaseAccess {
	
	private static ReportDatabaseAccess instance = new HSQLReportDatabaseAccess();
	private static HSQLAccess db;	
	
	private HSQLReportDatabaseAccess() {
		db = HSQLAccess.getInstance();
	}

	public static ReportDatabaseAccess getInstance() {
		return instance;
	}

	@Override
	public boolean createReport(long uid, long reference, String reference2, String reportTypeIdentifier, String report) throws SQLException, IllegalTypeException {
		PreparedStatement typeStatement = db.getPreparedStatement("SELECT id FROM reporttypes WHERE type = ?");
		typeStatement.setString(1, reportTypeIdentifier);
		ResultSet rs = typeStatement.executeQuery();
		if(!rs.next())
			throw new IllegalTypeException(reportTypeIdentifier + " does not exist");
		long typeid = rs.getLong(1);
		
		PreparedStatement statement = db.getPreparedStatement("INSERT INTO reports " +
				"(informant, type, reference, reference2, report) VALUES (" +
					"?,?,?,?,?);");
		
		statement.setLong(1, uid);
		statement.setLong(2, typeid);
		statement.setLong(3, reference);
		statement.setString(4, reference2);
		statement.setString(5, report);
		return statement.executeUpdate() == 1;
	}

	@Override
	public List<Report> fetchReports(String[] types, boolean alreadyReviewed, String fromInformant, int limit, int offset,
			int sortPolicy, boolean ascending) throws SQLException {
		//fromInformant: if null ignore
		//sortPolicy coding: 0=date, 1=type, 2=informant
		String selectQ = "SELECT * FROM reports " +
				"LEFT JOIN reporttypes ON reports.type = reporttypes.id " +
				"LEFT JOIN users ON users.uid = reports.informant ";
		String whereQ = "WHERE true " + (alreadyReviewed ? "" : "AND reviewed = false ") + (fromInformant == null ? "" : "AND users.name = ? ");
		whereQ += "AND (";
		for(int i = 0; i < types.length; i++)
			whereQ += " reporttypes.type = ? " + (i == types.length-1 ? "" : "OR ");
		whereQ += ") ";
		String orderQ = "ORDER BY " + (sortPolicy == 0 ? "reports.date " : (sortPolicy == 1 ? "reports.type " : (sortPolicy == 2 ? "UPPER(users.name) " : "reports.id ")));
		orderQ += ascending ? "ASC " : "DESC ";
		String limitQ = "LIMIT ? OFFSET ?;";
		String query = selectQ + whereQ + orderQ + limitQ;
		PreparedStatement statement = db.getPreparedStatement(query);
		
		
		int c = 1;
		if(fromInformant != null) statement.setString(c++, fromInformant);
		for(String type : types)
			statement.setString(c++, type);
		statement.setInt(c++, limit);
		statement.setInt(c, offset);
		
		ResultSet rs = statement.executeQuery();
		List<Report> result = new ArrayList<Report>();
		while(rs.next()) {
			long id = rs.getLong("reports.id");
			long type = rs.getLong("reports.type");
			long reference = rs.getLong("reports.reference");
			long date = rs.getTimestamp("reports.date").getTime();
			long informant = rs.getLong("reports.informant");
			long reviewedby = rs.getLong("reviewedby");
			Timestamp reviewtime = rs.getTimestamp("reports.reviewedon");
			long reviewdate = reviewtime == null ? -1L : reviewtime.getTime();
			String typeName = rs.getString("reporttypes.type");
			String informantName = rs.getString("users.name");
			String report = rs.getString("reports.report");
			String reference2 = rs.getString("reports.reference2");
			String review = rs.getString("reports.review");
			boolean reviewed = rs.getBoolean("reports.reviewed");
			
			Report r = new Report(id, type, reference, date, informant, reviewedby, 
					reviewdate, typeName, informantName, report, reference2, review, reviewed);
			result.add(r);
		}
		return result;
	}

	@Override
	public boolean closeReport(long id, long uid, String review) throws SQLException {
		String query = "UPDATE reports SET reviewed = true, reviewedby = ?, review = ?, reviewedon = NOW() WHERE id = ?;";
		PreparedStatement statement = db.getPreparedStatement(query);
		statement.setLong(1, uid);
		statement.setString(2, review);
		statement.setLong(3, id);
		return statement.executeUpdate() == 1;
	}
}