package de.tud.kom.socom.web.server;

import java.util.List;

import de.tud.kom.socom.web.client.services.influence.SoComInfluenceService;
import de.tud.kom.socom.web.client.sharedmodels.Influence;
import de.tud.kom.socom.web.client.sharedmodels.InfluenceAnswer;
import de.tud.kom.socom.web.client.sharedmodels.LoginResult;
import de.tud.kom.socom.web.client.util.exceptions.NotVisibleException;
import de.tud.kom.socom.web.server.database.influence.HSQLInfluenceDatabaseAccess;
import de.tud.kom.socom.web.server.database.influence.InfluenceDatabaseAccess;

@SuppressWarnings("serial")
public class SoComInfluenceServiceImpl extends SoComService implements SoComInfluenceService {

	private final InfluenceDatabaseAccess db = HSQLInfluenceDatabaseAccess.getInstance();
	
	@Override
	public List<Influence> getInfluences(String sid, boolean includeEndedInfluences, int rangeStart, int rangeEnd)
			throws NotVisibleException {

		LoginResult currentUser = getCurrentUser(sid);
		boolean userIsAdmin = currentUser.isAdmin();
		long uid = currentUser.getUid();
		includeEndedInfluences = userIsAdmin || includeEndedInfluences;
		// visibility check done directly in DB SELECT due to offset/limit..
		List<Influence> influences = db.getAllInfluences(uid, userIsAdmin, rangeStart, rangeEnd - rangeStart,
				includeEndedInfluences, userIsAdmin ? true : false);
		return influences;
	}

	@Override
	public Influence getInfluence(String sid, String influenceId, boolean log) throws NotVisibleException {
		LoginResult currentUser = getCurrentUser(sid);
		boolean userIsAdmin = currentUser.isAdmin();
		long uid = currentUser.getUid();
		boolean includeDeletedAnswer = userIsAdmin;
		// throws NotVisibleException
		Influence influence = db.getInfluence(uid, userIsAdmin, influenceId, includeDeletedAnswer);
		if (influence == null)
			throw new NotVisibleException(-1); // FIXME throw another error
		db.appendResults(influence);

		if(log) logRequest(currentUser, influence);
		return influence;
	}
	
	
	@Override
	public Influence getInfluence(String sid, String influenceId) throws NotVisibleException {
		return getInfluence(sid, influenceId, false);
	}

	private void logRequest(LoginResult currentUser, Influence influence) {
		try {
			StringBuffer sb = new StringBuffer();
			long uid = currentUser.getUid();
			if (uid >= 0) {
				sb.append("User #").append(uid).append(": \"").append(currentUser.getUsername()).append("\"");
			} else {
				sb.append("Unregistered User");
			}
			sb.append(" fetched Influence ").append(influence.getId()).append(" (\"")
					.append(influence.getQuestion()).append(",externalid:").append(influence.getExternalId()).append(")");
			logger.Debug(sb.toString());
		} catch (Exception e) {
			logger.Error(e);
		}
	}

	@Override
	public boolean answerInfluence(String sid, long id, List<InfluenceAnswer> givenAnswers) {
		boolean success = true;
		for (InfluenceAnswer answer : givenAnswers) {
			if (answer.isPredefined()) {
				success &= db.addPredefinedAnswer(id, answer.getId());
			} else {
				if (answer.isNewFreeAnswer()) {
					if (answer.getOwnerId() >= 0)
						success &= db.createFreeAnswer(id, answer.getOwnerId(), answer.getAnswer(),
								answer.getVisibility());
					else
						return false;
				} else {
					success &= db.addFreeAnswer(id, answer.getId());
				}
			}
		}
		
		if (sid != null) {// if a user is logged in..
			long uid = getCurrentUser(sid).getUid();
			if (uid != -1) {
				db.addAttendent(id, uid);
				logAnswering(success, id, givenAnswers, uid);
			}
		} else
			logAnswering(success, id, givenAnswers, -1);

		return success;
	}

	private void logAnswering(boolean success, long id, List<InfluenceAnswer> givenAnswers, long uid) {
		try {
			StringBuffer sb = new StringBuffer();
			if (success) {
				sb.append((uid >= 0) ? "User #" + uid : "Unregistered User").append(" answered Influence #").append(id)
						.append(": ");
				for (InfluenceAnswer an : givenAnswers) {
					sb.append(an.getId())
							.append(" (\"")
							.append(an.getAnswer())
							.append(an.isPredefined() ? "\" <predefined>" : an.getOwnerId() >= 0 ? "\" <*new free answer>"
									: "\" <free answer>").append(")");
				}
				logger.Debug(sb.toString());
			}
		} catch (Exception e) {
			logger.Error(e);
		}
	}
}