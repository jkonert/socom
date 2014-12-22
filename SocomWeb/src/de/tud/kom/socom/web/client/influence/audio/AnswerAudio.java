package de.tud.kom.socom.web.client.influence.audio;

/** A simple wrapper class for all parameters needed to render an AudioAnswer VIEW
 * (because it has a comment/message AND an Audiofile)
 * 
 * @author jkonert
 *
 */
public class AnswerAudio
{

	private String path;
	private String message;

	public AnswerAudio(String pathToFile, String message)
	{
		this.path = pathToFile;
		this.message = message;
	}

	public String getPath() {
		return path;
	}

	public String getMessage() {
		return message;
	}
}
