package de.tud.kom.socom.web.server.database.influence;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import de.tud.kom.socom.web.client.sharedmodels.AnswerResult;
import de.tud.kom.socom.web.client.sharedmodels.Influence;
import de.tud.kom.socom.web.client.sharedmodels.InfluenceAnswer;
import de.tud.kom.socom.web.client.util.Visibility;
import de.tud.kom.socom.web.server.database.HSQLAccess;
import de.tud.kom.socom.web.server.database.user.HSQLUserDatabaseAccess;
import de.tud.kom.socom.web.server.util.Logger;
import de.tud.kom.socom.web.server.util.LoggerFactory;

public class HSQLInfluenceDatabaseAccess implements InfluenceDatabaseAccess {

	private static InfluenceDatabaseAccess instance = new HSQLInfluenceDatabaseAccess();
	private Logger logger = LoggerFactory.getLogger();
	private static HSQLAccess db;

	/** Select statement that ends with the Joins "as infjoin" and can be extended by " WHERE" clauses and limits or additional tables ", xyz" **/
	private static final String INFLUENCE_SELECT_BASE = "SELECT infjoin.* FROM" +
								"(SELECT "+
								"influence.id, " + 
								"influence.externalid, " + 
								"influence.question, " + 
								"influence.timeout, " + 
								"influence.allowfreeanswers, " + 
								"influence.minchoices, " + 
								"influence.maxchoices, " + 
								"influence.maxdigits, " + 
								"influence.maxlines, " + 
								"influence.visibility, " + 
								"influence.freevotable, " +
								"influence.attendees, " + 
								"influencetypes.name AS typename, " + 
								"users.uid AS ownerid, " + 
								"users.name AS ownername, " + 
								"gameinstances.id AS gameid, " + 
								"gameinstances.version, " + 
								"games.name AS gamename, " + 
								"gamecontexts.id IS NOT NULL AS hascontext, " + 
								"gamecontexts.id AS contextid, " + 
								"gamecontexts.name AS contextname, " + 
								"gamecontexts.image AS contextimage " + 
							"FROM " + 
								"((((influence " + 
									"INNER JOIN influencetypes ON influence.type = influencetypes.id) "	+ 
									"INNER JOIN users ON influence.ownerid = users.uid) " + 
									"INNER JOIN gameinstances ON influence.gameinstid = gameinstances.id) "	+ 
									"INNER JOIN games ON gameinstances.gameid = games.gameid) " + 
									"LEFT JOIN gamecontexts ON influence.contextid = gamecontexts.id) "+
							"as infjoin ";
									
	
	
	private HSQLInfluenceDatabaseAccess() {
		db = HSQLAccess.getInstance();
	}

	public static InfluenceDatabaseAccess getInstance() {
		return instance;
	}

	@Override
	public boolean addPredefinedAnswer(long influenceId, long answerId) {
		try {
			String query = "UPDATE influencepredefinedanswers SET answercount = answercount + 1 WHERE influenceid = '" + influenceId
					+ "' AND id = '" + answerId + "';";
			return db.execQuery(query) == 1;
		} catch (SQLException e) {
			logger.Error(e);
			return false;
		}
	}

	@Override
	public boolean addFreeAnswer(long influenceId, long answerId) {
		try {
			String query = "UPDATE influencefreeanswers SET answercount = answercount + 1 WHERE influenceid = '" + influenceId
					+ "' AND id = '" + answerId + "';";
			return db.execQuery(query) == 1;
		} catch (SQLException e) {
			logger.Error(e);
			return false;
		}
	}

	@Override
	public boolean createFreeAnswer(long influenceId, long owner, String text, int visibility) {
		try {
			String query = "INSERT INTO influencefreeanswers (influenceid, text, ownerid, answercount, visibility) VALUES (" + influenceId + ", '" + text + "', "
					+ owner + ", 1, " + visibility + ");";
			db.execQuery(query);
		} catch (Exception e) {
			logger.Error(e);
			return false;
		}
		return true;
	}


