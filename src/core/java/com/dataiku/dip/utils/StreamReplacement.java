package com.dataiku.dip.utils;

import java.util.ArrayList;
import java.util.List;

public class StreamReplacement implements StreamFilter {
    StreamReplacement(List<StringPair> replacementString) {
        this.replacementString = replacementString;
        minCache = 1;
        for (StringPair pair: replacementString) {
            minCache = Math.max(pair.getSource().length(), minCache);
        }
        --minCache;
    }
    public StreamReplacement(String source, String destination) {
        replacementString = new ArrayList<StringPair>();
        replacementString.add(new StringPair(source, destination));
        minCache = source.length() - 1;
    }

    @Override
    public String transform(String str, boolean availableBytes) {
        return transform(str, availableBytes, -1);
    }
    private String transform(String str, boolean availableBytes, int start_idx) {

        // Restore the string as if we didn't have any buffer.
        str = buffer + str;
        buffer = "";
        // If we didn't enough string in str.
        if (str.length() < minCache) {
            if (!availableBytes) {
                // End of the stream, squash the buffer.
                return str;
            }
            else {
                // Keep the entire string as buffer.
                buffer = str;
                return "";
            }
        }

        // Find the best replacement.
        int best_idx = Integer.MAX_VALUE;
        StringPair best_pair = null;

        for (StringPair pair: replacementString) {
            int index = str.indexOf(pair.getSource(), start_idx);
            if (index != -1 && index < best_idx) {
                best_idx = index;
                best_pair = pair;
            }
        }
        if (best_idx != Integer.MAX_VALUE) {
            // Find one replacement, apply it.
            str = str.substring(0, best_idx)
                + best_pair.getDestination()
                + str.substring(best_idx + best_pair.getSource().length());
            // Make the recursion. Skip the modified characters.
            return transform(str, availableBytes, start_idx + best_pair.getDestination().length());
        }
        else {
            // No available replacement, compute the buffer, and
            // leave.
            int outputSize = availableBytes ? str.length() - minCache: str.length();
            assert outputSize >= 0;
            buffer = str.substring(outputSize);
            return str.substring(0, outputSize);
        }
    }

    // Getters/Setters
    public List<StringPair> getReplacementString() {
        return replacementString;
    }
    public void setReplacementString(List<StringPair> replacementString) {
        this.replacementString = replacementString;
    }
    public StreamReplacement withReplacementString(List<StringPair> replacementString) {
        this.replacementString = replacementString;
        return this;
    }
    public int getCacheSize() {
        return buffer.length();
    }

    public static class StringPair {
        public StringPair(String source, String destination) {
            this.source = source;
            this.destination = destination;
        }
        public String getSource() {
            return source;
        }
        public void setSource(String source) {
            this.source = source;
        }
        public StringPair withSource(String source) {
            this.source = source;
            return this;
        }
        public String getDestination() {
            return destination;
        }
        public void setDestination(String destination) {
            this.destination = destination;
        }
        public StringPair withDestination(String destination) {
            this.destination = destination;
            return this;
        }

        // Attributes
        private String source;
        private String destination;

    }

    // Attributes
    private List<StringPair> replacementString;
    private int minCache;
    private String buffer = "";
}
