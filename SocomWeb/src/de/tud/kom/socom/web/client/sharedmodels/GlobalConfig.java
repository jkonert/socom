package de.tud.kom.socom.web.client.sharedmodels;

/**
 * Global settings
 * @author creuter
 *
 */
public interface GlobalConfig {
	
	public static final int EntriesPerPage = 2; // for debug: 2
	
	public static final int EntriesPerPage_ContentGames = EntriesPerPage;
	public static final int EntriesPerPage_ContentContexts = EntriesPerPage;
	public static final int EntriesPerPage_ContentContent = EntriesPerPage;
	public static final int EntriesPerPage_ContentComments = EntriesPerPage;

	public static final int RatingMin = 1;
	public static final int RatingMax = 5;
	public static final int RatingValues = RatingMax - RatingMin + 1;
	
}