	@Override
	public List<Influence> getAllInfluences(long uidUser, boolean isAdmin,long uidOwner, boolean includeEndedInfluences, boolean includeDeletedAnswers)	
	{
		
		List<Influence> result = new LinkedList<Influence>();
		try 
		{
			String query = INFLUENCE_SELECT_BASE +
							getVisibilityJoins(uidUser)+
							"WHERE " + 
								"infjoin.uidOwner = " + uidOwner +" ";
			if (!includeEndedInfluences) query += " AND infjoin.timeout != -1 AND infjoin.timeout > "+ System.currentTimeMillis();
			query += getQueryVisibilityRestriction(isAdmin,uidUser);			
			query += " ORDER BY infjoin.id DESC"; // newest Influence as first
			ResultSet rs = db.execQueryWithResult(query);
			Influence influence = null;
			do
			{
				influence = getNextInfluenceFromDBResult(rs, uidUser, isAdmin,includeDeletedAnswers);
				if (influence != null) result.add(influence);
			} while (influence != null);

		} catch (Exception e) {
			e.printStackTrace();  // TODO not nice to have a silent die here...
			return result;
		}
		return result;
		
		
	}
	
	
	@Override
	public List<Influence> getAllInfluences(long uidUser, boolean isAdmin, int offset, int limit, boolean includeEndedInfluences, boolean includeDeletedAnswers)	
	{
		List<Influence> result = new LinkedList<Influence>();
		try 
		{
			String query = INFLUENCE_SELECT_BASE +
						   getVisibilityJoins(uidUser) +
							"WHERE TRUE "; // needed to have and AND... clause work afterwards
			if (!includeEndedInfluences) query += " AND infjoin.timeout != -1 AND infjoin.timeout > "+ System.currentTimeMillis();
			query += getQueryVisibilityRestriction(isAdmin,uidUser);	
			query += " ORDER BY infjoin.id DESC"; // newest Influence as first
			query += getLimitOffset(limit, offset);
			ResultSet rs = db.execQueryWithResult(query);
			Influence influence = null;
			do
			{
				influence = getNextInfluenceFromDBResult(rs, uidUser, isAdmin,includeDeletedAnswers);
				if (influence != null) result.add(influence);
			} while (influence != null);

		} catch (Exception e) {
			e.printStackTrace();  // TODO not nice to have a silent die here...
			return result;
		}
		return result;
	}
	
	private String getLimitOffset(int limit, int offset)
	{
		return  " LIMIT " + limit + " OFFSET " + offset;
	}

	@Override
	public Influence getInfluence(long uidUser, boolean isAdmin, String influenceId, boolean includeDeletedAnswers) 
	{
		try{
			Long.parseLong(influenceId);
		} catch(NumberFormatException e) {
			logger.Error("Influence ID was: " + influenceId + " (Wrong Format)");
			return null;
		}
		
		Influence result = null;
		try {
			String query = INFLUENCE_SELECT_BASE +
						    getVisibilityJoins(uidUser)+
							"WHERE " + 
								"infjoin.externalid = " + influenceId +" ";
			query += getQueryVisibilityRestriction(isAdmin,uidUser);
			
			
			ResultSet rs = db.execQueryWithResult(query);			
			result = getNextInfluenceFromDBResult(rs, uidUser, isAdmin,includeDeletedAnswers);

		} catch (Exception e) {
			logger.Error(e);
			return null;
		}
		return result;
		
	}

