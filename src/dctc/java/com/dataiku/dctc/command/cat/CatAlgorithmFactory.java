package com.dataiku.dctc.command.cat;

import java.io.IOException;

import com.dataiku.dctc.file.GeneralizedFile;

public class CatAlgorithmFactory {
    // Building methods.
    public CatAlgorithm build(GeneralizedFile file) throws IOException {
        return build(file, getAlgo());
    }
    public CatAlgorithm build(GeneralizedFile file,
                              AlgorithmType algo) throws IOException {
        switch (algo) {
        case CAT:
            return buildCat(file);
        case HEAD:
            return buildHead(file);
        case TAIL:
            return buildTail(file);
        default:
            throw new Error("Never reached");
        }
    }
    private CatAlgorithm buildCat(GeneralizedFile file) {
        return buildCat(file, getLinum(), getDollar(),
                        getSqueezeMultipleEmpty());
    }
    private CatAlgorithm buildCat(GeneralizedFile file,
                                  boolean linum,
                                  boolean dollar,
                                  boolean squeezeMultipleEmpty) {
        if (linum || dollar || squeezeMultipleEmpty) {
            LinumCatAlgorithm cat = new LinumCatAlgorithm(file);
            { // Set the cat printer
                cat.setPrinter(new SimpleCatPrinter());
            }
            { // Set Header
                if (linum) {
                    cat.getPrinter().setHeader(new LinumCatHeader());
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
            // This implementation make a full dump of the file to the
            // standard output. It's an optimization for the standard
            // usage case.
            return new BytesCatAlgorithm(file);
        }
    }
    private CatAlgorithm buildHead(GeneralizedFile file) throws IOException {
        return buildHead(file, getSkipLast(), getIsLineAlgo());
    }
    private CatAlgorithm buildHead(GeneralizedFile file,
                                   long skipLast,
                                   boolean isLine) throws IOException {
        if (isLine) {
            LinumCatAlgorithm linum = new LinumCatAlgorithm(file)
                .withSelect(new FullCatLineSelector());

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
                long fileSize = file.getSize();
                return new BytesCatAlgorithm(file)
                    .withSkipLast(fileSize - skipLast);
            }
            else {
                return new BytesCatAlgorithm(file)
                    .withSkipLast(-skipLast);
            }
        }
    }
    private CatAlgorithm buildTail(GeneralizedFile file) throws IOException {
        return buildTail(file, skipFirst, getIsLineAlgo());
    }
    private CatAlgorithm buildTail(GeneralizedFile file,
                                   long skipFirst,
                                   boolean isLine) throws IOException {
        if (isLine) {
            if (skipFirst > 0) {
                if (file.canGetLastLines()) {
                    return new LatestLineCatAlgorithm(file)
                        .withNbLine(skipFirst);
                }
                else if (file.canGetPartialFile()) {
                    return new PartialFileTailAlgorithm(file)
                        .withNbLine(skipFirst);
                }
            }
            LinumCatAlgorithm linum = new LinumCatAlgorithm(file);

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
                return new BytesCatAlgorithm(file)
                    .withSkipFirst(fileSize - skipFirst);
            }
            else {
                return new BytesCatAlgorithm(file)
                    .withSkipFirst(-skipFirst);
            }
        }
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

    // Attributes
    private boolean squeezeMultipleEmpty; // Squeeze multiple empty line.
    private boolean dollar;
    private boolean linum;
    private AlgorithmType algo;
    private long skipLast;
    private long skipFirst;
    private boolean isLineAlgo;
}
