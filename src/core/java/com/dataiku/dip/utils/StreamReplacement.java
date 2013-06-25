package com.dataiku.dip.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

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
        str = buffer + str;
        buffer = "";
        if (str.length() < minCache) {
            buffer = str;
            if (!availableBytes) {
                return buffer;
            }
            else {
                return "";
            }
        }

        allowedReplacement.clear();

        for (StringPair pair: replacementString) {
            int index = str.indexOf(pair.getSource());
            if (index != -1) {
                addIfNeeded(index, pair, allowedReplacement);
            }
        }

        if (!allowedReplacement.isEmpty()) {
            Entry<Integer, StringPair> replacement = allowedReplacement.entrySet().iterator().next();
            assert str.indexOf(replacement.getValue().getSource()) == replacement.getKey()
                : "str.indexOf(replacement.getValue().getSource()) == replacement.getKey()";
            str = buffer + str;
            str = str.substring(0, replacement.getKey())
                + replacement.getValue().getDestination()
                + str.substring(replacement.getKey() + replacement.getValue().getSource().length());
            return transform(str, availableBytes);
        }
        else {
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

    public class StringPair {
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

    // Privates
    private void addIfNeeded(Integer index, StringPair pair, Map<Integer, StringPair> map) {
        if (!map.containsKey(index)) {
            map.put(index, pair);
        }
    }
    // Attributes
    private List<StringPair> replacementString;
    private int minCache;
    private String buffer = new String();
    private Map<Integer, StringPair> allowedReplacement = new TreeMap<Integer, StringPair>();
}
