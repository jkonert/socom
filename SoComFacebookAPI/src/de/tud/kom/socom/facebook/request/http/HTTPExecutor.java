package de.tud.kom.socom.facebook.request.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;

public class HTTPExecutor {

	/**
	 * execute a http request and return answer or null if status code > 300
	 * 
	 * @param request
	 *            to execute including url & parameter
	 * @return response or null if error occured somewhere
	 * @throws IOException
	 */
	public static String executeHttpRequest(HttpUriRequest request) throws IOException {
		HttpClient client = HttpClientBuilder.create().setMaxConnPerRoute(1337).build();
		
		try {
			HttpResponse response = client.execute(request);

			InputStream in = response.getEntity().getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));

			String line = null;
			StringBuffer bf = new StringBuffer();
			while ((line = reader.readLine()) != null) {
				bf.append(line);
			}
			
			return bf.toString();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
		return null;
	}
}