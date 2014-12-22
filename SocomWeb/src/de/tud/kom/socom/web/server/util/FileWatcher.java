package de.tud.kom.socom.web.server.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FileWatcher {

	private static final int TIMEOUT_IN_SECONDS = 60 * 120; // 2 hours
	private Map<File, Long> filesToWatch;
	private static FileWatcher instance = new FileWatcher();

	private FileWatcher() {
		this.filesToWatch = new HashMap<File, Long>();
		new FileWatcherThread().start();
	}

	public static FileWatcher getInstance() {
		return instance;
	}

	public void addFile(File f) {
		long timeout = System.currentTimeMillis() + 1000 * TIMEOUT_IN_SECONDS;
		filesToWatch.put(f, timeout);
	}

	private class FileWatcherThread extends Thread {
		@Override
		public void run() {
			long currentTime = System.currentTimeMillis();
			for (File f : filesToWatch.keySet()) {
				if (filesToWatch.get(f) < currentTime) {
					filesToWatch.remove(f);
					f.delete();
					LoggerFactory.getLogger().Debug("Deleted File " + f.getPath());
				}
			}
		}
	}
}
