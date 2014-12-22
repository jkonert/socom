package de.tud.kom.socom.web.client.sharedmodels;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * 
 * @author rhaban
 * 
 */
public class GameInstance implements IsSerializable {

	private long id;
	private String name, genre, version, description, image;
	private int contentVisible, hits;

	public GameInstance() {
	}

	public GameInstance(long id, String name, String version, String genre, String description, int contentVisible, String image, int hits) {
		this.id = id;
		this.name = name;
		this.version = version;
		this.genre = genre;
		this.description = description;
		this.contentVisible = contentVisible;
		this.image = image;
		this.hits = hits;
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
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @return the genre
	 */
	public String getGenre() {
		return genre;
	}

	/**
	 * @return the genre
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the visible content
	 */
	public int getContentVisible() {
		return contentVisible;
	}

	/**
	 * @return the image
	 */
	public String getImage() {
		return image;
	}

	/**
	 * @return the hits
	 */
	public int getHits() {
		return hits;
	}
}
