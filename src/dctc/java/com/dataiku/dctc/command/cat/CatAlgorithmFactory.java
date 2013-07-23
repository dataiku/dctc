package com.dataiku.dctc.command.cat;

import java.io.IOException;

import com.dataiku.dctc.command.policy.YellPolicy;
import com.dataiku.dctc.file.GFile;

public class CatAlgorithmFactory {
    // Building methods.
    public CatAlgorithm build(GFile file) throws IOException {
        return build(file, getAlgo());
    }
    public CatAlgorithm build(GFile file,
                              AlgorithmType algo) throws IOException {
        switch (algo) {
        case CAT:
            return buildCat(file);
        case NL:
            return buildNl(file);
        case HEAD:
            return buildHead(file);
        case TAIL:
            return buildTail(file);
        default:
            throw new Error("Never reached");
        }
    }
    private CatAlgorithm buildCat(GFile file) {
        return buildCat(file
                        , getLinum()
                        , getDollar()
                        , getSqueezeMultipleEmpty()
                        , getStartingLine()
                        , getShowTabulation()
                        , getPrettyChar());
    }
    private CatAlgorithm buildCat(GFile file
                                  , boolean linum
                                  , boolean dollar
                                  , boolean squeezeMultipleEmpty
                                  , long lineNumber
                                  , boolean showTabulation
                                  , boolean prettyChar) {
        if (linum || dollar || squeezeMultipleEmpty) {
            LinumCatAlgorithm cat = new LinumCatAlgorithm(file, "cat");
            cat.setYell(getYell());
            { // Set the cat printer
                cat.setPrinter(new SimpleCatPrinter());
            }
            { // Set Header
                if (linum) {
                    cat.getPrinter().setHeader(new LeftLinumCatHeader()
                                               .withIndentSeparator(" ")
                                               .withLineNumber(lineNumber)
                                               .withNumberIncrement(1)
                                               .withNumberOfCol(6));
                }
                else {
                    cat.getPrinter().setHeader(new EmptyCatHeader());
                }
            }
            { // Set the line selector
                if (!squeezeMultipleEmpty) {
                    cat.setSelect(new SqueezeEmptyCatLineSelector());
                }
                else {
                    cat.setSelect(new FullCatLineSelector());
                }
            }

            { // Set the end of line printer
                if (dollar) {
                    cat.getPrinter().setEol(new DollarEOLCatPrinter());
                }
                else {
                    cat.getPrinter().setEol(new NewLineEOLCatPrinter());
                }
            }
            { // Don't stop the cat
                cat.setStop(new ContinueCatStop());
            }

            return cat;
        }
        else {
            if (prettyChar) {
                return new PrettyBytesCatAlgorithm(file, "cat")
                    .withShowTabulation(showTabulation)
                    .withYell(getYell());
            }
            else {
                // This implementation make a full dump of the file to the
                // standard output. It's an optimization for the standard
                // usage case.
                return new CopyBytesCatAlgorithm(file, "cat")
                    .withYell(getYell());
            }
        }
    }
    private CatAlgorithm buildHead(GFile file) throws IOException {
        return buildHead(file, getSkipLast(), getIsLineAlgo());
    }
    private CatAlgorithm buildHead(GFile file
                                   , long skipLast
                                   , boolean isLine) throws IOException {
        if (isLine) {
            LinumCatAlgorithm linum = new LinumCatAlgorithm(file, "head")
                .withSelect(new FullCatLineSelector());
            linum.setYell(getYell());
            if (skipLast > 0) {
                linum.withPrinter(new SimpleCatPrinter()
                                  .withHeader(new EmptyCatHeader())
                                  .withEol(new NewLineEOLCatPrinter()))
                    .withStop(new HeadCatStop()
                              .withHead(skipLast));

            }
            else {
                linum.withPrinter(new HeadCatPrinter()
                                  .withHead(-skipLast)
                                  .withHeader(new EmptyCatHeader())
                                  .withEol(new NewLineEOLCatPrinter()))
                    .withStop(new ContinueCatStop());
            }
            return linum;
        }
        else {
            if (skipLast > 0) {
                return new CopyBytesCatAlgorithm(file, "head")
                    .withSkipLast(skipLast);
            }
            else {
                long fileSize = file.getSize();
                return new CopyBytesCatAlgorithm(file, "head")
                    .withSkipLast(fileSize + skipLast);
            }
        }
    }
    private CatAlgorithm buildTail(GFile file) throws IOException {
        return buildTail(file, skipFirst, getIsLineAlgo());
    }
    private CatAlgorithm buildTail(GFile file
                                   , long skipFirst
                                   , boolean isLine) throws IOException {
        if (isLine) {
            if (skipFirst > 0) {
                if (file.canGetLastLines()) {
                    return new LatestLineCatAlgorithm(file, "tail")
                        .withNbLine(skipFirst);
                }
                else if (file.canGetPartialFile()) {
                    return new PartialFileTailAlgorithm(file, "tail")
                        .withNbLine(skipFirst);
                }
            }
            LinumCatAlgorithm linum = new LinumCatAlgorithm(file, "tail");
            linum.setYell(getYell());

            if (skipFirst > 0) {
                linum.withSelect(new FullCatLineSelector())
                    .withPrinter(new TailCatPrinter()
                                 .withTail(skipFirst)
                                 .withHeader(new EmptyCatHeader())
                                 .withEol(new NewLineEOLCatPrinter()))
                    .withStop(new ContinueCatStop());
            }
            else {
                linum.withSelect(new FullCatLineSelector())
                    .withPrinter(new SkipFirstLine()
                                 .withSkipNbLine(-skipFirst)
                                 .withPrinter(new SimpleCatPrinter()
                                              .withHeader(new EmptyCatHeader())
                                              .withEol(new NewLineEOLCatPrinter())))
                    .withStop(new ContinueCatStop());
            }

            return linum;
        }
        else {
            // bytes
            if (skipFirst > 0) {
                long fileSize = file.getSize();
                return new CopyBytesCatAlgorithm(file, "tail")
                    .withSkipFirst(fileSize - skipFirst);
            }
            else {
                return new CopyBytesCatAlgorithm(file, "tail")
                    .withSkipFirst(-skipFirst);
            }
        }
    }
    private CatAlgorithm buildNl(GFile file) {
        return buildNl(file
                       , getLineIncrement()
                       , getIndentSeparator()
                       , getIndentSize()
                       , getStartingLine());
    }
    private CatAlgorithm buildNl(GFile file
                                 , int lineIncrement
                                 , String indentSeparator
                                 , int minIndentSize
                                 , long startingLine) {
        return new LinumCatAlgorithm(file, "nl")
            .withSelect(new NlCatLineSelector())
            .withPrinter(new SimpleCatPrinter()
                         .withHeader(new LeftLinumCatHeader()
                                     .withNumberIncrement(lineIncrement)
                                     .withIndentSeparator(indentSeparator)
                                     .withNumberOfCol(minIndentSize)
                                     .withLineNumber(startingLine))
                         .withEol(new NewLineEOLCatPrinter()))
            .withStop(new ContinueCatStop())
            .withYell(getYell());
    }
    // Getters-Setters
    public AlgorithmType getAlgo() {
        return algo;
    }
    public void setAlgo(AlgorithmType algo) {
        this.algo = algo;
    }
    public CatAlgorithmFactory withAlgo(AlgorithmType algo) {
        setAlgo(algo);
        return this;
    }

