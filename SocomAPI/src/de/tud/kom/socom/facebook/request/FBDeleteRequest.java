package de.tud.kom.socom.facebook.request;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Map;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpRequestBase;

import de.tud.kom.socom.facebook.request.FBRequestBuilder.ParameterValue;

public class FBDeleteRequest extends FBRequest {

	public FBDeleteRequest(String url, Map<String, ParameterValue<?>> params) {
		super(url, params);
	}

	@Override
	public HttpRequestBase buildHttpRequest() throws URISyntaxException {
		return buildHttpDeleteRequest();
	}

	private HttpRequestBase buildHttpDeleteRequest() throws URISyntaxException {
		String url = appendParameterMap(getRequestUrl());
		HttpDelete delete = new HttpDelete(url);
		return delete;
	}

	private String appendParameterMap(URI requestUrl) {
		String url = requestUrl.toString() + "?";
		for (String key : params.keySet()) {
			try {
				ParameterValue<?> valueEnvelope = params.get(key);
				String value = (String) valueEnvelope.getValue();
				url += key + "=" + URLEncoder.encode(value, "UTF-8") + "&";
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return url;
	}
}
