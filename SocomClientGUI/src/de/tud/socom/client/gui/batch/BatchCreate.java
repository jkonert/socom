package de.tud.socom.client.gui.batch;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.json.JSONException;
import org.json.JSONObject;

import de.tud.kom.socom.GlobalConfig;
import de.tud.kom.socom.util.EasyEncrypter;
import de.tud.socom.client.gui.ClientGuiFrame;
import de.tud.socom.client.gui.LoginPanel;
import de.tud.socom.client.logic.Connection;
import de.tud.socom.client.logic.Status;

public class BatchCreate {

	private static final int REQUEST_DELAY = 300;
	private static final String COMMENT_START = "#";
	private static final String facebookCreate = "https://graph.facebook.com/231481593528599/accounts/test-users?installed=true&name={NAME}&locale=de_DE&permissions=user_about_me,publish_stream,read_stream,offline_access,read_friendlists,manage_friendlists&method=post&access_token=231481593528599|sf-SfH-kbob4FU4_2pRTbcFtCNg";
	private static final String facebookChangePW = "https://graph.facebook.com/{USERID}?password={PASSWD}&name={NAME}&method=post&access_token=231481593528599|sf-SfH-kbob4FU4_2pRTbcFtCNg";
	private static final String facebookMakeFriends = "https://graph.facebook.com/{USERID}/friends/{FRIENDID}?method=post&access_token={USERTOKEN}";
	
	private static final String fbParamName = "\\{NAME\\}";
	private static final String fbParamUserID = "\\{USERID\\}";
	private static final String fbParamUserToken = "\\{USERTOKEN\\}";
	private static final String fbParamFriendID = "\\{FRIENDID\\}";
	private static final String fbParamPassword = "\\{PASSWD\\}";
	private static final String GAME = "Portal";
	private static final String VERSION = "1.0";
	private static final String GAMEPW = "portalpw";
	private static final String VISIBILITY = "2";
	
	private Connection c = Connection.get();
	
	private HashSet<BatchAccount> accounts = new HashSet<BatchAccount>();
	private Map<String,Set<BatchAccount>> groups = new HashMap<String, Set<BatchAccount>>();
	private List<String> fileprefix = new LinkedList<String>();

