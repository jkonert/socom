package de.tud.kom.socom.web.server.database.content;

import java.util.List;

import de.tud.kom.socom.web.client.sharedmodels.GameContent;
import de.tud.kom.socom.web.client.sharedmodels.GameContentComment;

public interface ContentDatabaseAccess {

	public GameContent getGameContentNames(long cid);

	public List<GameContent> getGameContents(long uid, long gid, int page);

	public int getGameContentsPages(long userId, long gameId);

	public GameContent getGameContent(long uid, long cid);

	public List<GameContentComment> getGameContentComments(long contentId, int page);

	public int getGameCommentsPages(long contentId);

	public boolean setGameContentRating(long userId, long contentId, double rating);

	public boolean setGameContentComment(long userId, long contentId, String text);

	public boolean removeContent(long userId, long contentId);

	public boolean removeContentComment(long userId, long commentId);

	public boolean setContentVisibility(long userId, long contentId, int selectedIndex);
	
	public boolean registerContentHit(long cid);

	public byte[] getContentBytes(long contentid);
}
