package de.tud.kom.socom.web.client.services.statistic;

import com.google.gwt.user.client.rpc.AsyncCallback;


public interface SoComStatisticServiceAsync {

	void getGraph(long gaminstanceid, String sid, AsyncCallback<String> callback);

}