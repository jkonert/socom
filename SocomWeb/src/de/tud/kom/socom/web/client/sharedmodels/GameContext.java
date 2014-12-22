package de.tud.kom.socom.web.client.sharedmodels;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * 
 * @author rhaban
 * 
 */
public class GameContext implements IsSerializable {

	private long id, gameid;
	private String name, gamename, image;
	private int contentCount;

	public GameContext() {
	}

	public GameContext(long id, String name, long gameid, String gamename, int contentCount, String image) {
		this.id = id;
		this.name = name;
		this.gameid = gameid;
		this.gamename = gamename;
		this.contentCount = contentCount;
		this.image = image;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @return the Name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the games id
	 */
	public long getGameId() {
		return gameid;
	}

	/**
	 * @return the games name
	 */
	public String getGameName() {
		return gamename;
	}

	/**
	 * @return the number of visible contents
	 */
	public int getContentCount() {
		return contentCount;
	}

	/**
	 * @return the image
	 */
	public String getImage() {
		return image;
	}
}
