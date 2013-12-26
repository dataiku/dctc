package com.dataiku.dip.utils;


import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

public class LimitFilter extends Filter {

    public int nbLinesLogged = 0;
    public final int limit;

    private LimitFilter(int limit) {
        this.limit = limit;
    }

    public static LimitFilter withLimit(int limit) {
        return new LimitFilter(limit);
    }

    @Override
    public int decide(LoggingEvent loggingEvent) {
        nbLinesLogged+=1;
        if (nbLinesLogged > limit) {
            return Filter.DENY;
        }
        else {
            return Filter.ACCEPT;
        }
    }

    /**
     * Returns a logger with a single console appender
     * with a limit filter. It ensures that the appender will
     * output at most N- lines.
     *
     * Makes sure to not use it for static loggers
     * as this "budget" is for the lifetime of the logger.
     *
     * @param name
     * @param limit Number of max lines outputted for the lifetime.
     * @return A limitted logger.
     */
    public static Logger getLimitedLogger(String name, int limit) {
        Logger logger = Logger.getLogger(name);
        logger.setAdditivity(false);
        logger.removeAllAppenders();
        Appender consoleAppender = new ConsoleAppender(new SimpleLayout(), ConsoleAppender.SYSTEM_OUT);
        consoleAppender.addFilter(new LimitFilter(limit));
        logger.addAppender(consoleAppender);
        return logger;
    }
}