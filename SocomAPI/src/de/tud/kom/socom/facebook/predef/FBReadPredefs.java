package de.tud.kom.socom.facebook.predef;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.tud.kom.socom.facebook.request.FBRequestBuilder;
import de.tud.kom.socom.facebook.request.FBRequestBuilder.RequestMethod;

public class FBReadPredefs {
	
	private static final String CURRENT_USER = "me";
	private static final RequestMethod READ_METHOD = RequestMethod.GET;
	
	/**
	 * fetch essential information about a user
	 * @param ident which user should perform the request
	 * @return json object (https://developers.facebook.com/docs/reference/api/user)
	 * @throws IOException
	 */
	public static JSONObject executeReadUser(FBIdentities ident) throws IOException {
		return executeReadUser(ident, CURRENT_USER);
	}

	/**
	 * fetch essential information about the current user
	 * @param ident which user should perform the request
	 * @param user unique of the user to fetch information from
	 * @return json object (https://developers.facebook.com/docs/reference/api/user)
	 * @throws IOException
	 */
	public static JSONObject executeReadUser(FBIdentities ident, String user) throws IOException {
		JSONObject json = executeReadFullUser(ident, user);
		json.remove("link");
		json.remove("third_party_id");
		json.remove("verified");
		json.remove("devices");
		json.remove("political");
		json.remove("payment_pricepoints");
		json.remove("payment_mobile_pricepoints");
		json.remove("picture");
		json.remove("quotes");
		json.remove("relationship_status");
		json.remove("religion");
		json.remove("security_settings");
		json.remove("significant_other");
		json.remove("video_upload_limits");
		json.remove("website");
		json.remove("work");
		return json;
	}
	
	/**
	 * fetch all information about the current user provided by facebook
	 * @param ident which user should perform the request
	 * @return json object (https://developers.facebook.com/docs/reference/api/user)
	 * @throws IOException
	 */
	public static JSONObject executeReadFullUser(FBIdentities ident) throws IOException {
		return executeReadFullPost(ident, CURRENT_USER);
	}
	
	/**
	 * fetch all information about a user provided by facebook
	 * @param ident which user should perform the request
	 * @param user unique of the user to fetch information from
	 * @return json object (https://developers.facebook.com/docs/reference/api/user)
	 * @throws IOException
	 */
	public static JSONObject executeReadFullUser(FBIdentities ident, String user) throws IOException {
		FBRequestBuilder builder = FBRequestBuilder.create();
		builder.setMode(READ_METHOD);
		builder.setRequestUrl(user);
		builder.setParameter("access_token", ident.getAccessToken());
		return builder.build().writeOut();
	}
	
	
	/**
	 * counts and reads the friends of the current user
	 * @param ident which user should perform the request
	 * @return json object with friends array
	 * @throws IOException
	 */
	public static JSONObject executeReadFriends(FBIdentities ident) throws IOException {
		return executeReadFriends(ident, CURRENT_USER);
	}
	
	/**
	 * counts and reads the friends
	 * (most likely its not possible to fetch other users friends although they might be public) 
	 * 
	 * @param ident which user should perform the request
	 * @param user id of the user to get friendlist of (if its possible)
	 * @return json object with friends array
	 * @throws IOException
	 */
	public static JSONObject executeReadFriends(FBIdentities ident, String user) throws IOException {
		FBRequestBuilder builder = FBRequestBuilder.create();
		builder.setMode(READ_METHOD);
		builder.setRequestUrl(user + "/friends");
		builder.setParameter("access_token", ident.getAccessToken());
		builder.setParameter("limit", "100000000");
		JSONObject json = builder.build().writeOut();
		try {
			json.remove("paging");
			long count = json.getJSONArray("data").length();
			json.put("count", count);
		} catch (JSONException e){
			e.printStackTrace();
		}
		return json;
	}
	
	/**
	 * counts and shows the likes, displaying the liking users
	 * @param ident which user should perform the request
	 * @param postid unique id of the post to fetch likes from
	 * @return json object with likes and a count
	 * @throws IOException
	 */
	public static JSONObject executeReadLikes(FBIdentities ident, String postid) throws IOException  {
		FBRequestBuilder builder = FBRequestBuilder.create();
		builder.setMode(READ_METHOD);
		builder.setRequestUrl(postid + "/likes");
		builder.setParameter("access_token", ident.getAccessToken());
		builder.setParameter("summary", "1");
		builder.setParameter("limit", "1000000");
		JSONObject json = builder.build().writeOut();
		try {
			json.remove("paging");
			long count = json.getJSONObject("summary").getLong("total_count");
			json.put("count", count);
			json.remove("summary");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}
	
	
	/**
	 * reads the comments of a post
	 * @param ident which user should perform the request
	 * @param postid unique id of the post to fetch comments of
	 * @return json object with array of comments
	 * @throws IOException
	 */
	public static JSONObject executeReadComments(FBIdentities ident, String postid) throws IOException  {
		JSONObject json = executeReadFullPost(ident, postid);
		JSONObject jsonComments = null;
		try {
			if(json.has("comments"))
				jsonComments = json.getJSONObject("comments");
			else
				jsonComments = new JSONObject().put("data", new JSONArray());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		jsonComments.remove("paging");
		return jsonComments;
	}
	
	/**
	 * reads only essential information of a post
	 * @param ident which user should perform the request
	 * @param postid  unique id of the post to read
	 * @return json object with less essential information about the post
	 * @throws IOException
	 */
	public static JSONObject executeReadPost(FBIdentities ident, String postid) throws IOException  {
		JSONObject json = executeReadFullPost(ident, postid);
		json.remove("comments");
//		json.remove("likes");

		json.remove("message_tags");
		json.remove("caption");
		json.remove("description");
		json.remove("icon");
		json.remove("actions");
		json.remove("privacy");
		json.remove("place");
		json.remove("story");
		json.remove("story_tags");
		json.remove("with_tags");
		json.remove("object_id");
		json.remove("application");
		json.remove("properties");
		json.remove("shares");
		json.remove("is_hidden");
		json.remove("status_type");
		return json;
	}
	
	/**
	 * reads the full description provided by facebook to a given post
	 * @param ident which user should perform the request
	 * @param postid unique id of the post to read
	 * @return json object of a post (https://developers.facebook.com/docs/reference/api/post/)
	 * @throws IOException
	 */
	public static JSONObject executeReadFullPost(FBIdentities ident, String postid) throws IOException {
		FBRequestBuilder builder = FBRequestBuilder.create();
		builder.setMode(READ_METHOD);
		builder.setRequestUrl(postid);
		builder.setParameter("access_token", ident.getAccessToken());
		builder.setParameter("comments_limit", "1000000");
		return builder.build().writeOut();
	}
}
