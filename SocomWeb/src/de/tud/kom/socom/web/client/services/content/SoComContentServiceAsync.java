package de.tud.kom.socom.web.client.services.content;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import de.tud.kom.socom.web.client.sharedmodels.GameContent;
import de.tud.kom.socom.web.client.sharedmodels.GameContentComment;

public interface SoComContentServiceAsync {

	void getGameContentNames(long cid, AsyncCallback<GameContent> asyncCallback);
	
	void getGameContents(long uid, long contextid, int page, AsyncCallback<List<GameContent>> callback);

	void getGameContentsCount(long userId, long gameId, AsyncCallback<Integer> asyncCallback);

	void getGameContent(long uid, long cid, AsyncCallback<GameContent> asyncCallback);

	void getGameContentComments(long contentId, int page, AsyncCallback<List<GameContentComment>> asyncCallback);

	void getGameContentCommentsCount(long contentId, AsyncCallback<Integer> asyncCallback);

	void setGameContentRating(long userId, long contentId, double rating, AsyncCallback<GameContent> asyncCallback);

	void setGameContentComment(long userId, long contentId, String text, AsyncCallback<Boolean> asyncCallback);

	void removeContent(long userId, long contentId, AsyncCallback<Boolean> asyncCallback);

	void removeContentComment(long userId, long commentId, AsyncCallback<Boolean> asyncCallback);

	void setContentVisibility(long userId, long contentId, int selectedIndex, AsyncCallback<Boolean> asyncCallback);
	
	void downloadGameContent(long contentid, AsyncCallback<byte[]> callback);

	void registerContentHit(long cid, AsyncCallback<Boolean> callback);

	void getGameContentFile(long contentid, AsyncCallback<String> callback);
	
}
