package de.tud.kom.socom.web.client.services.administration;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import de.tud.kom.socom.web.client.sharedmodels.SimpleUser;


public interface SoComAdministrationServiceAsync {

	void getUsersStartingWith(String c, AsyncCallback<List<SimpleUser>> callback);

	void getUser(String username, AsyncCallback<SimpleUser> callback);

	void changeUserDeletedState(String sid, long uid, int deleteState,
			AsyncCallback<Boolean> callback);

	void getDeletedStates(AsyncCallback<String[]> callback);

//	void getInfluenceCount(AsyncCallback<Integer> callback);
//
//	void getInfluences(int offset, int limit, AsyncCallback<List<Influence>> callback);
//	
//	void getInfluences(String owner, AsyncCallback<List<Influence>> callback);
//	
//	void getInfluencesIncludeDeleted(int offset, int limit, AsyncCallback<List<Influence>> callback);
//
//	void getInfluencesIncludeDeleted(String owner, AsyncCallback<List<Influence>> callback);

	void changeInfluenceAnswerDeletedState(String sid, boolean predefined,
			long answerId, int deleteState, AsyncCallback<Boolean> callback);

	void startInfluence(String sid, long influenceId, long time,
			AsyncCallback<Boolean> callback);

	void stopInfluence(String sid, long influenceId,
			AsyncCallback<Boolean> callback);

	void changeInfluenceAnswerVisibilityState(String sid, boolean predefined,
			long id, int newVisibility, AsyncCallback<Boolean> asyncCallback);
	
}
