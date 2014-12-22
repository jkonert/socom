package de.tud.kom.socom.components.influence;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.json.JSONException;
import org.json.JSONObject;

import de.tud.kom.socom.GlobalConfig;
import de.tud.kom.socom.database.influence.HSQLInfluenceDatabase;
import de.tud.kom.socom.database.influence.InfluenceDatabase;
import de.tud.kom.socom.util.SocomRequest;
import de.tud.kom.socom.util.JSONUtils;
import de.tud.kom.socom.util.ResourceLoader;
import de.tud.kom.socom.util.exceptions.SocomException;
import de.tud.kom.socom.util.exceptions.IllegalAccessException;
import de.tud.kom.socom.util.exceptions.IllegalFileSizeException;
import de.tud.kom.socom.util.exceptions.MediaTypeNotSupportedException;
import de.tud.kom.socom.util.media.converter.FFMPEGConverter;
import de.tud.kom.socom.util.media.converter.MediaConverter;
import eu.medsea.mimeutil.MimeUtil;

public class InfluenceUploadHandler {

	private static InfluenceDatabase db = HSQLInfluenceDatabase.getInstance();
	private static final long MAX_SIZE_PICTURE_UPLOAD = 1024 /* kb */* 1024 /* MB */* 6; // 10MB
	private static final long MAX_SIZE_AUDIO_UPLOAD = 1024 * 1024 * 15;
	static final File DATA_DIR = new File(GlobalConfig.DATA_DIR + "/" + GlobalConfig.INFLUENCE_DATA_DIR);
	/* 
	 * Add predefined data via api-upload
	 */
	public static int addPredefinedData(SocomRequest req) throws IOException, SQLException, JSONException {
		try {
			long uid = req.getUid();
			String externalid = req.getCookieVal("id");
			String answer = req.getCookieVal("answer").replaceAll(";", ",");
			String fileext = req.getCookieVal("fileextension");
			InputStream is = req.getInputStream();

			File f = getInfluenceFile(externalid, fileext);
			writeFile(f, is);
			String mimeType = checkMimetype(f);
			String fileName = getFilenameAndConvertIfNeeded(f, mimeType);
			// extend answer with filename (regex is ';')
			answer += ";" + externalid + "/" + fileName;
			long answerid = db.addPredefinedAnswer(uid, externalid, answer);

			req.addOutput(JSONUtils.JSONToString(new JSONObject().put("answerid", answerid)));
		} catch (SocomException e) {
			return e.getErrorCode();
		}
		return 0;
	}

	/*
	 * add free answer data via web interface upload
	 */
	public static int addFreeData(SocomRequest req) throws IOException, SQLException, JSONException, FileUploadException {
		try {
			Map<String, FileItem> params = req.getMultipartContent();

			FileItem messageI = params.get("message");
			FileItem influenceIdI = params.get("influenceid");
			FileItem dataI = params.get("data");
			if(dataI == null) dataI = params.get("Filedata"); // used when drag n drop upload
			FileItem uidI = params.get("uid");
//			FileItem secretI = params.get("secret");
			String secretI = ""; //non-null
			
			if (messageI == null || influenceIdI == null || dataI == null || uidI == null || secretI == null) {
				throw new IllegalAccessException();
			}

			String temporaryContentType = dataI.getContentType();
			boolean seemsImage = isImageFile(temporaryContentType);
			boolean seemsAudio = isAudioFile(temporaryContentType);
			if (!seemsImage && !seemsAudio) {
				throw new MediaTypeNotSupportedException(temporaryContentType);
			}

			long uid = Long.parseLong(uidI.getString());
			//FIXME no validation (see /SocomWeb/src/de/tud/kom/socom/web/client/influence/InfluencePresenter.java Line 515)
//			String secretHash = secretI.getString();
//			if(!HSQLUserDatabase.getInstance().validateUser(uid, secretHash))
//				throw new UIDOrSecretNotValidException();
			
			String message = messageI.getString().replaceAll(";", ",");
			String influenceId = influenceIdI.getString();
			InputStream data = dataI.getInputStream();
			String ending = dataI.getName().substring(dataI.getName().lastIndexOf('.') + 1);
			File file = getInfluenceFile(influenceId, ending);

			long maxBytes = db.getMaxUploadSize(influenceId);
			writeFileWithLimitedSize(file, data, seemsImage, seemsAudio, maxBytes);
	
			String mimeType = checkMimetype(file);
			String fileName = getFilenameAndConvertIfNeeded(file, mimeType);
			String filePath = influenceId + "/" + fileName;
			String answer = message + ";" + filePath;
			long newAnswerId = db.addFreeAnswer(uid, influenceId, answer);
			req.addOutput(JSONUtils.JSONToString(new JSONObject().put("success", true).put("message", message).put("file", filePath).put("id", newAnswerId)));
		} catch (SocomException e) {
			return e.getErrorCode();
		}
		return 0;
	}

