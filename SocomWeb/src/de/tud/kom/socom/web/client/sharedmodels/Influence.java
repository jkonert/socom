package de.tud.kom.socom.web.client.sharedmodels;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

import de.tud.kom.socom.web.client.baseelements.presenters.ListItemInterface;

/**
 * 
 * @author rhaban, creuter, Johannes Konert
 * 
 */
public class Influence implements IsSerializable, ListItemInterface {

	private long id, gameId, contextId, ownerId;
	private String externalId, gameName, contextName, contextImage, ownerName, question, type;
	private boolean allowfreeanswers, freeAnswersVotable;
	private short minChoices, maxChoices;
	private int maxDigits, maxLines, visibility;
	private List<InfluenceAnswer> predefinedAnswers, freeAnswers;
	private Date timeout;
	private InfluenceAnswer mostVoted;
	private long[] attendees;

	public Influence() {
	}

	public Influence(long id, String externalId, long gameId, String gameName, long contextId, String contextName, String contextImage, long ownerId,
			String ownerName, String question, String type, boolean allowfreeanswers, short minChoices, short maxChoices, int maxDigits, int maxLines,
			Date timeout, int visibility, boolean freeAnswersVotable, String attendees) {
		this.id = id;
		this.externalId = externalId;
		this.gameId = gameId;
		this.gameName = gameName;
		this.contextId = contextId;
		this.contextName = contextName;
		this.contextImage = contextImage;
		this.ownerId = ownerId;
		this.ownerName = ownerName;
		this.question = question;
		this.type = type;
		this.allowfreeanswers = allowfreeanswers;
		this.minChoices = minChoices;
		this.maxChoices = maxChoices;
		this.maxDigits = maxDigits;
		this.maxLines = maxLines;
		this.timeout = timeout;
		this.visibility = visibility;
		this.freeAnswersVotable = freeAnswersVotable;
		this.predefinedAnswers = new ArrayList<InfluenceAnswer>();
		this.freeAnswers = new ArrayList<InfluenceAnswer>();
		setAttendees(attendees);
	}
	
	private void setAttendees(String attendees) {
		if(attendees.isEmpty()){
			this.attendees = new long[0];
			return;
		}
		String[] atts = attendees.split(";");
		this.attendees = new long[atts.length];
		for(int i = 0; i < atts.length; i++)
			this.attendees[i] = Long.parseLong(atts[i]);
	}
	
	public long[] getAttendees(){
		return this.attendees;
	}

	public boolean allowFreeAnswers() {
		return allowfreeanswers;
	}

	public boolean freeAnswersVotable() {
		return freeAnswersVotable;
	}

	public short getMinChoices() {
		return minChoices;
	}

	public int getVisibility() {
		return visibility;
	}

	public void addPredefinedAnswer(InfluenceAnswer answer) {
		predefinedAnswers.add(answer);
	}
	
	public void addFreeAnswer(InfluenceAnswer answer) {
		freeAnswers.add(answer);
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @return the external id
	 */
	public String getExternalId() {
		return externalId;
	}

	/**
	 * @return the games id
	 */
	public long getGameId() {
		return gameId;
	}

	/**
	 * @return the games name
	 */
	public String getGameName() {
		return gameName;
	}

	/**
	 * @return the contexts id
	 */
	public long getContextId() {
		return contextId;
	}

	/**
	 * @return the contexts name
	 */
	public String getContextName() {
		return contextName;
	}

	/**
	 * @return the contexts image
	 */
	public String getContextImage() {
		return contextImage;
	}

	/**
	 * @return the owners id
	 */
	public long getOwnerId() {
		return ownerId;
	}

	/**
	 * @return the owners name
	 */
	public String getOwnerName() {
		return ownerName;
	}

	/**
	 * @return the question
	 */
	public String getQuestion() {
		return question;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return whether free answers are allowed
	 */
	public boolean getAllowFreeAnswers() {
		return allowfreeanswers;
	}

	/**
	 * @return the max choices
	 */
	public short getMaxChoices() {
		return maxChoices;
	}

	/**
	 * @return the max digits
	 */
	public int getMaxDigits() {
		return maxDigits;
	}

	/**
	 * @return the max lines
	 */
	public int getMaxLines() {
		return maxLines;
	}

	/**
	 * @return the predefined answers
	 */
	public List<InfluenceAnswer> getPredefinedAnswers() {
		return predefinedAnswers;
	}

	/**
	 * @return the timeout
	 */
	public Date getTimeout() {
		return timeout;
	}
	
	/**
	 * 
	 * @return true if Influence was ever once started, false if never started by now.
	 */
	public boolean IsStarted()
	{
		return timeout.after(new Date(0));
	}
	
	/**
	 * 
	 * @return true, if Influence was ever once started and time is up.
	 */
	public boolean IsEnded()
	{
		return IsStarted() && timeout.before(new Date());
	}
	
	/**
	 * 
	 * @return true if Influence was started and has still time left to run
	 */
	public boolean IsRunning()
	{
		return IsStarted() & !IsEnded();
	}
	
	/**
	 * 
	 * @return  -1 if Influence is notrRunning currently, otherwise the time in milliseconds left to end
	 */
	public long getTimeMillisLeft()
	{		
		if (!IsRunning()) return -1;
		long result = timeout.getTime()-new Date().getTime();
		if (result < 0) result = -1;
		return result;
	}

	public List<InfluenceAnswer> getFreeAnswers() {
		return freeAnswers;
	}
	
	public InfluenceAnswer getFreeAnswer(long id) {
		for(InfluenceAnswer ans : freeAnswers)
			if(ans.getId() == id)
				return ans;
		return null;
	}
	
	public InfluenceAnswer getPredefinedAnswer(long id) {
		for(InfluenceAnswer ans : predefinedAnswers)
			if(ans.getId() == id)
				return ans;
		return null;
	}
	
	public boolean getFreeAnswersVotable() {
		return freeAnswersVotable;
	}
	
	public InfluenceAnswer getWinner() {
		if(mostVoted == null) {
			for(InfluenceAnswer a : predefinedAnswers)
				if(mostVoted == null || a.getResult().getVotes() > mostVoted.getResult().getVotes())
					mostVoted = a;
			for(InfluenceAnswer a : freeAnswers)
				if(mostVoted == null || a.getResult().getVotes() > mostVoted.getResult().getVotes())
					mostVoted = a;
		}
		return mostVoted;
	}

	public int getTotalNumberOfGivenVotes() {
		int votes = 0;
		for(InfluenceAnswer a : predefinedAnswers)
		{
			AnswerResult r = a.getResult();
			votes += r==null?0:r.getVotes();
		}
		for(InfluenceAnswer a : freeAnswers)
		{
			AnswerResult r = a.getResult();
			votes += r==null?0:r.getVotes();
		}
		return votes;
	}

	public boolean isAttendee(long userId) {
		if(attendees == null) return false;
		for(int i = 0; i < attendees.length; i++){
			if(attendees[i] == userId) return true;
		}
		return false;
	}


}











