package de.tud.kom.socom.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.GeneralSecurityException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.eclipse.jetty.util.Utf8Appendable.NotUtf8Exception;

import de.tud.kom.socom.GlobalConfig;
import de.tud.kom.socom.util.exceptions.CookieNotFoundException;
import de.tud.kom.socom.util.exceptions.CurrentGameInstanceNotIncludedException;
import de.tud.kom.socom.util.exceptions.IllegalAccessException;
import de.tud.kom.socom.util.exceptions.IllegalParameterException;
import de.tud.kom.socom.util.exceptions.ParseException;
import de.tud.kom.socom.util.exceptions.UIDNotIncludedException;

/**
 * 
 * @author rhaban
 * 
 */
public class SocomRequest {

	private static final String SESSION_GAME_KEY = "game";
	private static final String SESSION_UID_KEY = "id";
	private static final String CONTENT_RETURN_TYPE_JSON_TEXT = "text/html"; // text/html works better with browsers to parse JSON... "application/json";
	private HttpServletRequest request;
	private HttpServletResponse response;
	private Map<String, String> params = null;
	private StringBuilder out;
	private String url;
	private long uid, currentGameInst;
	private Logger logger = LoggerFactory.getLogger();
	private StringBuffer debugLog;

	public SocomRequest(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
		
		this.url = request.getServletPath();
		this.uid = determineID();
		this.currentGameInst = determineCurrentGameInst();
		this.out = new StringBuilder();
		
		if(GlobalConfig.DEBUG) {
			showRequest();
		}
	}

	private void showRequest() {
		StringBuffer sb = new StringBuffer();
		try {
			if(uid != -1) sb.append("Request: User #").append(uid).append(" | ");
			else sb.append("Request: No User | ");
			sb.append(getHandlerString() + "/" + getMethodString() + " (");
			
			if(request.getMethod().contains("GET")) { // do ONLY on GET, otherwise the stream will be unusable
				try {
					for(String param : getParams().keySet()) {
						sb.append("\"").append(param).append("\"=\"").append(getParam(param)).append("\" ");
					}
				} catch (NotUtf8Exception e) {	}
			} else if(request.getMethod().contains("POST")) {
				sb.append("Cookies: ");
				for(Cookie param : request.getCookies()) {
						sb.append("\"").append(param.getName()).append("\"=\"").append(param.getValue()).append("\" ");
				}				
			}
			sb.append(") [").append(request.getMethod()).append("]");
			this.debugLog = sb;
		}		
		catch (Throwable t) {
			logger.Error("Show Request abort: " + t);
		}
	}
	
	private void showResponse(String response) {
		debugLog.append("\nResponse: ").append(response);
		logger.Debug(debugLog.toString());
	}
	

	private long determineID() {
		Object inSessionId = request.getSession().getAttribute(SESSION_UID_KEY);
		if (inSessionId == null)
			return -1L;

		byte[] encUid = (byte[]) inSessionId;
		long uid = Long.parseLong(EasyEncrypter.getInstance().decryptString(encUid));

		return uid;
	}

	private long determineCurrentGameInst() {
		Object currentGameInst = request.getSession().getAttribute(SESSION_GAME_KEY);
		if (currentGameInst == null)
			currentGameInst = -1L;
		return (Long) currentGameInst;
	}

	private String parseMethod() throws ParseException {
		String method = null;
		try {
			method = url.split("/")[2];
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new ParseException("method");
		}
		return method;
	}

	private String parseHandler() throws ParseException {
		String handler = null;
		try {
			handler = url.split("/")[1];
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new ParseException("handler");
		}
		return handler;
	}

