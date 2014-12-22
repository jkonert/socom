package de.tud.kom.socom.util.datatypes;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

import de.tud.kom.socom.SocomCore;
import de.tud.kom.socom.util.JSONUtils;
import de.tud.kom.socom.util.LoggerFactory;
import de.tud.kom.socom.util.enums.ContentCategory;

public class GameContent implements JSONString {

        private long contentId, contextId, ownerId;
        private ContentCategory category;
        private String title, description, type, owner;
        private Map<String, String> metadata;
        private Date timestamp;
        private int hits, ratingCount;
        private double rating, currentUsersRating;
        private List<GameContent.ContentComment> comments;

        public GameContent(long contentId, long contextId, long ownerId, String title, String description, ContentCategory category, String type, String owner, Map<String, String> metadata,
                        Date timestamp, int hits, int ratingCount, double rating, double currentUsersRating) {
                this.contentId = contentId;
                this.contextId = contextId;
                this.ownerId = ownerId;
                this.title = title;
                this.description = description;
                this.category = category;
                this.type = type;
                this.owner = owner;
                this.metadata = metadata;
                this.timestamp = timestamp;
                this.hits = hits;
                this.ratingCount = ratingCount;
                this.rating = rating;
                this.currentUsersRating = currentUsersRating;
                comments = new LinkedList<GameContent.ContentComment>();
        }

        public void addComment(GameContent.ContentComment comment) {
                comments.add(comment);
        }

        public void setRatingCount(int ratingCount) {
                this.ratingCount = ratingCount;
        }
        
        public void setCategory(ContentCategory cate){
        	this.category = cate;
        }

        public void setRating(double rating) {
                this.rating = rating;
        }

        public void setCurrentUsersRating(double currentUsersRating) {
                this.currentUsersRating = currentUsersRating;
        }

        public long getContentId() {
                return contentId;
        }

        public long getContextId() {
                return contextId;
        }

        public long getOwnerId() {
                return ownerId;
        }

        public String getTitle() {
                return title;
        }

        public String getDescription() {
                return description;
        }

        public String getType() {
                return type;
        }

        public String getOwner() {
                return owner;
        }

        public Map<String, String> getMetadata() {
                return metadata;
        }

        public Date getTimestamp() {
                return timestamp;
        }

        public int getHits() {
                return hits;
        }

        public int getRatingCount() {
                return ratingCount;
        }

        public double getRating() {
                return rating;
        }

        public double getCurrentUsersRating() {
                return currentUsersRating;
        }

        @Override
        public String toJSONString() {
                try {
                        JSONObject json = new JSONObject();
                        json.put("contentid", contentId);
                        json.put("contextid", contextId);
                        json.put("ownerid", ownerId);
                        json.put("owner", owner);
                        json.put("title", title);
                        json.put("description", description);
                        json.put("category", category.name());
                        json.put("type", type);
                        json.put("metadata", metadata);
                        json.put("timestamp", SocomCore.getDateFormat().format(timestamp));
                        json.put("hits", hits);
                        json.put("ratingCount", ratingCount);
                        json.put("rating", rating);
                        json.put("usersRating", currentUsersRating);
                        json.put("comments", comments);
                        return JSONUtils.JSONToString(json);
                } catch (JSONException e) {
                        LoggerFactory.getLogger().Error(e);
                }
                return null;
        }

        public class ContentComment implements JSONString {

                private long id, uid, contentid;
                private String user, text;
                private Date time;

                public ContentComment(long id, long uid, long contentid, String user, String text, Date time) {
                        this.id = id;
                        this.uid = uid;
                        this.user = user;
                        this.contentid = contentid;
                        this.text = text;
                        this.time = time;
                }

                @Override
                public String toJSONString() {
                        try {
                                JSONObject json = new JSONObject();
                                json.put("id", id);
                                json.put("userid", uid);
                                json.put("user", user);
                                json.put("contentid", contentid);
                                json.put("time", SocomCore.getDateFormat().format(time));
                                json.put("text", text);
                                return JSONUtils.JSONToString(json);
                        } catch (JSONException e) {
                                LoggerFactory.getLogger().Error(e);
                        }
                        return null;
                }
        }
}