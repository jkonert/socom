package de.tud.kom.socom.web.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.tud.kom.socom.web.client.services.content.SoComContentService;
import de.tud.kom.socom.web.client.sharedmodels.GameContent;
import de.tud.kom.socom.web.client.sharedmodels.GameContentComment;
import de.tud.kom.socom.web.server.database.content.ContentDatabaseAccess;
import de.tud.kom.socom.web.server.database.content.HSQLContentDatabaseAccess;
import de.tud.kom.socom.web.server.util.FileWatcher;
import de.tud.kom.socom.web.server.util.Logger;
import de.tud.kom.socom.web.server.util.LoggerFactory;

@SuppressWarnings("serial")
public class SoComContentServiceImpl extends SoComService implements SoComContentService {

	private final ContentDatabaseAccess db = HSQLContentDatabaseAccess.getInstance();
	private Logger logger = LoggerFactory.getLogger();

	@Override
	public GameContent getGameContentNames(long cid) {
		GameContent result = null;
		try {
			result = db.getGameContentNames(cid);
		} catch (Exception e) {
			logger.Error(e);
		}
		return result;
	}

	@Override
	public List<GameContent> getGameContents(long uid, long contextid, int page) {
		List<GameContent> result = new ArrayList<GameContent>();
		try {
			result = db.getGameContents(uid, contextid, page);
		} catch (Exception e) {
			logger.Error(e);
		}
		return result;
	}

	@Override
	public int getGameContentsCount(long userId, long contextId) {
		int result = 0;
		try {
			result = db.getGameContentsPages(userId, contextId);
		} catch (Exception e) {
			logger.Error(e);
		}
		return result;
	}

	@Override
	public GameContent getGameContent(long uid, long cid) {

		GameContent result = null;
		try {
			result = db.getGameContent(uid, cid);
		} catch (Exception e) {
			logger.Error(e);
		}
		return result;
	}

	@Override
	public List<GameContentComment> getGameContentComments(long contentId, int page) {
		List<GameContentComment> result = new ArrayList<GameContentComment>();
		try {
			result = db.getGameContentComments(contentId, page);
		} catch (Exception e) {
			logger.Error(e);
		}
		return result;
	}

	@Override
	public int getGameContentCommentsCount(long contentId) {
		int result = 0;
		try {
			result = db.getGameCommentsPages(contentId);
		} catch (Exception e) {
			logger.Error(e);
		}
		return result;
	}

	@Override
	public GameContent setGameContentRating(long userId, long contentId, double rating) {
		db.setGameContentRating(userId, contentId, rating);
		return getGameContent(userId, contentId);
	}

	@Override
	public boolean setGameContentComment(long userId, long contentId, String text) {
		return db.setGameContentComment(userId, contentId, text);
	}

	@Override
	public boolean removeContent(long userId, long contentId) {
		return db.removeContent(userId, contentId);
	}

	@Override
	public boolean removeContentComment(long userId, long commentId) {
		return db.removeContentComment(userId, commentId);
	}

	@Override
	public boolean setContentVisibility(long userId, long contentId, int selectedIndex) {
		return db.setContentVisibility(userId, contentId, selectedIndex);
	}

	@Override
	public byte[] downloadGameContent(long contentid) {
		return db.getContentBytes(contentid);
	}

	@Override
	public boolean registerContentHit(long cid) {
		return db.registerContentHit(cid);
	}

	@Override
	public String getGameContentFile(long contentid) {
		try {
			byte[] bytes = downloadGameContent(contentid);
			File dir = new File("temp");
			if (!dir.exists())
				dir.mkdir();
			File f = new File(dir, "content" + contentid);
			FileWatcher.getInstance().addFile(f); //update file watcher to delete file after 2 hours if not downloaded again
			if(f.exists())
				return f.getPath();
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(bytes);
			fos.flush();
			fos.close();
			return f.getPath();
		} catch (FileNotFoundException e) {
			logger.Error(e);
		} catch (IOException e) {
			logger.Error(e);
		}
		return null;
	}
}
