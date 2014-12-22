package de.tud.kom.socom.facebook.request;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


public class FBRequestBuilder {
	
	class ParameterValue<T> {
		private T value;

		private ParameterValue(T value){
			this.value = value;
		}

		T getValue() {
			return this.value;
		}
	}
	
	public enum RequestMethod {
		GET, POST, DELETE //to be continued if necessary
	}
	
	private Map<String, ParameterValue<?>> params;
	private String requestUrl;
	private RequestMethod httpmethod;
	
	private FBRequestBuilder(){
		params = new HashMap<String, ParameterValue<?>>();
		httpmethod = RequestMethod.GET;
	}
	
	/**
	 * create a request which can be sent through the HTTPExecutor
	 * @return request object or null if requestUrl is missing (empty) or requestmethod was null
	 */
	public FBRequest build() {
		if(requestUrl == null || requestUrl.isEmpty())
			return null;
		FBRequest req = null;
		switch(this.httpmethod) {
		case GET:
			req = new FBGetRequest(requestUrl, params);
			break;
		case POST:
			req = new FBPostRequest(requestUrl, params);
			break;
		case DELETE:
			req = new FBDeleteRequest(requestUrl, params);
			break;
		default:
			break;
		}
		return req;
	}
	
	/**
	 * create a builder
	 */
	public static FBRequestBuilder create() {
		return new FBRequestBuilder();
	}
	
	/**
	 * request url following https://graph.facebook.com/
	 * e.g. "me/photos"
	 */
	public FBRequestBuilder setRequestUrl(String subUrl) {
		if(subUrl != null && subUrl.length() > 0 && subUrl.startsWith("/"))
			subUrl = subUrl.substring(1);
		this.requestUrl = subUrl;
		return this;
	}
	
	/**
	 * set string parameter
	 */
	public FBRequestBuilder setParameter(String key, String param) {
		params.put(key, new ParameterValue<String>(param));
		return this;
	}
	
	/**
	 * set file parameter
	 */
	public FBRequestBuilder setParameter(String key, File file) {
		params.put(key, new ParameterValue<File>(file));
		return this;
	}
	
	/**
	 * set stream parameter
	 */
	public FBRequestBuilder setParameter(String key, InputStream is) {
		params.put(key, new ParameterValue<InputStream>(is));
		return this;
	}
	
	/**
	 * set byte array parameter
	 */
	public FBRequestBuilder setParameter(String key, byte[] bytes) {
		params.put(key, new ParameterValue<byte[]>(bytes));
		return this;
	}
	
	/**
	 * set the mode: get, post or delete something, further may come
	 */
	public FBRequestBuilder setMode(RequestMethod mode){
		this.httpmethod = mode;
		return this;
	}
}
