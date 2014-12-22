package de.tud.kom.socom.web.client.services.statistic;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;


@RemoteServiceRelativePath("stats")
public interface SoComStatisticService extends RemoteService {
	
	public String getGraph(long gaminstanceid, String sid);
}
