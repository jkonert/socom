package de.tud.kom.socom.web.server.database.influence;

import java.util.List;

import de.tud.kom.socom.web.client.sharedmodels.Influence;

public interface InfluenceDatabaseAccess {

	public Influence getInfluence(long uid, boolean userIsAdmin, String influenceId, boolean includeDeletedAnswers);

	public boolean addPredefinedAnswer(long influenceId, long answerId);

	public boolean addFreeAnswer(long influenceId, long answerId);
	
	public boolean createFreeAnswer(long influenceId, long ownerId, String text, int visibiltiy);
	
	public int getInfluenceCount();

	public List<Influence> getAllInfluences(long uidUser, boolean isAdmin, int offset, int limit, boolean includeEndedInfluences, boolean includeDeletedAnswers);
	
	public List<Influence> getAllInfluences(long uidUser, boolean isAdmin, long uidOwner, boolean includeEndedInfluences, boolean includeDeletedAnswers);

	public boolean changeFreeAnswerDeletionFlag(long freeAnswerId, int deleteState);
	public boolean changePredefinedAnswerDeletionFlag(long answerId, int deleteState);

	public void appendResults(Influence influence);

	public boolean startInfluence(long influenceId, long time);

	public boolean stopInfluence(long influenceId);

	public boolean changeFreeAnswerVisibility(long id, int newVisibility);

	public boolean isOwnerOfFreeAnswer(long uid, long answerId);

	public void addAttendent(long id, long uid);

}
