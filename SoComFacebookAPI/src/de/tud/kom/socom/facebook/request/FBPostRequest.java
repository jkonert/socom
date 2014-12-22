package de.tud.kom.socom.facebook.request;

import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;

import de.tud.kom.socom.facebook.request.FBRequestBuilder.ParameterValue;

public class FBPostRequest extends FBRequest {

	public FBPostRequest(String url, Map<String, ParameterValue<?>> params) {
		super(url, params);
	}

	@Override
	public HttpRequestBase buildHttpRequest() throws URISyntaxException{
		return buildHttpPostRequest();
	}

	private HttpRequestBase buildHttpPostRequest() throws URISyntaxException {
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		
		includePostParameterMap(builder);
		
		HttpEntity entity = builder.build();
		HttpPost post = new HttpPost(getRequestUrl());
		post.setEntity(entity);
		return post;
	}

	private void includePostParameterMap(MultipartEntityBuilder builder) {
		for(String key : params.keySet()){
			ParameterValue<?> valueEnvelope = params.get(key);
			Object valueObject = valueEnvelope.getValue();
			ContentBody valueBody = packValue(valueObject);
			builder.addPart(key, valueBody);
		}
	}

	private ContentBody packValue(Object valueObject) {
		ContentBody body = null;
		if(valueObject instanceof String) {
			body = new StringBody((String)valueObject, ContentType.DEFAULT_TEXT);
		} else if (valueObject instanceof File) {
			body = new FileBody((File)valueObject);
		} else if (valueObject instanceof InputStream) {
			body = new InputStreamBody((InputStream)valueObject, "photos");
		} else if (valueObject instanceof byte[]) {
			//XXX is null here ok?
			body = new ByteArrayBody((byte[])valueObject, null);
		}
		return body;
	}
}
