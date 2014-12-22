package de.tud.kom.socom;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.LogManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;

import de.tud.kom.socom.util.Logger;
import de.tud.kom.socom.util.LoggerFactory;
import de.tud.kom.socom.util.ResourceLoader;
import de.tud.kom.socom.util.SocomRequest;

/**
 * Servlet taking Requests
 */
@SuppressWarnings("serial")
@WebServlet(description = "Connection Class for Socom", urlPatterns = { "/SocomCore" })
public class DeploySocom extends HttpServlet {

	private static final String PATH_TO_LOG4J_CONFIGFILE = "./config/log4j.xml";

	private static final Logger logger = LoggerFactory.getLogger();
	private SocomCore socom;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		socom.doRequest(new SocomRequest(request, response));
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		socom.doRequest(new SocomRequest(request, response));
	}

	public static void main(String[] args) {
		initLogger();
//		int port = 8080;
		int port = Integer.parseInt(ResourceLoader.getResource("port"));
		if (args.length > 0)
			port = Integer.valueOf(args[0]);

		// Start Server
		new DeploySocom().startServer(port);

	}

	private static void initLogger() {
		LoggerFactory.initializeLoggerFactory(PATH_TO_LOG4J_CONFIGFILE);
		LogManager.getLogManager().reset(); // use this to diable restfb
											// java.util.logging Logs
	}

	public void startServer(int port) {
		Server server = new Server(port);
		SocketConnector http = new SocketConnector();
		server.addConnector(http);

		// set up gwt handler
		WebAppContext gwthandler = new WebAppContext();		
		gwthandler.setResourceBase("./war");
		gwthandler.setDescriptor("./war/WEB-INF/web.xml");
		gwthandler.setContextPath("/web");
		gwthandler.setParentLoaderPriority(true);

		ServletContextHandler servletHandler = new ServletContextHandler(server, "/servlet", true, true);
		servletHandler.setMaxFormContentSize(50000000 /* 50 MB */);
		servletHandler.addServlet(new ServletHolder(this), "/");

		HandlerList handlers = new HandlerList();
		handlers.addHandler(servletHandler);
		handlers.addHandler(gwthandler);

		server.setHandler(handlers);

		try {
			server.start();
			logger.Info("***Server is running on " + InetAddress.getLocalHost() + "@" + port);
		} catch (UnknownHostException e) {
			logger.Info("***Server is running on port " + port);
		} catch (Throwable t) {
			if (t.getMessage().contains("Permission")) {
				logger.Fatal("Failed to start Server. Right permissions? (Only superusers can use ports < 1024)");
				t.printStackTrace();
			} else {
				logger.Fatal("Failed to start Server. Already Running?");
				t.printStackTrace();
			}
			return;
		}

		this.socom = new SocomCore();
	}

	@Override
	public void init() {
		// called by ServletContainer on init of given servlet object...
	}

	@Override
	public void destroy() {
		finalize();
	}

	@Override
	public void finalize() {
		// everything to do on a shutdown by ServletContainer
		org.apache.log4j.LogManager.shutdown(); // maybe this helps to flush the
												// buffers to files
	}
}
