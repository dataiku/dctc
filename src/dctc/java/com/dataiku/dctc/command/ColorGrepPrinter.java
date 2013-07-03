package com.dataiku.dctc.command;

import com.dataiku.dctc.file.GeneralizedFile;

class ColorGrepPrinter implements GrepPrinter {
    ColorGrepPrinter(GrepMatcher matcher) {
        this.matcher = matcher;
    }
    public void print(String line) {
        while(!line.isEmpty()) {
            if (matcher.match(line)) {
                int begin = matcher.begin(line);
                int end = matcher.end(begin, line);
                System.out.print(line.substring(0, begin));
                System.out.print("\u001B[1;31m" + line.substring(begin, end) + "\u001B[0m");
                line = line.substring(end);
            }
            else {
                break;
            }
        }
        System.out.println(line);
    }
    public void end(GeneralizedFile file) {
    }

    public GrepMatcher getMatcher() {
        return matcher;
    }
    public void setMatcher(GrepMatcher matcher) {
        this.matcher = matcher;
    }
    public ColorGrepPrinter withMatcher(GrepMatcher matcher) {
        this.matcher = matcher;
        return this;
    }

    // Attributes
    private GrepMatcher matcher;
}