	public BatchCreate() {
		JFileChooser fc = new JFileChooser();
		int returnVal = fc.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File f = fc.getSelectedFile();
			if (!f.exists())
				return;
			int groupcount = 0;
			int accountcount = 0;
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(f)));				
				boolean remembercomments = true;
				while (reader.ready()) {
					String line = reader.readLine();
					if (line.startsWith(COMMENT_START) || line.trim().length() == 0) {
						if (remembercomments)
						{
							fileprefix.add(line);
						}
						continue;
					} else 
					{
						BatchAccount newAccount = BatchAccount.createFromString(line);
						if (!newAccount.isValid()) continue;
						remembercomments = false; // only re-write to file the lines before first account...
						accounts.add(newAccount);
						if (newAccount.getGroupID()!=null) addToGroups(newAccount);
					}
				}				
				reader.close();
				
				// First now create all Accounts in facebook and in Socom
				for (BatchAccount ac: accounts)
				{
					try
					{
						boolean saveFBToken = false;
						if (!ac.isFacebookRegistered())
						{
							registerInFacebookAndSave(ac);
							saveFBToken = true;
						}
						if (!ac.isSocomRegistered())
						{
							registerInSocomAndSave(ac);
						}
						loginToSocom(ac);
						
									
						if (saveFBToken)
						{
							// now save facebookToken in SocomAPI:
							byte[] secretCrypted = EasyEncrypter.getInstance().encryptString(EasyEncrypter.getSHA(ac.getPassword()));
							String secret = "";
							for (byte be : secretCrypted) {
								String hex = String.format("%h", be);
								secret += hex.substring(hex.length() > 1 ? hex.length() - 2 : 0) + "-";
							}			
							String url = Status.SERVER_URL + "social/requestToken?code="+GlobalConfig.SOCIALNETWORK_CODE_DIRECT_TOKEN_SAVE+"&state="+GlobalConfig.SOCIALNETWORK_FACEBOOK+";"+ac.getSocomID()+"-"+secret+"&token="+ac.getSocomFbAccessToken();
							c.sendGETRequest(url); // this causes a JSOn error as it returns a redirect!
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
						System.out.println("Error for "+ac.getName()+". Continue..");
					}
					
				}
				// Second make friends for groups (on facebook)
				for(String groupID:groups.keySet())
				{
					Set<BatchAccount> members = groups.get(groupID);
					if (members.size() <= 1) continue;
					BatchAccount[] array = members.toArray(new BatchAccount[members.size()]);
					for(int i=0;i<array.length-1; i++)
					{
						for (int j=i+1; j<array.length; j++)
						{
							makeFacebookFriends(array[i], array[j]);
						}
					}
				}
				
				String outfileName = f.getAbsolutePath();
				int i = outfileName.lastIndexOf('.');
				String useName = outfileName;
				if (i == -1 || i == outfileName.length()-1)	useName = outfileName+"_processed";
				else useName = outfileName.substring(0,i)+"_processed."+outfileName.substring(i+1);				
					
				File outFile = new File(useName);
				int j=2;
				while (outFile.exists())
				{
					if (i == -1 || i == outfileName.length()-1)	useName = outfileName+"_processed"+j;
					else useName = outfileName.substring(0,i)+"_processed"+j+"."+outfileName.substring(i+1);
					outFile = new File(useName);
					j++;
				}
				
				BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
				for (String line: fileprefix)
				{
					writer.append(line).append('\n');
				}				
				
				for(String groupID:groups.keySet())
				{
					Set<BatchAccount> members = groups.get(groupID);
					for (BatchAccount member:members)
					{
						try
						{
							member.writeTo(writer);
							writer.append('\n');
							accountcount++;
							createSocomConfigFile(member,outFile.getParent());
						}
						catch (Exception e)
						{
							e.printStackTrace();
							System.out.println("Error writing into out file for "+member.getName()+". Continue..");
						}
					}
					groupcount++;
				}
				if (accountcount == 0) writer.append("# No successfully created accounts to write!");
				else writer.append("# In summary: "+accountcount+" accounts created in "+groupcount+" friend-groups");
				writer.flush();
				writer.close();								

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			JOptionPane.showMessageDialog(ClientGuiFrame.getWindows()[0],"Batch processing done.\nRead Log for Errors.\n\nIn summary: "+accountcount+" accounts created in "+groupcount+" friend-groups.\n(on Server-DB: "+Status.SERVER_URL+")");
		}
	}

	private void createSocomConfigFile(BatchAccount member, String dirPath) throws IOException {
		
		File outFile = new File(dirPath+"\\socom_"+member.getGroupID()+""+member.getSocomID()+""+member.getName().replaceAll(" ", "")+".cfg");
		
		String outfileName = outFile.getAbsolutePath();
		int i = outfileName.lastIndexOf('.');
		String useName = outfileName;
		int j=2;
		while (outFile.exists())
		{
			if (i == -1 || i == outfileName.length()-1)	useName = outfileName+"_"+j;
			else useName = outfileName.substring(0,i)+"_"+j+"."+outfileName.substring(i+1);
			outFile = new File(useName);
			j++;
		}
		
		BufferedWriter writer = null;
		try
		{
			writer = new BufferedWriter(new FileWriter(outFile));
			writer.append("{\n");
			writer.append("\t\"server\": \"ktxsoftware.com\",\n");
			writer.append("\t\"username\": \""+member.getName()+"\",\n");
			writer.append("\t\"password\": \""+member.getPassword()+"\"\n");
			writer.append("}");
			writer.flush();
		}
		finally 
		{
			writer.close();
		}				
	}

	private void makeFacebookFriends(BatchAccount ac1,
			BatchAccount ac2) {
		try {
			String result = c.readFromUrl(facebookMakeFriends.replaceAll(fbParamUserID,String.valueOf(ac1.getFacebookID())).replaceAll(fbParamUserToken,ac1.getSocomFbAccessToken()).replaceAll(fbParamFriendID,String.valueOf(ac2.getFacebookID())));		
			if (result !=null && result.equals("true"))
			{
				result = c.readFromUrl(facebookMakeFriends.replaceAll(fbParamUserID,String.valueOf(ac2.getFacebookID())).replaceAll(fbParamUserToken,ac2.getSocomFbAccessToken()).replaceAll(fbParamFriendID,String.valueOf(ac1.getFacebookID())));				
			}
			if (result ==null || !result.equals("true"))
			{
				System.out.println("Failed to make facebook friends!");
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}

	private void addToGroups(BatchAccount ac) {
		Set<BatchAccount> members = groups.get(ac.getGroupID());
		if (members == null)
		{
			members = new HashSet<BatchAccount>(8);
			groups.put(ac.getGroupID(), members);
		}
		members.add(ac);		
	}

	private void loginToSocom(BatchAccount ac) {		
		LoginPanel.get().startLogout();
		delay();
		JSONObject json = c.sendGETRequest(
				Status.SERVER_URL + "user/loginuser?username=" + ac.getName() + "&password=" + ac.getPassword() + "&game=" + GAME + "&version=" + VERSION + "&gamepassword="
						+ GAMEPW);

		if (json.has("uid")) {
			LoginPanel.get().setLogin(ac.getName(), ac.getPassword(),GAME, VERSION, GAMEPW);
		}
		
	}

	private void registerInSocomAndSave(BatchAccount ac) throws JSONException {
		try
		{
			LoginPanel.get().startLogout();
		}
		catch (Exception e) {}
		String url = Status.SERVER_URL+"user/createUser?username="+ac.getName()+"&password="+ac.getPassword()+"&game="+GAME+"&version="+VERSION+"&gamepassword="+GAMEPW+"&visibility="+VISIBILITY;
		JSONObject r = c.sendGETRequest(url);
		// ServerAnswer sa = new ServerAnswer(url, r.toString());			
		ac.setSocomID(r.getInt("uid"));
		delay();
	}

//	private void loginToFacebook(BatchAccount ac) {
//		if (ac.isFacebookRegistered())
//		{
//			try {
//				String r = c.readFromUrl(ac.getFbLoginUrl());
//			} catch (MalformedURLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}			
//			delay();
//		}
//		
//	}

	private void registerInFacebookAndSave(BatchAccount ac) {
		
		JSONObject r;
		try 
		{
			 r =  c.readJsonFromUrl(facebookCreate.replaceAll(fbParamName, ac.getName()));
			ac.setFacebookID(r.getLong("id"));
			ac.setSocomFbAccessToken(r.getString("access_token"));
			ac.setFbEmail(r.getString("email"));
			ac.setFbPassword(r.getString("password"));
			ac.setPassword(r.getString("password"));
			ac.setFbLoginUrl(r.getString("login_url"));
			
			// save the facebook-Token in Socom later...
			
		} 
		catch (JSONException e)
		{

			e.printStackTrace();
		}
		catch (NullPointerException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		// does NOT WOrk as it invalidates the access tokens! need to keep the pw as given from facebook!
//		if (ac.isFacebookRegistered())
//		{
//			try {
//				String result = c.readFromUrl(facebookChangePW.replaceAll(fbParamName, ac.getName()).replaceAll(fbParamPassword, ac.getPassword()).replaceAll(fbParamUserID,String.valueOf(ac.getFacebookID())));
//				if (result==null || !result.equals("true"))
//				{
//					System.out.println("Failed to change FB PW");
//				}
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace(); 
//			}			
//		}
			
		
	}



	private void delay() {
		try {
			Thread.sleep(REQUEST_DELAY);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
