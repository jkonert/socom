package de.tud.kom.socom.facebook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import de.tud.kom.socom.facebook.predef.FBDeletePredefs;
import de.tud.kom.socom.facebook.predef.FBIdentities;
import de.tud.kom.socom.facebook.predef.FBMediaType;
import de.tud.kom.socom.facebook.predef.FBPublishPredefs;

public class FBTest {

	public static void main(String[] args) {
		FBIdentities
				.addFBIdent(
						"user_0",
						// "{{insert access_token here}}");
						"CAACEdEose0cBAIBh8t3sKIILQ9EL3nWSp7rsdCGt9ehARCcjDkVZC7xzidvZCNh9bjZClznVfZCrSEoceyCmzprTa4mXUak5wDpjIA6QLbp46lZCgYDSUwNyqGadNmZCKPaD3TmFKqdqdGOvxWo7ZABvZCWaUSFmkhdXHLXHpw44k7IHczVdmZCXZAPE0ZAedyRUqRJwtseZCnzjtwZDZD");
		try {
			File f = new File("wiki-commons.png");
			if (!f.exists())
				System.out.println(f.getAbsolutePath() + " doesnt exist");
			JSONObject json = FBPublishPredefs.executePublishLinkAndMediaMessage(FBIdentities.getFBIdent("user_0"), "me",
					"test1", null, new FileInputStream(f), FBMediaType.photos);
			System.out.println(json.toString(1));
			JSONObject json1 = FBDeletePredefs.executeDeletePost(FBIdentities.getFBIdent("user_0"),
					json.getString("id"));
			System.out.println(json1.toString(1));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}