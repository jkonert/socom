package de.tud.kom.socom.util.media;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import de.tud.kom.socom.GlobalConfig;
import de.tud.kom.socom.util.LoggerFactory;
import de.tud.kom.socom.util.ResourceLoader;

public class MediaHandler implements GlobalConfig{

	private static final String GAME_IMG_DIR = ResourceLoader.getResource("war_dir") + "/" + DATA_DIR + "/" + GAME_IMAGE_DIR;

	public static String saveGameImage(String game, String version, String extension, InputStream imageStream) {
		File imageDir = new File(GAME_IMG_DIR);
		checkDir(imageDir);

		int i = 0;
		File f = null;
		while ((f = new File(imageDir, game + "_" + version + "_image" + i++ + "." + extension)).exists())
			;
		copyFile(f, imageStream);
		return GAME_IMAGE_DIR + "/" + f.getName();
	}

	public static String saveContextImage(String game, String version, String contextid, String extension, InputStream imageStream) {
		File imageDir = new File(GAME_IMG_DIR);
		checkDir(imageDir);

		int i = 0;
		File f = null;
		while ((f = new File(imageDir, game + "_" + version + "_context_" + contextid + "_image" + i++ + "." + extension)).exists())
			;

		copyFile(f, imageStream);
		return GAME_IMAGE_DIR + "/" + f.getName();
	}

	private static void checkDir(File imageDir) {
		if (!imageDir.exists())
			imageDir.mkdirs();
	}

	private static File copyFile(File f, InputStream imageStream) {
		try {
			OutputStream out = new FileOutputStream(f);

			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = imageStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}

			imageStream.close();
			out.flush();
			out.close();
		} catch (IOException e) {
			LoggerFactory.getLogger().Error(e);
		}
		return f;
	}
}
