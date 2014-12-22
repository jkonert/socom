package de.tud.kom.socom.web.server;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.OutputDocument;
import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTagType;
import de.tud.kom.socom.web.server.util.Logger;
import de.tud.kom.socom.web.server.util.LoggerFactory;

/**
 * handles servlet-requests for "default" website and delivers the template with
 * GWT javascript etc components inside
 * This is basically the point of "static" website delöivery with dynamic parts
 * inside. Here the HTML skeleton is delivered
 * 
 * @author jkonert
 */
public class SoComWebTemplateServlet extends HttpServlet
{

	private Logger logger = LoggerFactory.getLogger();
	// look in this order for the template..
	String[] PATH_TO_TEMPLATE = {"SoComWeb.html",
								"war/SoComWeb.html",
	                            "war/WEB-INF/SoComWeb.html"};
	Source template;
	
	// whatever we want to manipulate
	Segment title;
	
	OutputDocument out;
	/**
	 * 
	 */
	private static final long serialVersionUID = -2857499347196625311L;

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
					
		try
		{
			// TODO JK: listen to file changes and update source automatically
			for (int i=0; i < PATH_TO_TEMPLATE.length; i++)
			{
//				File f = new File("test.tmp");
//				System.err.println("Path to SocomWeb.html: "+f.getAbsolutePath());			
				
				File temp = new File(PATH_TO_TEMPLATE[i]);
				if (temp.exists())
				{// try to lookup directories above..
					template = new Source(temp);
					out = new OutputDocument(template);
					break;
				}
			}			
			if (template == null) logger.Error("Template not found in any of the pathes!");
			//find title
			List<Element> list = template.getAllElements("title");			
			for(Element e: list)
			{
				title = e.getContent();
				break; // only 1..
			}
			
		} catch (IOException e)
		{
			logger.Error(e);
		}
		
	}

	@Override
	public void service(ServletRequest request, ServletResponse response)
																			throws ServletException,
																			IOException {
		super.service(request, response);
	}
	
	@Override public void doGet(HttpServletRequest  rq, HttpServletResponse  rs)
	{
		doRequest(rq, rs);
	}
	
	@Override public void doPost(HttpServletRequest  rq, HttpServletResponse  rs)
	{
		doRequest(rq, rs);
	}

	private void doRequest(HttpServletRequest rq, HttpServletResponse rs) {
		
		// XXX JK: remember startdate/last file change and return "UNMODIFIED" if file did not change..
		
		rs.setContentType("text/html;charset=UTF-8");				
		
		
			// do add/replace parts that should be fetched/shown e.g. by facebook
			// out.replace(title, "");
			
			// minimize template
			out.remove(template.getAllElements(StartTagType.COMMENT));
			String result = out.toString();
			rs.setContentLength(result.length());			
			try {
				rs.getWriter().append(result);
				rs.getWriter().flush();
			} 
			catch (IOException e) 
			{
				rs.setContentLength(0);
				logger.Error(e);
			}
	}
}