    public boolean getLinum() {
        return linum;
    }
    public void setLinum(boolean linum) {
        this.linum = linum;
    }
    public CatAlgorithmFactory withLinum(boolean linum) {
        setLinum(linum);
        return this;
    }
    public boolean getDollar() {
        return dollar;
    }
    public void setDollar(boolean dollar) {
        this.dollar = dollar;
    }
    public CatAlgorithmFactory withDollar(boolean dollar) {
        setDollar(dollar);
        return this;
    }
    public boolean getSqueezeMultipleEmpty() {
        return squeezeMultipleEmpty;
    }
    public void setSqueezeMultipleEmpty(boolean squeezeMultipleEmpty) {
        this.squeezeMultipleEmpty = squeezeMultipleEmpty;
    }
    public CatAlgorithmFactory withSqueezeMultipleEmpty(boolean squeezeMultipleEmpty) {
        setSqueezeMultipleEmpty(squeezeMultipleEmpty);
        return this;
    }
    public boolean getIsLineAlgo() {
        return isLineAlgo;
    }
    public void setIsLineAlgo(boolean isLineAlgo) {
        this.isLineAlgo = isLineAlgo;
    }
    public CatAlgorithmFactory withIsLineAlgo(boolean isLineAlgo) {
        setIsLineAlgo(isLineAlgo);
        return this;
    }
    public long getSkipFirst() {
        return skipFirst;
    }
    public void setSkipFirst(long skipFirst) {
        this.skipFirst = skipFirst;
    }
    public CatAlgorithmFactory withSkipFirst(long skipFirst) {
        setSkipFirst(skipFirst);
        return this;
    }

