package de.tud.kom.socom.util;

import org.apache.log4j.Priority;


public class Logger
{

    private org.apache.log4j.Logger log4jLogger;

    protected Logger(org.apache.log4j.Logger log4jLogger)
    {
        this.log4jLogger = log4jLogger;
    }
    
    
    public boolean isTrace()
    {
        return log4jLogger.isTraceEnabled();
    }
    
    public boolean isDebug()
    {
        return log4jLogger.isDebugEnabled();
    }
    
    public boolean isInfo()
    {
        return log4jLogger.isInfoEnabled();
    }
    
    public boolean isWarn()
    {
        return log4jLogger.getLevel().getSyslogEquivalent() >= Priority.WARN_INT;
    }
    
    public boolean isFatal()
    {
        return log4jLogger.getLevel().getSyslogEquivalent() >= Priority.FATAL_INT;
    }
    
    public final void Trace(Object message)
    {
        log4jLogger.trace(message);
    }
    
    public final void Trace(Object message, Throwable t)
    {
        log4jLogger.trace(message, t);
    }
    
    public final void Debug(Object message)
    {
        log4jLogger.debug(message);
    }
    
    public final void Debug(Object message, Throwable t)
    {
        log4jLogger.debug(message, t);
    }
    
    
    public final void Info(Object message)
    {
        log4jLogger.info(message);
    }
    
    public final void Info(Object message, Throwable t)
    {
        log4jLogger.info(message, t);
    }
    
    public final void Warn(Object message)
    {
        log4jLogger.warn(message);
    }
    
    public final void Warn(Object message, Throwable t)
    {
        log4jLogger.warn(message, t);
    }
    
    public final void Error(Object message)
    {
        log4jLogger.error(message);
    }
    
    public final void Error(Object message, Throwable t)
    {
        log4jLogger.error(message, t);
    }
    
    
    public final void Fatal(Object message)
    {
        log4jLogger.fatal(message);
    }
    
    public final void Fatal(Object message, Throwable t)
    {
        log4jLogger.fatal(message, t);
    }
    

}
