package de.tud.kom.socom.web.client.sharedmodels;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * 
 * @author rhaban
 * 
 */
public class GameContentComment implements IsSerializable {

	long commentid, ownerId;
	String ownerName, text;
	Date time;

	public GameContentComment() {
	}

	public GameContentComment(long commentid, long ownerId, String ownerName, String text, Date time) {
		this.commentid = commentid;
		this.ownerId = ownerId;
		this.ownerName = ownerName;
		this.text = text;
		this.time = time;
	}

	public long getID() {
		return commentid;
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
	 * @return the text
	 */
	public String getText() {
		return text;
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

}
