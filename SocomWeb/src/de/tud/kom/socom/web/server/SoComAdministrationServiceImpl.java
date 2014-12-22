package de.tud.kom.socom.web.server;

import java.util.List;

import de.tud.kom.socom.web.client.services.administration.SoComAdministrationService;
import de.tud.kom.socom.web.client.sharedmodels.SimpleUser;
import de.tud.kom.socom.web.client.util.exceptions.IllegalAccessException;
import de.tud.kom.socom.web.server.database.influence.HSQLInfluenceDatabaseAccess;
import de.tud.kom.socom.web.server.database.influence.InfluenceDatabaseAccess;
import de.tud.kom.socom.web.server.database.user.HSQLUserDatabaseAccess;
import de.tud.kom.socom.web.server.database.user.UserDatabaseAccess;

@SuppressWarnings("serial")
public class SoComAdministrationServiceImpl extends SoComService implements SoComAdministrationService {

	private UserDatabaseAccess udb = HSQLUserDatabaseAccess.getInstance();
	private InfluenceDatabaseAccess idb = HSQLInfluenceDatabaseAccess.getInstance();

	@Override
	public SimpleUser getUser(String username) {
		return udb.getSimpleUserByName(username);
	}

	@Override
	public List<SimpleUser> getUsersStartingWith(String startingWith) {
		return udb.getSimpleUsersByName(startingWith);
	}

	@Override
	public boolean changeUserDeletedState(String sid, long uid, int deleteState) throws IllegalAccessException {
		if(getCurrentUser(sid).isAdmin())
			return udb.changeUserDeletionFlag(uid, deleteState);
		throw new IllegalAccessException("not an admin");
	}

	@Override
	public String[] getDeletedStates() {
		return udb.getDeletedStates();
	}
	
	@Override
	public boolean changeInfluenceAnswerDeletedState(String sid, boolean predefined, long answerId, int deleteState) throws IllegalAccessException {
		if(!(getCurrentUser(sid).isAdmin() || (!predefined && idb.isOwnerOfFreeAnswer(getCurrentUser(sid).getUid(), answerId))))
			throw new IllegalAccessException("not an admin");
		if(predefined) return idb.changePredefinedAnswerDeletionFlag(answerId, deleteState);
		else return idb.changeFreeAnswerDeletionFlag(answerId, deleteState);
	}

	@Override
	public boolean startInfluence(String sid, long influenceId, long time) throws IllegalAccessException {
		if(!getCurrentUser(sid).isAdmin())
			throw new IllegalAccessException("not an admin");
		return idb.startInfluence(influenceId, time);
	}

	@Override
	public boolean stopInfluence(String sid, long influenceId) throws IllegalAccessException {
		if(!getCurrentUser(sid).isAdmin())
			throw new IllegalAccessException("not an admin");
		return idb.stopInfluence(influenceId);
	}

	@Override
	public boolean changeInfluenceAnswerVisibilityState(String sid, boolean predefined, long id, int newVisibility) throws IllegalAccessException {
		if(!(getCurrentUser(sid).isAdmin() || (!predefined && idb.isOwnerOfFreeAnswer(getCurrentUser(sid).getUid(), id))))
			throw new IllegalAccessException("not an admin");
		if(predefined) return false;
		else return idb.changeFreeAnswerVisibility(id, newVisibility);
	}
}
