package de.tud.kom.socom.facebook.predef;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONObject;

import de.tud.kom.socom.facebook.request.FBRequestBuilder;
import de.tud.kom.socom.facebook.request.FBRequestBuilder.RequestMethod;

public class FBPublishPredefs {

	private static final String CURRENT_USER = "me";
	private static final RequestMethod PUBLISH_METHOD = RequestMethod.POST;

	/**
	 * Publish a message on default feed
	 * @param ident which user should execute the request
	 * @param message message to publish
	 * @return json answer
	 */
	public static JSONObject executePublishMessage(FBIdentities ident, String message) throws IOException {
		return executePublish(ident, CURRENT_USER, message, null, null, null, null);
	}

	/**
	 * Publish a message on a feed
	 * @param ident which user should execute the request
	 * @param toContext feed where the post should be sendet, e.g. a page-id
	 * @param message message to publish
	 * @return json answer
	 */
	public static JSONObject executePublishMessage(FBIdentities ident, String toContext, String message) throws IOException {
		return executePublish(ident, toContext, message, null, null, null, null);
	}
	
	/**
	 * Publish a message with an attached link on default feed
	 * @param ident which user should execute the request
	 * @param message message to publish
	 * @param url link which should be attached
	 * @return json answer
	 */
	public static JSONObject executePublishLinkMessage(FBIdentities ident, String message, String url) throws IOException {
		return executePublish(ident, CURRENT_USER, message, url, null, null, null);
	}
	
	/**
	 * Publish a message with an attached link on a feed
	 * @param ident which user should execute the request
	 * @param toContext feed where the post should be sendet, e.g. a page-id
	 * @param message message to publish
	 * @param url link which should be attached
	 * @return json answer
	 */
	public static JSONObject executePublishLinkMessage(FBIdentities ident, String toContext, String message, String url) throws IOException {
		return executePublish(ident, toContext, message, url, null, null, null);
	}
	
	/**
	 * Publish a message with an attached media on default feed
	 * @param ident which user should execute the request
	 * @param message message to publish
	 * @param file with media which should be published
	 * @return json answer
	 */
	public static JSONObject executePublishMediaMessage(FBIdentities ident, String message, File file, FBMediaType mediatype) throws IOException {
		return executePublish(ident, CURRENT_USER, message, null, file, null, mediatype);
	}
	
	/**
	 * Publish a message with an attached media on a feed
	 * @param ident which user should execute the request
	 * @param toContext feed where the post should be sendet, e.g. a page-id
	 * @param message message to publish
	 * @param file with media which should be published
	 * @return json answer
	 */
	public static JSONObject executePublishMediaMessage(FBIdentities ident, String toContext, String message, File file, FBMediaType mediatype) throws IOException {
		if(mediatype != FBMediaType.photos && mediatype != FBMediaType.videos) return null;
		return executePublish(ident, toContext, message, null, file, null, mediatype);
	}
	
	/**
	 * Publish a message with an attached media on a feed
	 * @param ident which user should execute the request
	 * @param toContext feed where the post should be sendet, e.g. a page-id
	 * @param message message to publish
	 * @param url link which should be attached
	 * @param file with media which should be published
	 * @return json answer
	 */
	public static JSONObject executePublishLinkAndMediaMessage(FBIdentities ident, String toContext, String message, String url, File file, FBMediaType mediatype) throws IOException {
		if(mediatype != FBMediaType.photos && mediatype != FBMediaType.videos) return null;
		return executePublish(ident, toContext, message, url, file, null, mediatype);
	}
	
	/**
	 * Publish a message with an attached media on a feed
	 * @param ident which user should execute the request
	 * @param message message to publish
	 * @param url optional: link which should be attached (null if without link)
	 * @param is stream with media which should be published
	 * @return json answer
	 */
	public static JSONObject executePublishLinkAndMediaMessage(FBIdentities ident, String message, String url, InputStream is, FBMediaType mediatype) throws IOException {
		if(mediatype != FBMediaType.photos && mediatype != FBMediaType.videos) return null;
		return executePublish(ident, CURRENT_USER, message, url, null, is, mediatype);
	}
	
	
	/**
	 * Publish a message with an attached media on a feed
	 * @param ident which user should execute the request
	 * @param toContext feed where the post should be sendet, e.g. a page-id
	 * @param message message to publish
	 * @param url optional: link which should be attached (null if without link)
	 * @param is stream with media which should be published
	 * @return json answer
	 */
	public static JSONObject executePublishLinkAndMediaMessage(FBIdentities ident, String toContext, String message, String url, InputStream is, FBMediaType mediatype) throws IOException {
		if(mediatype != FBMediaType.photos && mediatype != FBMediaType.videos) return null;
		return executePublish(ident, toContext, message, url, null, is, mediatype);
	}
	
	/**
	 * Publish a comment to a post
	 * @param ident which user should perform the request
	 * @param post unique id of the post to comment on
	 * @param message comment message
	 * @return json answer
	 * @throws IOException
	 */
	public static JSONObject executePublishComment(FBIdentities ident, String post, String message) throws IOException {
		FBRequestBuilder builder = FBRequestBuilder.create();
		builder.setMode(PUBLISH_METHOD);
		builder.setRequestUrl(post + "/comments");
		builder.setParameter("access_token", ident.getAccessToken());
		builder.setParameter("message", message);
		return builder.build().writeOut();
	}
	
	private static JSONObject executePublish(FBIdentities ident, String toContext, String message, String url,  File file, InputStream is, FBMediaType mediatype) throws IOException {
		if(file != null && is != null)
			return null;
		
		FBRequestBuilder builder = FBRequestBuilder.create();
		builder.setMode(PUBLISH_METHOD);
		builder.setRequestUrl(toContext + "/" + (mediatype == null ? "feed" : mediatype.name()));
		builder.setParameter("access_token", ident.getAccessToken());
		if(message != null && !message.isEmpty()) builder.setParameter("message", message);
		if(url != null) builder.setParameter("link", url);
		if(file != null) builder.setParameter("source", file);
		if(is != null) builder.setParameter("source", is);
		return builder.build().writeOut();
	}
}
