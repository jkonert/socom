package de.tud.kom.socom.web.client.services.administration;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import de.tud.kom.socom.web.client.sharedmodels.SimpleUser;
import de.tud.kom.socom.web.client.util.exceptions.IllegalAccessException;

@RemoteServiceRelativePath("admin")
public interface SoComAdministrationService extends RemoteService{
	
	public SimpleUser getUser(String username);
	List<SimpleUser> getUsersStartingWith(String c);
	public boolean changeUserDeletedState(String sid, long uid, int deleteState) throws IllegalAccessException;
	public String[] getDeletedStates();

//	public int getInfluenceCount();
//	public List<Influence> getInfluences(int offset, int limit);
//	public List<Influence> getInfluences(String owner);
//	public List<Influence> getInfluencesIncludeDeleted(int offset, int limit);
//	public List<Influence> getInfluencesIncludeDeleted(String owner);
	public boolean changeInfluenceAnswerDeletedState(String sid, boolean predefined, long answerId, int deleteState) throws IllegalAccessException;

	public boolean startInfluence(String sid, long influenceId, long time) throws IllegalAccessException;
	public boolean stopInfluence(String sid, long influenceId) throws IllegalAccessException;
	public boolean changeInfluenceAnswerVisibilityState(String sid,
			boolean predefined, long id, int newVisibility) throws IllegalAccessException;
	
}
