package de.tud.kom.socom.components.influence;

import java.sql.SQLException;

import de.tud.kom.socom.database.influence.HSQLInfluenceDatabase;
import de.tud.kom.socom.database.influence.InfluenceDatabase;
import de.tud.kom.socom.util.SocomRequest;
import de.tud.kom.socom.util.exceptions.SocomException;
import de.tud.kom.socom.util.exceptions.IllegalParameterException;

public class InfluenceFactory {

	private static final int MAX_UPLOAD_BYTES_DEFAULT = 10 * 1024 * 1024; // 10
																			// mb
																			// default
																			// max
																			// upload
																			// size
	private static InfluenceDatabase db = HSQLInfluenceDatabase.getInstance();

	public static String createInfluence(long uid, long gid, String question, String type, int visibleTo,
			SocomRequest req, boolean template) throws NumberFormatException, SocomException, SQLException {
		int minChoices = 1, maxChoices = 1;
		if (req.containsParam("minchoices") && req.containsParam("maxchoices")) {
			try {
				minChoices = Integer.parseInt(req.getParam("minchoices"));
				maxChoices = Integer.parseInt(req.getParam("maxchoices"));
			} catch (NumberFormatException e) {
				throw new IllegalParameterException("minchoices/maxchoices must be of type integer");
			}
		}

		String context = "", externalId = null;
		if (req.containsParam("contextid"))
			context = req.getParam("contextid");

		boolean allowFreeAnswers = Boolean.parseBoolean(req.getParam("allowfreeanswers"));
		if (allowFreeAnswers) {
			boolean freeAnswersVotable = Boolean.parseBoolean(req.getParam("freeanswersvotable"));
			if (type.equals("text")) {
				int maxlines, maxdigits;
				try {
					maxlines = req.containsParam("maxlines") ? Integer.parseInt(req.getParam("maxlines"))
							: 5;
					maxdigits = req.containsParam("maxdigits") ? Integer.parseInt(req.getParam("maxdigits"))
							: 200;
				} catch (NumberFormatException e) {
					throw new IllegalParameterException("maxlines/maxdigits must be of type integer.");
				}
				externalId = InfluenceFactory.createFreeAnswerableInfluence(uid, gid, context, type,
						question, minChoices, maxChoices, visibleTo, maxlines, maxdigits, freeAnswersVotable,
						-1L, template);
			} else {
				long maxBytes = MAX_UPLOAD_BYTES_DEFAULT;
				if (req.containsParam("maxbytes")) {
					try {
						maxBytes = Long.parseLong(req.getParam("maxbytes"));
					} catch (NumberFormatException e) {
						throw new IllegalParameterException("maxbytes must be of type long.");
					}
				}
				externalId = InfluenceFactory.createFreeAnswerableInfluence(uid, gid, context, type,
						question, minChoices, maxChoices, visibleTo, -1, -1, freeAnswersVotable, maxBytes,
						template);
			}
		} else {
			externalId = InfluenceFactory.createNormalInfluence(uid, gid, context, type, question,
					minChoices, maxChoices, visibleTo, template);
		}
		return externalId;
	}

	public static boolean checkInfluenceConfiguration(int minChoices, int maxChoices,
			boolean allowFreeAnswers, boolean freeAnswersVotable, String type, int maxlines, int maxdigits,
			long maxbytes) throws IllegalParameterException {
		if (minChoices > maxChoices || minChoices < 0 || maxChoices < 0)
			throw new IllegalParameterException("minChoices must be smaller/equal than maxChoices, both > 0");
		if (type.equals("text") && allowFreeAnswers) {
			if (maxlines < 1 || maxdigits < 1) {
				throw new IllegalParameterException("maxLines and maxDigits must be at minimum 1");
			}
		}
		return true;
	}

	private static String createNormalInfluence(long uid, long gameInst, String contextId, String type,
			String question, int minChoices, int maxChoices, int visibleTo, boolean template)
			throws SQLException, IllegalParameterException {
		boolean allowFreeAnswers = false;
		boolean freeAnswersVotable = false;
		int maxlines = 0;
		int maxdigits = 0;
		int maxBytes = -1;
		checkInfluenceConfiguration(minChoices, maxChoices, allowFreeAnswers, freeAnswersVotable, type,
				maxlines, maxdigits, maxBytes);
		return db.prepareInfluence(uid, gameInst, contextId, question, type, allowFreeAnswers, minChoices,
				maxChoices, maxlines, maxdigits, visibleTo, freeAnswersVotable, maxBytes, template);
	}

	private static String createFreeAnswerableInfluence(long uid, long gameInst, String contextId,
			String type, String question, int minChoices, int maxChoices, int visibleTo, int maxLines,
			int maxDigits, boolean votable, long maxBytes, boolean template) throws SQLException,
			IllegalParameterException {
		boolean allowFreeAnswers = true;
		checkInfluenceConfiguration(minChoices, maxChoices, allowFreeAnswers, votable, type, maxLines,
				maxDigits, maxBytes);
		return db.prepareInfluence(uid, gameInst, contextId, question, type, allowFreeAnswers, minChoices,
				maxChoices, maxLines, maxDigits, visibleTo, votable, maxBytes, template);
	}
}