    public long getSkipLast() {
        return skipLast;
    }
    public void setSkipLast(long skipLast) {
        this.skipLast = skipLast;
    }
    public CatAlgorithmFactory withSkipLast(long skipLast) {
        setSkipLast(skipLast);
        return this;
    }
    public int getLineIncrement() {
        return lineIncrement;
    }
    public void setLineIncrement(int lineIncrement) {
        this.lineIncrement = lineIncrement;
    }
    public CatAlgorithmFactory withLineIncrement(int lineIncrement) {
        setLineIncrement(lineIncrement);
        return this;
    }
    public String getIndentSeparator() {
        return indentSeparator;
    }
    public void setIndentSeparator(String indentSeparator) {
        this.indentSeparator = indentSeparator;
    }
    public CatAlgorithmFactory withIndentSeparator(String indentSeparator) {
        setIndentSeparator(indentSeparator);
        return this;
    }
    public int getIndentSize() {
        return indentSize;
    }
    public void setIndentSize(int indentSize) {
        this.indentSize = indentSize;
    }
    public CatAlgorithmFactory withIndentSize(int indentSize) {
        setIndentSize(indentSize);
        return this;
    }
    public long getStartingLine() {
        return startingLine;
    }
    public void setStartingLine(long startingLine) {
        this.startingLine = startingLine;
    }
    public CatAlgorithmFactory withStartingLine(long startingLine) {
        setStartingLine(startingLine);
        return this;
    }
    public YellPolicy getYell() {
        return yell;
    }
    public void setYell(YellPolicy yell) {
        this.yell = yell;
    }
    public CatAlgorithmFactory withYell(YellPolicy yell) {
        setYell(yell);
        return this;
    }
    public boolean getPrettyChar() {
        return prettyChar;
    }
    public void setPrettyChar(boolean prettyChar) {
        this.prettyChar = prettyChar;
    }
    public CatAlgorithmFactory withPrettyChar(boolean prettyChar) {
        setPrettyChar(prettyChar);
        return this;
    }
    public boolean getShowTabulation() {
        return showTabulation;
    }
    public void setShowTabulation(boolean showTabulation) {
        this.showTabulation = showTabulation;
    }
    public CatAlgorithmFactory withShowTabulation(boolean showTabulation) {
        setShowTabulation(showTabulation);
        return this;
    }

    // Attributes
    private boolean showTabulation;
    private boolean prettyChar;
    private YellPolicy yell;
    private long startingLine;
    private int indentSize;
    private String indentSeparator;
    private int lineIncrement;
    private boolean squeezeMultipleEmpty; // Squeeze multiple empty line.
    private boolean dollar;
    private boolean linum;
    private AlgorithmType algo;
    private long skipLast;
    private long skipFirst;
    private boolean isLineAlgo;
}
