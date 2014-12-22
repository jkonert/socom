package de.tud.kom.socom.database.content;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import de.tud.kom.socom.util.datatypes.GameContent;
import de.tud.kom.socom.util.enums.ContentCategory;
import de.tud.kom.socom.util.exceptions.ContentNotAvailableException;
import de.tud.kom.socom.util.exceptions.ContentNotFoundException;
import de.tud.kom.socom.util.exceptions.IllegalAccessException;

public interface GameContentDatabase {

	public List<GameContent> fetchContent(long uid, long contextID, Date sinceDate) throws SQLException;
	
	public List<GameContent> fetchContent(long uid, long gameInstId, String[] contextids, Date since, String[] types, String[] titles, String[] keywords, String[] metadata) throws SQLException;

	public String createGameContent(long uid, long contextID, String title, String description, ContentCategory category, Map<String, String> attributes, String type, int visibility) throws SQLException;

	public void rateContent(long uid, long id, double rating) throws SQLException;
	
	public long uploadGameContent(long uid, String identifier, InputStream is) throws SQLException, ContentNotFoundException;

	public byte[] downloadContent(long id, long contentid, boolean increaseHits) throws SQLException, IOException, ContentNotAvailableException, IllegalAccessException;

	public String getType(long id) throws SQLException, ContentNotFoundException;

	public void setContent(long id, InputStream in) throws SQLException;

	public long addComment(long uid, long contentid, String message) throws SQLException;

	public boolean deleteComment(long uid, long commentid, int delete) throws SQLException, IllegalAccessException;
}
