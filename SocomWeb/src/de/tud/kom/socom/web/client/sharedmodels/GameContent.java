package de.tud.kom.socom.web.client.sharedmodels;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * 
 * @author rhaban
 * 
 */
public class GameContent implements IsSerializable {

	private long contentid, gameId, contextId, ownerId;
//	private boolean isVisible;
	private int visibilty, comments, ratingCount, hits;
	private String type, gameName, contextName, ownerName, title, description;
	private Date time;
	private double ratingAvg;//, ratingOwn;
	private String[] metadata;
//	private byte[] content;
	private GameContentComment lastComment;

	public GameContent() {
	}

	public GameContent(long contentid, int visibilty, String title, String description, long contextId, String contextName, long gameId, String gameName, Date time, int hits) {
		this.contentid = contentid;
//		this.isVisible = isVisible;
		this.visibilty = visibilty;
		this.title = title;
		this.description = description;
		this.contextId = contextId;
		this.contextName = contextName;
		this.gameId = gameId;
		this.gameName = gameName;
		this.time = time;
		this.hits = hits;
	}

	public GameContent(long contentid, int visibilty, String title, String description, String contextName, String type, long ownerId, String ownerName, Date time, double ratingAvg,  int ratingCount, int comments, int hits, GameContentComment lastComment) {
		this.contentid = contentid;
//		this.isVisible = isVisible;
		this.visibilty = visibilty;
		this.title = title;
		this.description = description;
		this.contextName = contextName;
		this.type = type;
		this.ownerId = ownerId;
		this.ownerName = ownerName;
		this.time = time;
		this.ratingAvg = ratingAvg;
//		this.ratingOwn = ratingOwn;
		this.ratingCount = ratingCount;
		this.comments = comments;
		this.hits = hits;
		this.lastComment = lastComment;
	}

	public long getID() {
		return contentid;
	}

	/**
	 * @return if the profile is visible
	 */
//	public boolean getIsVisible() {
//		return isVisible;
//	}

	/**
	 * @return the visibility setting
	 */
	public int getVisibility() {
		return visibilty;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return the games id
	 */
	public long getGameId() {
		return gameId;
	}

	/**
	 * @return the games name
	 */
	public String getGameName() {
		return gameName;
	}

	/**
	 * @return the contexts id
	 */
	public long getContextId() {
		return contextId;
	}

	/**
	 * @return the contexts name
	 */
	public String getContextName() {
		return contextName;
	}

	/**
	 * @return the owners id
	 */
	public long getOwnerId() {
		return ownerId;
	}

	/**
	 * @return the owners name
	 */
	public String getOwnerName() {
		return ownerName;
	}

	/**
	 * @return the time
	 */
	public Date getTime() {
		return time;
	}

	/**
	 * @return the formated time
	 */
	public String getTimeFormated() {
		DateTimeFormat f = DateTimeFormat.getFormat("dd.MM.yyyy, HH:mm");
		return f.format(time);
	}

	/**
	 * @return the average rating
	 */
	public double getRatingAvg() {
		return ratingAvg;
	}
//
//	/**
//	 * @return the own rating
//	 */
//	public double getRatingOwn() {
//		return ratingOwn;
//	}

	/**
	 * @return the number of ratings
	 */
	public double getRatingCount() {
		return ratingCount;
	}

	/**
	 * @return the number of comments
	 */
	public int getComments() {
		return comments;
	}

	/**
	 * @return the metadata
	 */
	public String[] getMetadata() {
		return metadata;
	}

//	/**
//	 * @return the content
//	 */
//	public byte[] getContent() {
//		return content;
//	}
//
//	public void setContent(byte[] cont){
//		this.content = cont;
//	}
	
	/**
	 * @return the number of hits
	 */
	public int getHits() {
		return hits;
	}
	
	public GameContentComment getLastComment() {
		return lastComment;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("GameContent for Context ").append(contextName).append("\n");
		sb.append("Content type is ").append(type).append("\n");
		for (String s : metadata)
			sb.append(" > ").append(s.split(";")[0]).append(": ")
					.append(s.split(";")[1]).append("\n");
		sb.append("\n");
		return sb.toString();
	}
}
