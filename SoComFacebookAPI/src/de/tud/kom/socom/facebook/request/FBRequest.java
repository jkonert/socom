package de.tud.kom.socom.facebook.request;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.apache.http.client.methods.HttpRequestBase;
import org.json.JSONException;
import org.json.JSONObject;

import de.tud.kom.socom.facebook.request.FBRequestBuilder.ParameterValue;
import de.tud.kom.socom.facebook.request.http.HTTPExecutor;

public abstract class FBRequest {
	
	static final String FACEBOOK_GRAPH_URL = "https://graph.facebook.com/";
	String url;
	Map<String, ParameterValue<?>> params;

	public FBRequest(String url, Map<String, ParameterValue<?>> params){
		this.url = url;
		this.params = params;
	}
	
	public abstract HttpRequestBase buildHttpRequest() throws URISyntaxException;
	
	URI getRequestUrl() throws URISyntaxException {
		String requestUrl = FACEBOOK_GRAPH_URL + url;
		return new URI(requestUrl);
	}
	
	public JSONObject writeOut() throws IOException {
		try {
			
			HttpRequestBase buildHttpRequest = buildHttpRequest();
			String response = HTTPExecutor.executeHttpRequest(buildHttpRequest);
			if (response == null)
			{
				throw new IOException("response was null");
			}
			if(response.matches("true|false"))	
			{
				response = "{\"success\":" + response + "}";
			}
			return new JSONObject(response);
		} catch (URISyntaxException e) {
			throw new IOException(e.getMessage());
		} catch (JSONException e) {
			throw new IOException(e.getMessage());
		}
	}
}
