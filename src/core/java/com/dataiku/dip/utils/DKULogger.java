package com.dataiku.dip.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.LoggingEvent;

public class DKULogger extends Logger {
    public static DKULogger getLogger(String loggerName) {
       return new DKULogger(Logger.getLogger(loggerName));
    }
    
    protected DKULogger(Logger innerLogger) {
        super(innerLogger.getName());
        this.delegate = innerLogger;
    }
    protected DKULogger(String name) {
        super(name);
    }
    
    private Logger delegate;

    static private class CallTime {
        long start;
    }
    public static void startCurrentCall() {
        if (tl.get() == null) tl.set(new CallTime());
        tl.get().start =System.currentTimeMillis();
    }
    public static void endCurrentCall() {
        if (tl.get() != null) tl.get().start = 0;
    }
    private static ThreadLocal<CallTime> tl = new ThreadLocal<DKULogger.CallTime>();
    
    private Object concatTime(Object message) {
        if (tl.get() != null && tl.get().start != 0) {
            String header = "[ct: " + (System.currentTimeMillis() - tl.get().start) + "]";
            return header + " " + message;
        }
        return message;
    }

    public void addAppender(Appender newAppender) {
        delegate.addAppender(newAppender);
    }

    public void assertLog(boolean assertion, String msg) {
        delegate.assertLog(assertion, msg);
    }

    public void callAppenders(LoggingEvent arg0) {
        delegate.callAppenders(arg0);
    }

    public void debug(Object message, Throwable t) {
        delegate.debug(concatTime(message), t);
    }

    public void debug(Object message) {
        delegate.debug(concatTime(message));
    }

    public boolean equals(Object obj) {
        return delegate.equals(obj);
    }

    public void error(Object message, Throwable t) {
        delegate.error(message, t);
    }

    public void error(Object message) {
        delegate.error(message);
    }

    public void fatal(Object message, Throwable t) {
        delegate.fatal(message, t);
    }

    public void fatal(Object message) {
        delegate.fatal(message);
    }

    public boolean getAdditivity() {
        return delegate.getAdditivity();
    }

    public Enumeration getAllAppenders() {
        return delegate.getAllAppenders();
    }

    public Appender getAppender(String name) {
        return delegate.getAppender(name);
    }

    public Priority getChainedPriority() {
        return delegate.getChainedPriority();
    }

    public Level getEffectiveLevel() {
        return delegate.getEffectiveLevel();
    }

    public LoggerRepository getHierarchy() {
        return delegate.getHierarchy();
    }

    public LoggerRepository getLoggerRepository() {
        return delegate.getLoggerRepository();
    }

    public ResourceBundle getResourceBundle() {
        return delegate.getResourceBundle();
    }

    public int hashCode() {
        return delegate.hashCode();
    }
    
    public void info(Object message, Throwable t) {
        delegate.info(concatTime(message), t);
    }

    public void info(Object message) {
        delegate.info(concatTime(message));
    }

    public boolean isAttached(Appender appender) {
        return delegate.isAttached(appender);
    }

    public boolean isDebugEnabled() {
        return delegate.isDebugEnabled();
    }

    public boolean isEnabledFor(Priority level) {
        return delegate.isEnabledFor(level);
    }

    public boolean isInfoEnabled() {
        return delegate.isInfoEnabled();
    }

    public boolean isTraceEnabled() {
        return delegate.isTraceEnabled();
    }

    public void l7dlog(Priority arg0, String arg1, Object[] arg2, Throwable arg3) {
        delegate.l7dlog(arg0, arg1, arg2, arg3);
    }

    public void l7dlog(Priority arg0, String arg1, Throwable arg2) {
        delegate.l7dlog(arg0, arg1, arg2);
    }

    public void log(Priority priority, Object message, Throwable t) {
        delegate.log(priority, message, t);
    }

    public void log(Priority priority, Object message) {
        delegate.log(priority, message);
    }

    public void log(String callerFQCN, Priority level, Object message,
            Throwable t) {
        delegate.log(callerFQCN, level, message, t);
    }

    public void removeAllAppenders() {
        delegate.removeAllAppenders();
    }

    public void removeAppender(Appender appender) {
        delegate.removeAppender(appender);
    }

    public void removeAppender(String name) {
        delegate.removeAppender(name);
    }

    public void setAdditivity(boolean additive) {
        delegate.setAdditivity(additive);
    }

    public void setLevel(Level level) {
        delegate.setLevel(level);
    }

    public void setPriority(Priority priority) {
        delegate.setPriority(priority);
    }

    public void setResourceBundle(ResourceBundle bundle) {
        delegate.setResourceBundle(bundle);
    }

    public String toString() {
        return delegate.toString();
    }

    public void trace(Object message, Throwable t) {
        delegate.trace(message, t);
    }

    public void trace(Object message) {
        delegate.trace(message);
    }

    public void warn(Object message, Throwable t) {
        delegate.warn(message, t);
    }

    public void warn(Object message) {
        delegate.warn(message);
    }
}