package de.tud.kom.socom.util.playerstate;

import de.tud.kom.socom.util.LoggerFactory;

public class PlayerStateWatcher extends Thread {

	private static final PlayerStateWatcher instance = new PlayerStateWatcher();
	private static final int REFRESH_SECONDS = 120; // every 120 seconds the
													// users
													// are observed for being
													// still online.
	private static final int TIMEOUT_MINUTES = 20; // timeout-time to set a user
													// listed
													// as offline
	private static ObservedUIDs uids;

	private PlayerStateWatcher() {
		uids = ObservedUIDs.getInstance();
	}

	public static PlayerStateWatcher getInstance() {
		return instance;
	}

	@Override
	public void run() {
		long waitFor = 1000 * REFRESH_SECONDS;

		while (true) {
			long timeout = System.currentTimeMillis() - (TIMEOUT_MINUTES * 60 * 1000);

			for (Long uid : uids.keySet()) {
				long lastOnline = uids.getTime(uid);
				if (lastOnline < timeout) {
					uids.setOffline(uid);
					// FIXME ConcurrentModificationException
				}
			}

			try {
				Thread.sleep(waitFor);
			} catch (InterruptedException e) {
				LoggerFactory.getLogger().Error(e);
			}
		}
	}

}
