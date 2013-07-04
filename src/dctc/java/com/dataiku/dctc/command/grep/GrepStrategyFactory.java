package com.dataiku.dctc.command.grep;

public class GrepStrategyFactory {
    public GrepPrinter buildPrinter(GrepHeaderPrinter header, GrepMatcher matcher) {
        return buildPrinter(getCount(), getListing(), getColor(), header, matcher);
    }
    public GrepPrinter buildPrinter(boolean count, boolean listing,
                                    boolean color, GrepHeaderPrinter header,
                                    GrepMatcher matcher) {
        if (count) {
            return new CountGrepPrinter(header);
        }
        else if (listing) {
            if (color) {
                return new ColorFileGrepPrinter();
            }
            else {
                return new FileGrepPrinter();
            }
        }
        else if (color) {
            return new ColorGrepPrinter(matcher);
        }
        else {
            return new SimpleGrepPrinter();
        }
    }
    public GrepMatcher buildMatcher(String[] pattern) {
        return buildMatcher(pattern, getRatexp(), getIgnoreCase(), getFullLine(), getInverse());
    }
    public GrepMatcher buildMatcher(String[] pattern, boolean ratexp,
                                    boolean ignoreCase, boolean fullLine,
                                    boolean inverse) {
        GrepMatcher matcher = null;
        if (ratexp) {
            matcher = new RatExpGrepMatcher(pattern);
        }
        else {
            matcher = new StringGrepMatcher(pattern);
        }

        if (ignoreCase) {
            matcher = new IgnoreCaseGrepMatcher(matcher);
        }
        if (fullLine) {
            matcher = new FullLineGrepMatcher(matcher);
        }
        if (inverse) {
            matcher = new InvGrepMatcher(matcher);
        }

        return matcher;
    }
    public GrepLinePrinter buildLinePrinter() {
        return buildLinePrinter(getCount(), getLinum(), getColor());
    }
    public GrepLinePrinter buildLinePrinter(boolean count, boolean linum, boolean color) {
        if (count || !linum) {
            return new OffGrepLinePrinter();
        }
        else if (linum) {
            if (color) {
                return new ColoredGrepLinePrinter();
            }
        }
        return new OnGrepLinePrinter();
    }
    public GrepHeaderPrinter buildHeaderPrinter() {
        return buildHeaderPrinter(getHeader(), getCount());
    }
    public GrepHeaderPrinter buildHeaderPrinter(boolean header, boolean count) {
        if(header) {
            GrepHeaderPrinter printer = new SimpleGrepHeaderPrinter();
            if (count) {
                return new QuietGrepHeaderPrinter(printer);
            }
            return printer;
        }
        else {
            return new QuietGrepHeaderPrinter(null);
        }
    }

    // Getters-Setters
    public boolean getCount() {
        return count;
    }
    public void setCount(boolean count) {
        this.count = count;
    }
    public GrepStrategyFactory withCount(boolean count) {
        setCount(count);
        return this;
    }
    public boolean getListing() {
        return listing;
    }
    public void setListing(boolean listing) {
        this.listing = listing;
    }
    public GrepStrategyFactory withListing(boolean listing) {
        setListing(listing);
        return this;
    }
    public boolean getColor() {
        return color;
    }
    public void setColor(boolean color) {
        this.color = color;
    }
    public GrepStrategyFactory withColor(boolean color) {
        setColor(color);
        return this;
    }
    public boolean getHeader() {
        return header;
    }
    public void setHeader(boolean header) {
        this.header = header;
    }
    public GrepStrategyFactory withHeader(boolean header) {
        setHeader(header);
        return this;
    }
    public boolean getRatexp() {
        return ratexp;
    }
    public void setRatexp(boolean ratexp) {
        this.ratexp = ratexp;
    }
    public GrepStrategyFactory withRatexp(boolean ratexp) {
        setRatexp(ratexp);
        return this;
    }
    public boolean getLinum() {
        return linum;
    }
    public void setLinum(boolean linum) {
        this.linum = linum;
    }
    public GrepStrategyFactory withLinum(boolean linum) {
        setLinum(linum);
        return this;
    }
    public boolean getIgnoreCase() {
        return ignoreCase;
    }
    public void setIgnoreCase(boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
    }
    public GrepStrategyFactory withIgnoreCase(boolean ignoreCase) {
        setIgnoreCase(ignoreCase);
        return this;
    }
    public boolean getFullLine() {
        return fullLine;
    }
    public void setFullLine(boolean fullLine) {
        this.fullLine = fullLine;
    }
    public GrepStrategyFactory withFullLine(boolean fullLine) {
        setFullLine(fullLine);
        return this;
    }
    public boolean getInverse() {
        return inverse;
    }
    public void setInverse(boolean inverse) {
        this.inverse = inverse;
    }
    public GrepStrategyFactory withInverse(boolean inverse) {
        setInverse(inverse);
        return this;
    }

    // Attributes
    private boolean inverse;
    private boolean fullLine;
    private boolean ignoreCase;
    private boolean linum;
    private boolean ratexp;
    private boolean header;
    private boolean color;
    private boolean listing;
    private boolean count;
}
