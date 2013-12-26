package com.dataiku.dip.utils;



/**
 * Ensures that the appender will
 * output at most N- lines.
 *
 * Makes sure to not use it for static loggers
 * as this "budget" is for the lifetime of the logger.
 */
public class LimitFilter implements DKULoggerFilter {

    public int nbLinesLogged = 0;
    public final int limit;

    private LimitFilter(int limit) {
        this.limit = limit;
    }

    public static LimitFilter withLimit(int limit) {
        return new LimitFilter(limit);
    }

    @Override
    public boolean accept(Object msg) {
        nbLinesLogged+=1;
        if (nbLinesLogged > limit) {
            return false;
        }
        else {
            return true;
        }
    }
}