	/**
	 * parse all parameter of the requests into a (key,value) map.
	 * 
	 * @return map with params
	 * @throws ParseException 
	 */
	private Map<String, String> parseParams() throws ParseException {
		Map<String, String> params = new HashMap<String, String>();
		Enumeration<String> keys = request.getParameterNames();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			try
			{
				String param = request.getParameter(key);
				if (param != null)
				{
					param = URLDecoder.decode(param, "UTF-8");
					params.put(key, param);
				}
			}
			catch (NotUtf8Exception e)
			{
				// TODO make this a childclass of ParseException saying that UTF8 is needed
				throw new ParseException(key);
				
			} 
			catch (UnsupportedEncodingException e)
			{
				// TODO make this a childclass of ParseException saying that UTF8 is needed
				throw new ParseException(key);
			}
					
					
			
		}
		return params;
	}

	/**
	 * append output
	 * 
	 * @param output
	 *            string you want to write
	 */
	public void addOutput(String output) {
		out.append(output);
	}

	/**
	 * overwrites already included output with given string
	 * 
	 * @param output
	 *            new output string
	 */
	public void setOutput(String output) {
		out = new StringBuilder();
		addOutput(output);
	}

	/**
	 * finishes the request
	 */
	public void finish() {
		try {
			if (!out.toString().isEmpty()) {
				response.setContentType(CONTENT_RETURN_TYPE_JSON_TEXT);
				response.setCharacterEncoding("UTF-8");
				response.getWriter().print(out.toString());
				response.flushBuffer();

				if(GlobalConfig.DEBUG)
					showResponse(out.toString());
			} else
				getOutputStream().flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return the params as map
	 * @throws ParseException 
	 */
	public Map<String, String> getParams() throws ParseException {
		if (params == null)
			params = parseParams();
		return params;
	}

	public String getParam(String key) throws IllegalParameterException {
		// String result = getParams().get(key);
		String result = request.getParameter(key);
		if (result == null)
			throw new IllegalParameterException(key);
		try {
			result = URLDecoder.decode(result, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO make this a childclass of ParseException saying that UTF8 is needed
			throw new IllegalParameterException(key);
		}
		return result;
	}
	
	public String getParam(String key, String defaultValue) {
		String result = request.getParameter(key);
		if (result == null) return defaultValue;
		return result;
	}
	
	public long getParam(String key, long defaultValue) {
		String result = request.getParameter(key);
		if (result == null) return defaultValue;
		return Long.parseLong(result); 
	}
	
	public int getParam(String key, int defaultValue) {
		String result = request.getParameter(key);
		if (result == null) return defaultValue;
		return Integer.parseInt(result);
	}
	
	public boolean getParam(String key, boolean defaultValue) {
		String result = request.getParameter(key);
		if (result == null) return defaultValue;
		return Boolean.parseBoolean(result);
	}

	public boolean containsParam(String key) throws ParseException {
		return getParams().containsKey(key);
	}

	/**
	 * @return parses the handler
	 * @throws ParseException
	 */
	public String getHandlerString() throws ParseException {
		return parseHandler();
	}

	/**
	 * @return the method
	 * @throws ParseException
	 */
	public String getMethodString() throws ParseException {
		return parseMethod();
	}

	/**
	 * gives the inputstream if its a post
	 * 
	 * @return inputstream
	 * @throws IOException
	 */
	public InputStream getInputStream() throws IOException {
		return request.getInputStream();
	}

	/**
	 * gives the output stream
	 * 
	 * @return the output stream
	 */
	public OutputStream getOutputStream() {
		try {
			return response.getOutputStream();
		} catch (IOException e) {
			setOutput(e.getMessage());
			return null;
		}
	}

	/**
	 * sets the filename for file sent through outputstream
	 * @param filename
	 */
	public void setOutputFilename(String filename) {
		response.setHeader("Content-Disposition", "fileName=" + filename);
	}

	/**
	 * @return the id
	 * @throws UIDNotIncludedException
	 */
	public long getUid() throws UIDNotIncludedException {
		if (uid == -1)
			throw new UIDNotIncludedException();
		return uid;
	}

	/**
	 * @param uid
	 *            the id to set
	 * @throws GeneralSecurityException
	 * @throws UnsupportedEncodingException
	 */
	public void setUid(long uid) {
		this.uid = uid;
		byte[] encUid = EasyEncrypter.getInstance().encryptString(String.valueOf(uid));
		request.getSession().setAttribute(SESSION_UID_KEY, encUid);
	}
	
	public void removeUid() {
		request.getSession().removeAttribute(SESSION_UID_KEY);
	}

	/**
	 * @return the current game instance
	 * @throws CurrentGameInstanceNotIncludedException
	 */
	public long getCurrentGameInst() throws CurrentGameInstanceNotIncludedException {
		if (currentGameInst == -1)
			throw new CurrentGameInstanceNotIncludedException();
		return currentGameInst;
	}

	/**
	 * @param uid
	 *            the id to set
	 */
	public void setCurrentGameInst(long currentGameInst) {
		this.currentGameInst = currentGameInst;
		request.getSession().setAttribute(SESSION_GAME_KEY, this.currentGameInst);
	}
	
	public void removeGameInst() {
		request.getSession().removeAttribute(SESSION_GAME_KEY);
	}

	/**
	 * @return the value of cookie with given key
	 * @throws CookieNotFoundException
	 */
	public String getCookieVal(String key) throws CookieNotFoundException {
		for (Cookie c : request.getCookies())
			if (c.getName().equalsIgnoreCase(key))
				return c.getValue();
		throw new CookieNotFoundException(key);
	}

	/**
	 * adds a cookie to response
	 */
	public void addCookie(String key, String value) {
		response.addCookie(new Cookie(key, value));
	}

	public boolean isMultipartContent() {
		return ServletFileUpload.isMultipartContent(request);
	}

	public Map<String, FileItem> getMultipartContent() throws FileUploadException, IllegalAccessException {

		if (!ServletFileUpload.isMultipartContent(request))
			throw new IllegalAccessException();

		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);

		Map<String, FileItem> result = new HashMap<String, FileItem>();

		@SuppressWarnings("unchecked")
		List<FileItem> items = upload.parseRequest(request);
		for (FileItem fi : items) {
			result.put(fi.getFieldName(), fi);
		}
		return result;
	}
}