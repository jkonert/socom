package de.tud.kom.socom.web.client.influence.image;

/** A simple wrapper class for all parameters needed to render an ImageAnswer VIEW
 * (because it has a comment/message AND an Image file)
 * 
 * @author jkonert
 *
 */
public class AnswerImage
{

	private String path;
	private String message;

	public AnswerImage(String pathToFile, String message)
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
