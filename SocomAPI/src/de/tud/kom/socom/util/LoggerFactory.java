package de.tud.kom.socom.util;

import org.apache.log4j.xml.DOMConfigurator;

/*
 * Provides the "service" to return a correctly named Logger instance for the
 * caller class
 * @author Johannes Konert
 */
public class LoggerFactory
{

    private LoggerFactory()
    {
        throw new UnsupportedOperationException();
        // intentionally no instances
    }


    // call this at least once per runtime to have the LoggerFactory read and
    // watch the XML-based config file for Log4J
    public static void initializeLoggerFactory(String cfgFilePath)
    {
        DOMConfigurator.configureAndWatch(cfgFilePath);
    }


    /**
     * Returns a new or existing Logger for caller class
     * 
     * @return returns a Logger instance of a wrapper to Log4J Logger named exactly as the
     *         caller class-path e.g. de.tud.kom.socom.util.LoggerFactory
     */
    public static Logger getLogger()
    {
        Throwable t = new Throwable();
        StackTraceElement[] stack = t.getStackTrace();
        String loggerName = (stack.length > 1) ? stack[1].getClassName()
                : stack[0].getClassName();
        return new Logger(org.apache.log4j.Logger.getLogger(loggerName));
    }

}
