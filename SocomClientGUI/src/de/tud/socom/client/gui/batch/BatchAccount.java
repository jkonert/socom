package de.tud.socom.client.gui.batch;

import java.io.IOException;
import java.io.Writer;

/** holding all information of a Socom / facebook Account to create, read, save
 * 
 * @author jkonert
 *
 */
public class BatchAccount
{
	
	private static final String SEPERATOR = ";";
	
	private String firstname;
	private String lastname;
	private String password;
	
	private String groupID = null;
	private int socomID = -1;
	private long facebookID = -1;
	private String fbEmail = null;
	private String fbPassword = null;
	private String socomFbAccessToken = null;
	private String fbLoginUrl = null;

	


	private BatchAccount()
	{
		
	}
	
	
	public BatchAccount(String firstname, String lastname, String password)
	{
		this.firstname = firstname;
		this.lastname = lastname;
		this.password = password;
		
	}


	protected String getGroupID() {
		return groupID;
	}


	protected void setGroupID(String groupID) {
		this.groupID = groupID;
	}


	protected int getSocomID() {
		return socomID;
	}


	protected void setSocomID(int socomID) {
		this.socomID = socomID;
	}


	protected long getFacebookID() {
		return facebookID;
	}


	protected void setFacebookID(long facebookID) {
		this.facebookID = facebookID;
	}


	protected String getFbEmail() {
		return fbEmail;
	}


	protected void setFbEmail(String fbEmail) {
		this.fbEmail = fbEmail;
	}


	public void setFbPassword(String password) {
		this.fbPassword  = password;		
		
	}
	
	protected String getFBPassword()
	{
		return fbPassword;
	}

	protected String getSocomFbAccessToken() {
		return socomFbAccessToken;
	}


	protected void setSocomFbAccessToken(String socomFbAccessToken) {
		this.socomFbAccessToken = socomFbAccessToken;
	}


	protected String getFbLoginUrl() {
		return fbLoginUrl;
	}


	protected void setFbLoginUrl(String fbLoginUrl) {
		this.fbLoginUrl = fbLoginUrl;
	}
	
	public boolean isSocomRegistered()
	{
		return this.socomID != -1;
	}
	
	public boolean isFacebookRegistered()
	{
		return this.facebookID != -1;
	}
	
	public boolean isAppRegistered()
	{
		return this.socomFbAccessToken != null;
	}
	
	public boolean isDone()
	{
		return isSocomRegistered() & isFacebookRegistered() & isAppRegistered();
	}
	
	/**
	 * 
	 * @param account2
	 * @return false if this groupID is not set or account2 is null, otherwise only returns true if both IDs are equal
	 */
	public boolean EqualGroup (BatchAccount account2)
	{
		return this.groupID!= null && account2 !=null && this.groupID.equals(account2.getGroupID());
	}


	public boolean isValid() {
		return this.firstname != null && this.lastname !=null;
	}
	
	@Override public int hashCode()
	{
		int result = 0;
		result += (firstname!=null)?firstname.hashCode():0;
		result += (lastname!=null)?lastname.hashCode():0;
		// result += (password!=null)?password.hashCode():0;   as password is now set via facebook the account is defined by name
		return result;
	}
	
	@Override public boolean equals(Object other)
	{
		if (other == null || !(other instanceof BatchAccount)) return false;
		return this.hashCode() == other.hashCode();
	}


	/** Parses and converts data from a line from a file.
	 *  Format: Vorname; Nachname; Passwort[; groupID][; Socom ID; facebook-ID; fb-email; access-token]
	 *  Password min 6 chars
	 *  groupID (optional) to make all accounts with same groupID friends on facebook
	 * 
	 * @param line expects a semicolon serperated line from file. 
	 * @return
	 */
	public static BatchAccount createFromString(String line) {
		String[] parts = line.split(SEPERATOR);
		if (parts.length<3) return new BatchAccount();
		BatchAccount newAccount = new BatchAccount(parts[0].trim(),parts[1].trim(),parts[2].trim());
		if (parts.length<4) return newAccount;
		newAccount.setGroupID(parts[3].trim());
		if (parts.length<5) return newAccount;
		newAccount.setSocomID(Integer.parseInt(parts[4].trim()));
		if (parts.length<6) return newAccount;
		newAccount.setFacebookID(Long.parseLong(parts[5].trim()));
		if (parts.length<7) return newAccount;
		newAccount.setFbEmail(parts[6].trim());
		if (parts.length<8) return newAccount;
		newAccount.setSocomFbAccessToken(parts[7].trim());
		return newAccount;
	}


	public String getName() {
		return (lastname != null && lastname.length() > 0)?firstname+" "+lastname:firstname;
	}


	public String getPassword() {
		return password;
	}


	public void writeTo(Writer writer) throws IOException {
		writer.append(firstname).append(SEPERATOR);
		writer.append(lastname).append(SEPERATOR);
		writer.append(password);
		writeIfNotEmpty(writer,getGroupID());
		writeIfNotEmpty(writer,getSocomID());
		writeIfNotEmpty(writer,getFacebookID());
		writeIfNotEmpty(writer,getFbEmail());
		writeIfNotEmpty(writer,getSocomFbAccessToken());
	}


	private void writeIfNotEmpty(Writer writer, long value) throws IOException {
		if (value != -1)
		{
			writeIfNotEmpty(writer, String.valueOf(value)); // subobtimal String creation here...
		}
		
	}


	private void writeIfNotEmpty(Writer writer, String value) throws IOException {
		if (value != null && value.length() > 0)
		{
			writer.append(SEPERATOR).append(value);
		}
				
		
	}


	public void setPassword(String password) {
		this.password = password;
		
	}

	
}
