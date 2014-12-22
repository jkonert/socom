package de.tud.kom.socom.web.client.services.content;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import de.tud.kom.socom.web.client.sharedmodels.GameContent;
import de.tud.kom.socom.web.client.sharedmodels.GameContentComment;

@RemoteServiceRelativePath("content")
public interface SoComContentService extends RemoteService{

	public GameContent getGameContentNames(long cid);
	public List<GameContent> getGameContents(long uid, long contextid, int page);
	public int getGameContentsCount(long userId, long gameId);
	public GameContent getGameContent(long uid, long cid);
	public List<GameContentComment> getGameContentComments(long contentId, int page);
	public int getGameContentCommentsCount(long contentId);
	public GameContent setGameContentRating(long userId, long contentId, double rating);
	public boolean setGameContentComment(long userId, long contentId, String text);
	public boolean removeContent(long userId, long contentId);
	public boolean removeContentComment(long userId, long commentId);
	public boolean setContentVisibility(long userId, long contentId, int selectedIndex);
	public byte[] downloadGameContent(long contentid);
	public String getGameContentFile(long contentid);
	public boolean registerContentHit(long cid);
}
