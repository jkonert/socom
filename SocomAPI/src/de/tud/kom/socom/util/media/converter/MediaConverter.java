package de.tud.kom.socom.util.media.converter;

import java.io.File;

public abstract class MediaConverter extends Thread {

	private String source, target;
	private Callback callback;

	public MediaConverter(File file, File newFile) {
		super();
		this.source = file.getAbsolutePath();
		this.target = newFile.getAbsolutePath();
		this.callback = null;
	}

	public MediaConverter(File file, File newFile, Callback callback) {
		super();
		this.source = file.getAbsolutePath();
		this.target = newFile.getAbsolutePath();
		this.callback = callback;
	}

	public void run() {
		convertMp3ToOgg(source, target);
	}

	public void doCallback() {
		if (callback != null)
			callback.doJob();
	}

	public abstract void convertMp3ToOgg(String source, String target);

	public interface Callback {
		public void doJob();
	}
}
