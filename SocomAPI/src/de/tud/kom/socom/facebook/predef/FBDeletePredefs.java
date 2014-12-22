package de.tud.kom.socom.facebook.predef;

import java.io.IOException;

import org.json.JSONObject;

import de.tud.kom.socom.facebook.request.FBRequestBuilder;
import de.tud.kom.socom.facebook.request.FBRequestBuilder.RequestMethod;

public class FBDeletePredefs {
	
	private static final RequestMethod DELETE_METHOD = RequestMethod.DELETE;
	
	/**
	 * deletes a post
	 * @param ident user on whichs behalf the request should be executed
	 * @param postid unique id of post to be deleted
	 * @return json object
	 * @throws IOException
	 */
	public static JSONObject executeDeletePost(FBIdentities ident, String postid) throws IOException {
		FBRequestBuilder builder = FBRequestBuilder.create();
		builder.setMode(DELETE_METHOD);
		builder.setRequestUrl(postid);
		builder.setParameter("access_token", ident.getAccessToken());
		return builder.build().writeOut();
	}
}
