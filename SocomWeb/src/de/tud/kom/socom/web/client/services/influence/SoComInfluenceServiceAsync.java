package de.tud.kom.socom.web.client.services.influence;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import de.tud.kom.socom.web.client.sharedmodels.Influence;
import de.tud.kom.socom.web.client.sharedmodels.InfluenceAnswer;

public interface SoComInfluenceServiceAsync {

	void getInfluence(String sid, String influenceId, AsyncCallback<Influence> asyncCallback);
	
	void getInfluence(String sid, String influenceId, boolean log, AsyncCallback<Influence> asyncCallback);

	void answerInfluence(String sid, long id, List<InfluenceAnswer> values, AsyncCallback<Boolean> asyncCallback);

	void getInfluences(String sid, boolean includeEndedInfluences,
		int rangeStart, int rangeEnd, AsyncCallback<List<Influence>> callback);

}