	/** this method moves the ResultSet cursor itself forward, so just call and expect NULL or Influence instance as result
	 * 
	 * @param rs
	 * @param uid
	 * @param isAdmin
	 * @param includeDeletedAnswers
	 * @return
	 * @throws SQLException
	 */
	private Influence getNextInfluenceFromDBResult(ResultSet rs, long uid,
		boolean isAdmin, boolean includeDeletedAnswers) throws SQLException 
		{
		String query;
		long id = -1;
		Influence result = null;
		if (rs.next()) {
			result = new Influence(id = rs.getLong("id"), rs.getString("externalid"), rs.getLong("gameid"), rs.getString("gamename") + " "
					+ rs.getString("version"), rs.getBoolean("hascontext") ? rs.getLong("contextid") : -1, rs.getString("contextname"),
					rs.getString("contextimage"), rs.getLong("ownerid"), rs.getString("ownername"), rs.getString("question"), rs.getString("typename"),
					rs.getBoolean("allowfreeanswers"), rs.getShort("minchoices"), rs.getShort("maxchoices"), rs.getInt("maxdigits"), rs.getInt("maxlines"),
					rs.getTimestamp("timeout"), rs.getInt("visibility"), rs.getBoolean("freevotable"), rs.getString("attendees"));
		} else {
			return null;
		}

		query = "SELECT id, text, deleted " +
				"FROM influencepredefinedanswers " +
				"WHERE influenceid = " + id + (includeDeletedAnswers ? ";" : " AND influencepredefinedanswers.deleted = 0;");
		rs = db.execQueryWithResult(query);
		while (rs.next()) {
			result.addPredefinedAnswer(new InfluenceAnswer(rs.getLong("id"), rs.getString("text"), true, rs.getInt("deleted")));
		}

		query = "SELECT id, text, ownerid, deleted, name, visibility " +
				"FROM influencefreeanswers INNER JOIN users ON influencefreeanswers.ownerid = users.uid  " +
				"WHERE influenceid = " + id + (includeDeletedAnswers ? ";" : " AND influencefreeanswers.deleted = 0;");
		rs = db.execQueryWithResult(query);
		while (rs.next()) {
			int v = rs.getInt("visibility");
			long ownerid = rs.getLong("ownerid");
			if(isAdmin || checkIfVisible(v, ownerid, uid))
				result.addFreeAnswer(new InfluenceAnswer(rs.getLong("id"), rs.getString("text"), false, 
					ownerid, rs.getString("name"), rs.getInt("deleted"), v));
		}
		return result;
	}
	
	/**
	 *  adds a join part needed fort visibility setting sto be checked.
	 * @param uidUser
	 * @return
	 */
	private String getVisibilityJoins(long uidUser) {		
		return "LEFT JOIN usersnfriends ON (infjoin.ownerid = usersnfriends.uid AND  usersnfriends.friendid = "+uidUser+") "+
				"LEFT JOIN usergames ON (infjoin.gameid = usergames.gameinstanceid and usergames.uid = "+uidUser+") ";
	}

	
	/** returns a String with " AND ...)"
	 *  expects the join to hold infjoin table name and left joins with usergames and usersnfriends
	 * @param isAdmin
	 * @param uid
	 * @return
	 */
	private String getQueryVisibilityRestriction(boolean isAdmin, long uid) {
		if (!isAdmin)
		{ // check and filter visibility ..only works if the joins with visibilty needed tables has been done
			return " AND (" +
						    "infjoin.visibility = "    + String.valueOf(Visibility.PUBLIC) + " " +
						    "OR (infjoin.visibility = " + String.valueOf(Visibility.SOCOM_INTERN) + " AND " + ((uid >=0)?"TRUE": "FALSE")+ ") " +
						    "OR (infjoin.visibility = " + String.valueOf(Visibility.PRIVATE) + " AND infjoin.ownerid = "+ uid + ") " +
						    "OR (infjoin.visibility = " + String.valueOf(Visibility.FRIENDS) + " AND usersnfriends.uid = infjoin.ownerid AND usersnfriends.friendid = "+ uid + ") " +
						    "OR (infjoin.visibility = " + String.valueOf(Visibility.GAME_INTERN) + " AND usergames.uid = " + uid + " AND usergames.gameinstanceid = infjoin.gameid) "+
						    ")";
						    
		}
		return "";
	}

	

	private boolean checkIfVisible(int v, long ownerid, long uid) {
		//use OR's to prevent checking if not necessary (dont know if compiler does)
		boolean isPublic = v == Visibility.PUBLIC;
		boolean orIsOwner = isPublic || ownerid == uid;
		boolean orIsSocomUser = orIsOwner || (v == Visibility.SOCOM_INTERN && uid != -1);
		boolean orIsFriend = orIsSocomUser || (v == Visibility.FRIENDS && HSQLUserDatabaseAccess.getInstance().isFriendOf(ownerid, uid));
		return orIsFriend;
	}

