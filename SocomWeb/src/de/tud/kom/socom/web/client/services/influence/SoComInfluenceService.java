package de.tud.kom.socom.web.client.services.influence;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import de.tud.kom.socom.web.client.sharedmodels.Influence;
import de.tud.kom.socom.web.client.sharedmodels.InfluenceAnswer;
import de.tud.kom.socom.web.client.util.exceptions.NotVisibleException;


@RemoteServiceRelativePath("influence")
public interface SoComInfluenceService extends RemoteService {
	
	Influence getInfluence(String sid, String influenceId) throws NotVisibleException;
	Influence getInfluence(String sid, String influenceId, boolean log) throws NotVisibleException;
	List<Influence> getInfluences(String sid, boolean includeEndedInfluences, int rangeStart, int rangeEnd) throws NotVisibleException;
	public boolean answerInfluence(String sid, long id, List<InfluenceAnswer> values);

}
