package de.tud.kom.socom.util.media.converter;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import de.tud.kom.socom.util.Logger;
import de.tud.kom.socom.util.LoggerFactory;
import de.tud.kom.socom.util.PlatformTools;

public class FFMPEGConverter extends MediaConverter {

	private static final boolean ENABLE_OUPUT = false;
	private static final Logger logger = LoggerFactory.getLogger();

	public FFMPEGConverter(File source, File target) {
		super(source, target);
	}

	public FFMPEGConverter(File source, File target, Callback callback) {
		super(source, target, callback);
	}

	public void convertMp3ToOgg(String source, String target) {
		try {
			target += ".temp.ogg";
			// String cmd = getFFMPEGPath() + " -y -vn -i " + source +
			// " -acodec libvorbis -aq 2 " + target;
			String[] cmd = new String[] { getFFMPEGPath(), "-y", "-vn", "-i", source, "-acodec", "libvorbis", "-aq", "2", target };
			Process p = Runtime.getRuntime().exec(cmd);
			watchProcess(p, ENABLE_OUPUT);

			int returnCode = -1;
			if ((returnCode = p.waitFor()) != 0)
				logger.Warn("Could not convert " + source + " to " + target + " (Return Code: " + returnCode + ")");
			logger.Debug("Finished converting .." + source.substring(source.length() < 40 ? 0 : 30));
			new File(target).renameTo(new File(target.substring(0, target.length() - ".temp.ogg".length())));
			if (!new File(source).delete())
				logger.Warn("Could not delete " + source);

			doCallback();
		} catch (IOException e) {
			logger.Error(e);
		} catch (InterruptedException e) {
			logger.Error(e);
		}
	}

	private void watchProcess(Process p, boolean enableOuput) {
		if (!enableOuput)
			return;
		Scanner errorScan = new Scanner(p.getErrorStream());
		Scanner outputScan = new Scanner(p.getInputStream());
		while (errorScan.hasNext()) {
			String line = errorScan.nextLine();
			logger.Debug(line);
		}
		while (outputScan.hasNext()) {
			String line = outputScan.nextLine();
			logger.Debug(line);
		}
		errorScan.close();
		outputScan.close();
	}

	// ffmpeg only for 64-bit systems in between
	private String getFFMPEGPath() {
		if (PlatformTools.isUnix()) {
			return "./ffmpeg/64bit/ffmpeg";
		} else if (PlatformTools.isWindows()) {
			return "ffmpeg\\64bit\\ffmpeg.exe";
		} else
			return null;
	}
}