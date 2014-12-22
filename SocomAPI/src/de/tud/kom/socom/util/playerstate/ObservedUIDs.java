package de.tud.kom.socom.util.playerstate;

import java.sql.SQLException;
import java.util.HashMap;

import de.tud.kom.socom.database.user.HSQLUserDatabase;
import de.tud.kom.socom.util.Logger;
import de.tud.kom.socom.util.LoggerFactory;

@SuppressWarnings("serial")
public class ObservedUIDs extends HashMap<Long, Long> {

	private static final ObservedUIDs instance = new ObservedUIDs();
	private static Logger logger;

	private ObservedUIDs() {
		logger = LoggerFactory.getLogger();
	}

	public static ObservedUIDs getInstance() {
		return instance;
	}

	public void put(Long uid) {
		long now = System.currentTimeMillis();
		super.put(uid, now);
	}

	public void setOnline(Long uid) {
		if (!super.containsKey(uid)) {
			try {
				HSQLUserDatabase.getInstance().setUserOnline(uid);
			} catch (SQLException e) {
				logger.Error(e);
			}
		}
		this.put(uid);
	}

	public Long getTime(Long uid) {
		return super.get(uid);
	}

	public void setOffline(Long uid) {
		super.remove(uid);
		try {
			HSQLUserDatabase.getInstance().setUserOffline(uid);
		} catch (SQLException e) {
			logger.Error(e);
		}
	}
}
