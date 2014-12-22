package de.tud.kom.socom.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import de.tud.kom.socom.database.content.GameContentDatabase;
import de.tud.kom.socom.database.content.HSQLGameContentDatabase;
import de.tud.kom.socom.util.exceptions.ContentNotAvailableException;
import de.tud.kom.socom.util.exceptions.ContentNotFoundException;
import de.tud.kom.socom.util.exceptions.IllegalAccessException;
import de.tud.kom.socom.util.media.converter.FFMPEGConverter;
import de.tud.kom.socom.util.media.converter.MediaConverter;

public class ContentMediaConverter {

	private static GameContentDatabase db = HSQLGameContentDatabase.getInstance();
	private static Logger logger = LoggerFactory.getLogger();

	public static void convertIfNecessary(long id) throws SQLException, ContentNotFoundException {
		String type = db.getType(id);

		if (type.equalsIgnoreCase("audio")) {
			try {
				convertAudio(id);
			} catch (IllegalAccessException e) {
				logger.Error(e);
			}
		}
		// else if (type.equalsIgnoreCase("image")){
		// convertPicture(id);
		// }
		// add more convertions if needed
	}

	private static void convertAudio(final long id) throws SQLException, IllegalAccessException {
		String filename = (System.currentTimeMillis() % 100000) + ".temp";
		File source = new File(filename);
		final File target = new File(filename + ".ogg");
		try {
			byte[] content = db.downloadContent(-2, id, false);
			FileOutputStream fos = new FileOutputStream(source);
			fos.write(content);
			fos.flush();
			fos.close();
		} catch (ContentNotAvailableException e) {
			logger.Error("While converting audio: " + e);
			return;
		} catch (IOException e) {
			logger.Error("While converting audio: " + e);
			return;
		}
		MediaConverter converter = new FFMPEGConverter(source, target, new MediaConverter.Callback() {

			@Override
			public void doJob() {
				try {
					db.setContent(id, new FileInputStream(target));
					target.delete();
				} catch (FileNotFoundException e) {
					logger.Error("While saving converted audio: " + e);
				} catch (SQLException e) {
					logger.Error("While saving converted audio: " + e);
				}
			}
		});
		converter.start();
	}

}
