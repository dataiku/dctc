package com.dataiku.dctc.command.cat;

import com.dataiku.dctc.file.GeneralizedFile;

public class CatAlgorithmFactory {
    // The algorithms types
    public enum Algorithm {
        CAT
        , HEAD
        , TAIL
        ;
    }

    // Building methods.
    public CatAlgorithm build(GeneralizedFile file) {
        return build(file, getAlgo());
    }
    public CatAlgorithm build(GeneralizedFile file, Algorithm algo) {
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
        return buildCat(file, getLinum(), getDollar(), getSqueezeMultipleEmpty());
    }
    private CatAlgorithm buildCat(GeneralizedFile file, boolean linum,
                                  boolean dollar, boolean squeezeMultipleEmpty) {
        if (linum || dollar || squeezeMultipleEmpty) {
            LinumCatAlgorithm cat = new LinumCatAlgorithm(file);

            { // Set Header
                if (linum) {
                    cat.setHeader(new LinumCatHeader());
                }
                else {
                    cat.setHeader(new EmptyCatHeader());
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
            { // Set the cat printer
                cat.setPrinter(new SimpleCatPrinter());
            }
            { // Set the end of line printer
                if (dollar) {
                    cat.setEol(new DollarEOLCatPrinter());
                }
                else {
                    cat.setEol(new NewLineEOLCatPrinter());
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
            return new SimpleCatAlgorithm(file);
        }
    }
    private CatAlgorithm buildHead(GeneralizedFile file) {
        return buildHead(file, getHead());
    }
    private CatAlgorithm buildHead(GeneralizedFile file, int head) {
        return new LinumCatAlgorithm(file)
            .withSelect(new FullCatLineSelector())
            .withHeader(new EmptyCatHeader())
            .withPrinter(new SimpleCatPrinter())
            .withEol(new NewLineEOLCatPrinter())
            .withStop(new HeadCatStop().withHead(head));
    }
    private CatAlgorithm buildTail(GeneralizedFile file) {
        return null;
    }

    // Getters-Setters
    public Algorithm getAlgo() {
        return algo;
    }
    public void setAlgo(Algorithm algo) {
        this.algo = algo;
    }
    public CatAlgorithmFactory withAlgo(Algorithm algo) {
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
    public int getHead() {
        return head;
    }
    public void setHead(int head) {
        this.head = head;
    }
    public CatAlgorithmFactory withHead(int head) {
        setHead(head);
        return this;
    }

    // Attributes
    private boolean squeezeMultipleEmpty;
    private boolean dollar;
    private boolean linum;
    private Algorithm algo;
    private int head;
}
