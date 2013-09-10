package com.dataiku.dip.utils;

import java.util.HashSet;
import java.util.Set;

/**
 * Transforms strings into whatever they would like to be.
 */
public class StringTransmogrifier {
    private Set<String> assigned = new HashSet<String>();
    private String delimiter = "_";
    
    public StringTransmogrifier() {
    }
    public StringTransmogrifier(String delimiter) {
        this.delimiter = delimiter;
    }
    
    public void addAlreadyTransmogrified(String in) {
        if (assigned.contains(in)) {
            throw new IllegalArgumentException("Input string " + in + " is already in the transmogrifier");
        }
        assigned.add(in);
    }
    
    public void addAlreadyTransmogrifiedAcceptDupes(String in) {
        assigned.add(in);
    }
    
    
    public String transmogrify(String input) {
        String cur = input;
        int i = 0;
        while (assigned.contains(cur)) {
            cur = input + delimiter + (++i);
        }
        assigned.add(cur);
        return cur;
    }
}