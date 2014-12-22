package de.tud.kom.socom.database.user;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import de.tud.kom.socom.GlobalConfig;
import de.tud.kom.socom.database.HSQLDatabase;
import de.tud.kom.socom.util.datatypes.JournalEntry;
import de.tud.kom.socom.util.datatypes.SimpleGameContext;
import de.tud.kom.socom.util.exceptions.ContextNotFoundException;
import de.tud.kom.socom.util.exceptions.CurrentContextNotFoundException;

public class HSQLUserGameInfoDatabase extends HSQLDatabase implements UserGameInfoDatabase {

	private static UserGameInfoDatabase instance = new HSQLUserGameInfoDatabase();

	private HSQLUserGameInfoDatabase() {
		super();
	}

	public static UserGameInfoDatabase getInstance() {
		return instance;
	}

	@Override
	public void setCurrentContext(long uid, long oldContext, long newContext) throws SQLException, ContextNotFoundException {
//		PreparedStatement statement = db.getPreparedStatement("SELECT uid FROM userprogress WHERE uid = ? AND scnid = ?");
//		statement.setLong(1, uid);
//		statement.setLong(2, newContext);
////		String selectQuery = "SELECT uid FROM userprogress WHERE uid = '" + uid + "' AND scnid = '" + newContext + "'";
//		ResultSet rs = statement.executeQuery();
//		if (!rs.next()) {
			// Insert
		PreparedStatement statement = db.getPreparedStatement("INSERT INTO userprogress (uid, scnid, playtime, time) VALUES (?, ?, '0', NOW());");
		statement.setLong(1, uid);
		statement.setLong(2, newContext);
		statement.executeUpdate();
//		} else {
//			// Update time
//			statement = db.getPreparedStatement("UPDATE userprogress SET time = NOW() WHERE uid = ? AND scnid = ?;");
//			statement.setLong(1, uid);
//			statement.setLong(2, newContext);
//			statement.executeUpdate();
//		}
		
		statement = db.getPreparedStatement("UPDATE gamecontextrelations SET timesused = timesused + 1 WHERE parentid = ? AND childid = ?;");
		statement.setLong(1, oldContext);
		statement.setLong(2, newContext);
		statement.executeUpdate();
	}

	@Override
	public long getCurrentContext(long userId, long gameInstanceId) throws SQLException, CurrentContextNotFoundException {
		PreparedStatement statement = db.getPreparedStatement("SELECT userprogress.scnid " +
									"FROM ((userprogress " +
									"INNER JOIN gamecontexts ON userprogress.scnid = gamecontexts.id) "
												+ "INNER JOIN gameinstances ON gamecontexts.gameinstid = gameinstances.id) " 
													+ "INNER JOIN users ON gameinstances.id = users.currentgameinst "
									+ "WHERE uid = ? AND currentgameinst = ? "
									+ "ORDER BY userprogress.time DESC "
									+ "LIMIT 0,1");
		statement.setLong(1, userId);
		statement.setLong(2, gameInstanceId);
		ResultSet rs = statement.executeQuery();
		if (!rs.next())
			throw new CurrentContextNotFoundException(userId);

		return rs.getLong("scnid");
	}

	@Override
	public void addTimePlayed(long uid, long contextId, long timeInS) throws SQLException {
		PreparedStatement statement = db.getPreparedStatement("UPDATE userprogress SET playtime = playtime + ? WHERE uid = ? AND scnid = ?");
		statement.setLong(1, timeInS);
		statement.setLong(2, uid);
		statement.setLong(3, contextId);
		statement.executeUpdate();
	}

	@Override
	public void setTimePlayed(long uid, long contextId, long timeInS) throws SQLException {
		PreparedStatement statement = db.getPreparedStatement("UPDATE userprogress SET playtime = ? WHERE uid = ? AND scnid = ?");
		statement.setLong(1, timeInS);
		statement.setLong(2, uid);
		statement.setLong(3, contextId);
		statement.executeUpdate();
	}

	@Override
	public void addJournalEntry(long uid, long gameInstId, JournalEntry log) throws SQLException {
	
		long typeId = lazyInsert("userlogtypes", log.getType());
	
		PreparedStatement statement = db.getPreparedStatement("INSERT INTO " + 
							"userlogs (" + 
								"uid, " + 
								"gameinstid, " +
								"typeid," + 
								"content," + 
								"time, " +
								"visibility) " + 
						"VALUES (?,?,?,?,NOW(),?);");
		statement.setLong(1, uid);
		statement.setLong(2, gameInstId);
		statement.setLong(3, typeId);
		statement.setString(4, log.getMessage());
		statement.setInt(5, log.getVisibility());
		statement.executeUpdate();
	}

	@Override
	public List<JournalEntry> getUserJournal(long uid, long gameInstId, int limit, int offset, String type, boolean game) throws SQLException {
		List<JournalEntry> result = new LinkedList<JournalEntry>();
		String typeSelect = type.equals("all") ? "" : " AND name = UPPER(?) ";
		String visibilitySelect = " AND " + (game ? "visibility = " + GlobalConfig.VISIBILITY_NON_USER: 
			"visibility < " + GlobalConfig.VISIBILITY_NON_USER);
		//FIXME make journal entries accessable for other players if visibility allows so
		
		PreparedStatement statement = db.getPreparedStatement("SELECT name, content, time, visibility " +
				"FROM (userlogs INNER JOIN userlogtypes ON userlogs.typeid = userlogtypes.id) " +
				"WHERE uid = ? " + typeSelect + visibilitySelect + " AND gameinstid = ? AND deleted = 0" +
						" ORDER BY time DESC LIMIT ? OFFSET ?;");
		statement.setLong(1, uid);
		boolean withtype = typeSelect.length() > 0;
		if(withtype) statement.setString(2, type);
		statement.setLong(withtype?3:2, gameInstId);
		statement.setInt(withtype?4:3, limit);
		statement.setInt(withtype?5:4, offset);
		
		ResultSet rs = statement.executeQuery();
		
		while(rs.next()){
			JournalEntry log = new JournalEntry(rs.getString("name"), rs.getString("content"), rs.getInt("visibility"));
			log.setTime(new Date(rs.getTimestamp("time").getTime()));
			result.add(log);
		}
		
		return result;
	}

	@Override
	public long getTimePlayed(long uid, long contextId) throws SQLException {
		PreparedStatement statement = db.getPreparedStatement("SELECT playtime FROM userprogress WHERE uid = ? AND scnid = ?;");
		statement.setLong(1, uid);
		statement.setLong(2, contextId);
		ResultSet rs = statement.executeQuery();
		
		if(!rs.next())
			return -1;
		
		return rs.getLong(1);
	}

	@Override
	public List<SimpleGameContext> getVisitedContexts(long uid, long gameInst) throws SQLException {
		List<SimpleGameContext> result = new LinkedList<SimpleGameContext>();
		
		PreparedStatement statement = db.getPreparedStatement("SELECT " +
						"playtime, time, externalid, name " +
				"FROM " +
						"userprogress JOIN gamecontexts ON userprogress.scnid = gamecontexts.id " +
				"WHERE " +
						"userprogress.uid = ? AND gamecontexts.gameinstid = ?;");
		statement.setLong(1, uid);
		statement.setLong(2, gameInst);
		ResultSet rs = statement.executeQuery();
		while(rs.next()){
			result.add(new SimpleGameContext(rs.getString("externalid"), rs.getTimestamp("time"), rs.getLong("playtime"), rs.getString("name")));
		}
		
		return result;
	}
}
