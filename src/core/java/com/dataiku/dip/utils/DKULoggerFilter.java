package com.dataiku.dip.utils;


public interface DKULoggerFilter {
    public boolean accept(Object msg);
}
