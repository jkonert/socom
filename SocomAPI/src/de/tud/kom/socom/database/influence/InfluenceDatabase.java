package de.tud.kom.socom.database.influence;

import java.sql.SQLException;

import de.tud.kom.socom.util.datatypes.InfluenceConfiguration;
import de.tud.kom.socom.util.datatypes.InfluenceResult;
import de.tud.kom.socom.util.exceptions.AlreadyStartedException;
import de.tud.kom.socom.util.exceptions.IllegalAccessException;
import de.tud.kom.socom.util.exceptions.IllegalParameterException;
import de.tud.kom.socom.util.exceptions.InfluenceNotAvailableException;
import de.tud.kom.socom.util.exceptions.InfluenceTemplateException;

public interface InfluenceDatabase {

	public String prepareInfluence(long uid, long gid, String context, String question, String type, boolean allowFreeAnswers, int minChoices, int maxChoices,
			int maxlines, int maxdigits, int visibility, boolean freeAnswersVotable, long maxBytes, boolean template) throws SQLException, IllegalParameterException;
	
	public long addPredefinedAnswer(long uid, String externalId, String answer) throws SQLException, InfluenceNotAvailableException, IllegalAccessException;
	
	public boolean removePredefinedAnswer(long uid, String externalid, long answerid) throws SQLException;

	public long addFreeAnswer(long uid, String externalId, String answer) throws SQLException, InfluenceNotAvailableException, InfluenceTemplateException;
	
	public void startInfluence(long uid, String externalId, int duration) throws SQLException, InfluenceNotAvailableException, IllegalAccessException, InfluenceTemplateException;
	
	public void stopInfluence(long uid, String externalId) throws SQLException, InfluenceNotAvailableException, IllegalAccessException, InfluenceTemplateException;
	
	public InfluenceResult fetchResult(long uid, String externalId) throws SQLException, InfluenceNotAvailableException, IllegalAccessException, InfluenceTemplateException;

	public long getMaxUploadSize(String influenceId) throws InfluenceNotAvailableException, SQLException;

	public long getInfluenceId(long uid, String externalId) throws SQLException, InfluenceNotAvailableException, IllegalAccessException;

	public String copyInfluenceTemplate(long uid, long gid, String templateid) throws SQLException, InfluenceNotAvailableException, InfluenceTemplateException;

	public InfluenceConfiguration readConfiguration(long uid, String externalid) throws SQLException, InfluenceNotAvailableException, IllegalAccessException;

	public boolean changeConfiguration(long uid, String externalid, String question, String type,
			int minchoices, int maxchoices, int maxdigits, int maxlines, int maxBytes, int visibility,
			boolean containsAllowFree, boolean allowFree, boolean containsFreeVotable, boolean freeVotable) throws SQLException, InfluenceNotAvailableException, IllegalAccessException, IllegalParameterException, AlreadyStartedException;

}