	/*
	 * helpers to write file
	 */
	private static void writeFile(File file, InputStream data) throws FileNotFoundException, IOException {
		OutputStream fos = new FileOutputStream(file);
		byte[] buffer = new byte[1024];
		int len = -1;
		while ((len = data.read(buffer)) != -1) {
			fos.write(buffer, 0, len);
		}
		fos.close();
	}

	private static void writeFileWithLimitedSize(File file, InputStream data, boolean seemsImage, boolean seemsAudio, long maxBytesPre)
			throws FileNotFoundException, IOException, IllegalFileSizeException {
		OutputStream fos = new FileOutputStream(file);
		byte[] buffer = new byte[1024];
		int len = -1;
		long maxBytes = maxBytesPre != -1 ? maxBytesPre : (seemsImage ? MAX_SIZE_PICTURE_UPLOAD : seemsAudio ? MAX_SIZE_AUDIO_UPLOAD : -1);
		while ((len = data.read(buffer)) != -1) {
			fos.write(buffer, 0, len);
			maxBytes -= len;
			if (maxBytes < 0) {
				fos.close();
				file.delete();
				throw new IllegalFileSizeException(maxBytes + "MB");
			}
		}
		fos.close();
	}

	/*
	 * get filename, make sure all directories exist
	 */
	private static File getInfluenceFile(String externalid, String fileext) {
		File dir = new File(ResourceLoader.getResource("war_dir") + "/" + DATA_DIR);
		if (!dir.exists())
			dir.mkdir();

		dir = new File(dir, externalid);
		if (!dir.exists())
			dir.mkdir();

		String[] dataList = dir.list(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().startsWith("data_");
			}
		});
		int dataCount = dataList.length;
		File f;
		while ((f = new File(dir, "data_" + dataCount++ + "." + fileext)).exists())
			;
		return f;
	}

	/*
	 * convert file to acceptable format if needed, return (new) filename
	 */
	private static String getFilenameAndConvertIfNeeded(File file, String mimeType) {
		String fileName = file.getName();
		if (isMp3AudioFile(mimeType)) {
			fileName = convertMp3ToOgg(file);
		}
		// add more convertions if needed..
		return fileName;
	}

	/*
	 * helpers to determine mimetype
	 */
	private static String checkMimetype(File file) throws MediaTypeNotSupportedException {
		String mimeType = getMimetype(file);
		//TODO JK: determination finds application/octet-stream instead of detailed type.. what to do? (RH)
		if (!isImageFile(mimeType) && !isAudioFile(mimeType)) {
			file.delete();
			throw new MediaTypeNotSupportedException(mimeType);
		}
		return mimeType;
	}

	private static String getMimetype(File file) {
		MimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");
		Collection<?> mimeTypes = MimeUtil.getMimeTypes(file);
		return String.valueOf(mimeTypes);
	}

	private static String convertMp3ToOgg(File file) {
		String newFileName = file.getName().substring(0, file.getName().length() - 4) + ".ogg";
		File newFile = new File(file.getParentFile(), newFileName);
		/*
		 * hotfix for existing files, just append old filename to a incrementing index until filename is free
		 */
		int fixExistingFileIndex = 0;
		while(newFile.exists()) {
			newFile = new File(file.getParentFile(), fixExistingFileIndex++ + newFileName );
		}
		MediaConverter converter = new FFMPEGConverter(file, newFile);
		converter.start();
		return newFile.getName();
	}

	private static boolean isImageFile(String contentType) {
		return contentType.equalsIgnoreCase("image/gif") || contentType.equalsIgnoreCase("image/jpeg") || contentType.equalsIgnoreCase("image/png");
	}

	private static boolean isAudioFile(String contentType) {
		return isOggAudioFile(contentType) || isMp3AudioFile(contentType);
	}

	private static boolean isOggAudioFile(String contentType) {
		return contentType.endsWith("/ogg");
	}

	private static boolean isMp3AudioFile(String contentType) {
		return contentType.equalsIgnoreCase("audio/mpeg") || contentType.equalsIgnoreCase("audio/x-mpeg") || contentType.equalsIgnoreCase("audio/mp3")
				|| contentType.equalsIgnoreCase("audio/x-mp3") || contentType.equalsIgnoreCase("audio/mpeg3") || contentType.equalsIgnoreCase("audio/x-mpeg3")
				|| contentType.equalsIgnoreCase("audio/mpg") || contentType.equalsIgnoreCase("audio/x-mpg")
				|| contentType.equalsIgnoreCase("audio/x-mpegaudio");
	}

}
