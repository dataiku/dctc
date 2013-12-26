package com.dataiku.dip.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Enumeration;
import java.util.ResourceBundle;

import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.LoggingEvent;

/**
 * A variant of the Log4J logger that adds a "relative call timestamp" to messages.
 * 
 * To use, call DKULogger.startCurrentCall() to record the "beginning of call timestamp".
 * Until you call DKULogger.endCurrentCall(), all infos will contain [ct: time_in_ms]
 * 
 * Also adds some variadic methods with printf formatting: infoV, debugV, warnV, errorV
 */
public class DKULogger extends Logger {

    public static DKULogger getLogger(String loggerName) {
       return new DKULogger(Logger.getLogger(loggerName));
    }

    protected DKULogger(Logger innerLogger) {
        super(innerLogger.getName());
        this.delegate = innerLogger;
    }

    private final List<DKULoggerFilter> filters = new ArrayList<DKULoggerFilter>();
    private final Logger delegate;

    static private class CallTime {
        long start;
    }
    public static void startCurrentCall() {
        if (tl.get() == null) tl.set(new CallTime());
        tl.get().start = System.currentTimeMillis();
    }
    public static void endCurrentCall() {
        if (tl.get() != null) {
            tl.remove();
        }
    }
    private static ThreadLocal<CallTime> tl = new ThreadLocal<CallTime>();

    private Object concatTime(Object message) {
        if (tl.get() != null && tl.get().start != 0) {
            String header = "[ct: " + (System.currentTimeMillis() - tl.get().start) + "]";
            return header + " " + message;
        }
        return message;
    }

    public  DKULogger addFilter(DKULoggerFilter filter) {
        this.filters.add(filter);
        return this;
    }

    public boolean accept(Object message) {
        for (DKULoggerFilter filter: this.filters) {
            if (!filter.accept(message)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void addAppender(Appender newAppender) {
        delegate.addAppender(newAppender);
    }

    @Override
    public void assertLog(boolean assertion, String msg) {
        delegate.assertLog(assertion, msg);
    }

    @Override
    public void callAppenders(LoggingEvent arg0) {
        delegate.callAppenders(arg0);
    }

    @Override
    public void debug(Object message, Throwable t) {
        if (this.accept(message)) {
            delegate.debug(concatTime(message), t);
        }
    }

    @Override
    public void debug(Object message) {
        if (this.accept(message)) {
            delegate.debug(concatTime(message));
        }
    }

    public void debugV(String message, Object... format) {
        this.debug(String.format(message, format));
    }

    @Override
    public boolean equals(Object obj) {
        return delegate.equals(obj);
    }

    @Override
    public void error(Object message, Throwable t) {
        if (this.accept(message)) {
            delegate.error(message, t);
        }
    }

    @Override
    public void error(Object message) {
        if (this.accept(message)) {
            delegate.error(message);
        }
    }

    public void errorV(String message, Object... format) {
        this.error(String.format(message, format));
    }

    @Override
    public void fatal(Object message, Throwable t) {
        if (this.accept(message)) {
            delegate.fatal(message, t);
        }
    }

    @Override
    public void fatal(Object message) {
        if (this.accept(message)) {
            delegate.fatal(message);
        }
    }

    @Override
    public boolean getAdditivity() {
        return delegate.getAdditivity();
    }

    @Override
    public Enumeration<?> getAllAppenders() {
        return delegate.getAllAppenders();
    }

    @Override
    public Appender getAppender(String name) {
        return delegate.getAppender(name);
    }

    @SuppressWarnings("deprecation")
    public Priority getChainedPriority() {
        return delegate.getChainedPriority();
    }

    @Override
    public Level getEffectiveLevel() {
        return delegate.getEffectiveLevel();
    }

    @SuppressWarnings("deprecation")
    public LoggerRepository getHierarchy() {
        return delegate.getHierarchy();
    }

    @Override
    public LoggerRepository getLoggerRepository() {
        return delegate.getLoggerRepository();
    }

    @Override
    public ResourceBundle getResourceBundle() {
        return delegate.getResourceBundle();
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public void info(Object message, Throwable t) {
        if (this.accept(message)) {
            delegate.info(concatTime(message), t);
        }
    }

    @Override
    public void info(Object message) {
        if (this.accept(message)) {
            delegate.info(concatTime(message));
        }
    }


    public void infoV(String message, Object... format) {
        this.info(String.format(message, format));
    }

    @Override
    public boolean isAttached(Appender appender) {
        return delegate.isAttached(appender);
    }

    @Override
    public boolean isDebugEnabled() {
        return delegate.isDebugEnabled();
    }

    @Override
    public boolean isEnabledFor(Priority level) {
        return delegate.isEnabledFor(level);
    }

    @Override
    public boolean isInfoEnabled() {
        return delegate.isInfoEnabled();
    }

    @Override
    public boolean isTraceEnabled() {
        return delegate.isTraceEnabled();
    }

    @Override
    public void l7dlog(Priority arg0, String arg1, Object[] arg2, Throwable arg3) {
        delegate.l7dlog(arg0, arg1, arg2, arg3);
    }

    @Override
    public void l7dlog(Priority arg0, String arg1, Throwable arg2) {
        delegate.l7dlog(arg0, arg1, arg2);
    }

    @Override
    public void log(Priority priority, Object message, Throwable t) {
        if (this.accept(message)) {
            delegate.log(priority, message, t);
        }
    }

    @Override
    public void log(Priority priority, Object message) {
        if (this.accept(message)) {
            delegate.log(priority, message);
        }
    }

    @Override
    public void log(String callerFQCN, Priority level, Object message,
            Throwable t) {
        if (this.accept(message)) {
            delegate.log(callerFQCN, level, message, t);
        }
    }

    @Override
    public void removeAllAppenders() {
        delegate.removeAllAppenders();
    }

    @Override
    public void removeAppender(Appender appender) {
        delegate.removeAppender(appender);
    }

    @Override
    public void removeAppender(String name) {
        delegate.removeAppender(name);
    }

    @Override
    public void setAdditivity(boolean additive) {
        delegate.setAdditivity(additive);
    }

    @Override
    public void setLevel(Level level) {
        delegate.setLevel(level);
    }

    @SuppressWarnings("deprecation")
    public void setPriority(Priority priority) {
        delegate.setPriority(priority);
    }

    @Override
    public void setResourceBundle(ResourceBundle bundle) {
        delegate.setResourceBundle(bundle);
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

    @Override
    public void trace(Object message, Throwable t) {
        if (this.accept(message)) {
            delegate.trace(message, t);
        }
    }

    @Override
    public void trace(Object message) {
        if (this.accept(message)) {
            delegate.trace(message);
        }
    }

    @Override
    public void warn(Object message, Throwable t) {
        if (this.accept(message)) {
            delegate.warn(message, t);
        }
    }

    @Override
    public void warn(Object message) {
        if (this.accept(message)) {
            delegate.warn(message);
        }
    }

    public void warnV(String message, Object... format) {
        this.warn(String.format(message, format));
    }

}
