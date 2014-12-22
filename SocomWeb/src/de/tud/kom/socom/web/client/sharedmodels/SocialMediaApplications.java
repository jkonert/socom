package de.tud.kom.socom.web.client.sharedmodels;

/** a central list of supported SocialMedia Application Plattforms */
public enum SocialMediaApplications
{
	facebook(1,"fb", "facebook"),
	googleplus(2,"gp", "Google+");
	
	private int id;
	private String identifier;
	private String displayName;

	private SocialMediaApplications(int id, String identifier, String displayName)
	{
		this.id = id;
		this.identifier = identifier;
		this.displayName = displayName;
		// more to come (supported features etc...)
	}

	/** returns the internal ID of this Application (1,2,...) to be used as a very short identifier; e.g. in DB or numerical switches
	 * 
	 * @return
	 */
	public int getId() {
		return id;
	}

	/** returns a short 2-6 character identifier of this Social Media Application; e.g. fb for facebook, gp for googleplus
	 * 
	 * @return
	 */
	public String getIdentifier() {
		return identifier;
	}
	
	/** returns a ID:name:identifier string, e.g. "1:facebook:fb";
	 * 
	 */
	@Override
	public String toString()
	{
		return getId()+":"+this.name()+":"+getIdentifier();
	}

	public static SocialMediaApplications valueOfIndentifier(String identifier) {
		// XXX JK: this could be done with a HashMap to have it in O(1)... (JK)
		for(SocialMediaApplications app: SocialMediaApplications.values())
		{
			if (app.getIdentifier().equals(identifier)) return app;
		}
		throw new UnsupportedOperationException("given identifier not found");
	}

	public String getDisplayname() {
		return displayName;
	}
}
 