package de.tud.kom.socom.database.user;


import java.sql.SQLException;
import java.util.List;

import de.tud.kom.socom.util.datatypes.JournalEntry;
import de.tud.kom.socom.util.datatypes.SimpleGameContext;
import de.tud.kom.socom.util.exceptions.ContextNotFoundException;
import de.tud.kom.socom.util.exceptions.CurrentContextNotFoundException;

public interface UserGameInfoDatabase {

	public void setCurrentContext(long uid, long context, long contextId) throws SQLException, ContextNotFoundException;
	public long getCurrentContext(long uid, long gameInstance) throws SQLException, CurrentContextNotFoundException;
	public void addJournalEntry(long uid, long currentGameInst, JournalEntry log) throws SQLException;
	public void addTimePlayed(long uid, long contextId, long timeInS) throws SQLException;
	public void setTimePlayed(long uid, long contextId, long timeInS) throws SQLException;
	
	public List<JournalEntry>  getUserJournal(long uid, long gameInstId, int limit, int offset, String type, boolean gameLogs) throws SQLException;
	public long getTimePlayed(long uid, long contextId) throws SQLException;
	public List<SimpleGameContext> getVisitedContexts(long uid, long gameInst) throws SQLException;
}