	@Override
	public int getInfluenceCount() {
		try {			
			String query = "SELECT COUNT(*) FROM influence;";
			ResultSet rs = db.execQueryWithResult(query);
			if (!rs.next())
				return -1;
			return rs.getInt(1);
		} catch (SQLException e) {
			logger.Error(e);
		}
		return -1;
	}

	@Override
	public boolean changeFreeAnswerDeletionFlag(long freeAnswerId, int flag) {
		return changeAnswerDeletionFlag("influencefreeanswers", freeAnswerId, flag);
	}
	
	@Override
	public boolean changePredefinedAnswerDeletionFlag(long answerId, int deleteState) {
		return changeAnswerDeletionFlag("influencepredefinedanswers", answerId, deleteState);
	}

	private boolean changeAnswerDeletionFlag(String table, long freeAnswerId, int flag) {
		try {
			String query = "SELECT * FROM deletedflags WHERE id = " + flag + ";";
			ResultSet rs = db.execQueryWithResult(query);

			if (!rs.next())
				return false;

			query = "UPDATE " + table + " SET deleted = " + flag + " WHERE id = " + freeAnswerId + ";";
			return db.execQuery(query) > 0;

		} catch (SQLException e) {
			logger.Error(e);
		}
		return false;
	}

	@Override
	public void appendResults(Influence influence) {
		if(influence == null)
			return;
		try {
			String query = "SELECT * FROM influencefreeanswers WHERE influenceid = " + influence.getId();
			ResultSet rs = db.execQueryWithResult(query);
			while(rs.next()) {
				long answerId = rs.getLong("id");
				int count = rs.getInt("answercount");
				Date timestamp = rs.getTimestamp("createdtime");
				InfluenceAnswer answer = influence.getFreeAnswer(answerId);
				if(answer == null) continue; // not included (maybe deleted)
				answer.addAnswer(new AnswerResult(answerId, count, timestamp));
			}
			
			query = "SELECT * FROM influencepredefinedanswers WHERE influenceid = " + influence.getId();
			rs = db.execQueryWithResult(query);
			while(rs.next()) {
				long answerId = rs.getLong("id");
				int count = rs.getInt("answercount");
				InfluenceAnswer answer = influence.getPredefinedAnswer(answerId);
				if(answer == null) continue;
				answer.addAnswer(new AnswerResult(answerId, count, null));
			}
		} catch (SQLException e) {
			logger.Error(e);
		}
	}

	@Override
	public boolean startInfluence(long influenceId, long time) {
		try {
			PreparedStatement query = db.getPreparedStatement("UPDATE influence SET timeout = NOW() + ? SECOND WHERE id = ?;");

			query.setLong(1, time);
			query.setLong(2, influenceId);
			query.execute();
			return true;
		} catch (SQLException e) {
			logger.Error(e);
			return false;
		}
	}

	@Override
	public boolean stopInfluence(long influenceId) {
		return startInfluence(influenceId, 0);
	}

	@Override
	public boolean changeFreeAnswerVisibility(long id, int newVisibility) {
		String query = "UPDATE INFLUENCEFREEANSWERS SET visibility = " + newVisibility + " WHERE id = " + id + ";";
		try {
			return db.execQuery(query) == 1;
		} catch (SQLException e) {
			logger.Error(e);
			return false;
		}
	}

	@Override
	public boolean isOwnerOfFreeAnswer(long uid, long answerId) {
		String query = "SELECT ownerid FROM INFLUENCEFREEANSWERS WHERE id = " + answerId;
		try {
			ResultSet rs = db.execQueryWithResult(query);
		if(!rs.next())
			return false;
		return rs.getLong(1) == uid;
		} catch (SQLException e) {
			logger.Error(e);
			return false;
		}
	}

	@Override
	public void addAttendent(long id, long uid) {
		String query = "UPDATE influence SET attendees = CONCAT(attendees, '" + uid + ";') WHERE id = " + id + ";";
		try {
			db.execQuery(query);
		} catch (SQLException e) {
			logger.Error(e);
		}
	}
}