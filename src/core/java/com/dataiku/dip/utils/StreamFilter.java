package com.dataiku.dip.utils;

public interface StreamFilter {
    public String transform(String str, boolean availableBytes);
}
