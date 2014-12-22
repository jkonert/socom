package de.tud.kom.socom.facebook.request;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Map;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;

import de.tud.kom.socom.facebook.request.FBRequestBuilder.ParameterValue;

public class FBGetRequest extends FBRequest {

	public FBGetRequest(String url, Map<String, ParameterValue<?>> params) {
		super(url, params);
	}

	@Override
	public HttpRequestBase buildHttpRequest() throws URISyntaxException {
		return buildHttpGetRequest();
	}

	private HttpRequestBase buildHttpGetRequest() throws URISyntaxException {
		String url = appendParameterMap(getRequestUrl());
		HttpGet get = new HttpGet(url);
		return get;
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
