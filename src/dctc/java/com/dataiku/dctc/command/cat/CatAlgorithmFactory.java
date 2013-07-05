package com.dataiku.dctc.command.cat;

import com.dataiku.dctc.file.GeneralizedFile;

public class CatAlgorithmFactory {
    // Building methods.
    public CatAlgorithm build(GeneralizedFile file) {
        return build(file, getAlgo());
    }
    public CatAlgorithm build(GeneralizedFile file, AlgorithmType algo) {
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
            return new BytesCatAlgorithm(file)
                .withSkip(new CatByteSkip()); // Skip 0 bytes.
        }
    }
    private CatAlgorithm buildHead(GeneralizedFile file) {
        return buildHead(file, getNbLine());
    }
    private CatAlgorithm buildHead(GeneralizedFile file, int head) {
        if (head > 0) {
            return new LinumCatAlgorithm(file)
                .withSelect(new FullCatLineSelector())
                .withPrinter(new SimpleCatPrinter()
                             .withHeader(new EmptyCatHeader())
                             .withEol(new NewLineEOLCatPrinter()))
                .withStop(new HeadCatStop().withHead(head));
        }
        else {
            return new LinumCatAlgorithm(file)
                .withSelect(new FullCatLineSelector())
                .withPrinter(new HeadCatPrinter()
                             .withHead(-head)
                             .withHeader(new EmptyCatHeader())
                             .withEol(new NewLineEOLCatPrinter()))
                .withStop(new ContinueCatStop());

        }
    }
    private CatAlgorithm buildTail(GeneralizedFile file) {
        return buildTail(file, getNbLine());
    }
    private CatAlgorithm buildTail(GeneralizedFile file, int nbLine) {
        if (file.canGetLastLines()) {
            return new LastestLineCatAlgorithm(file)
                .withNbLine(nbLine);
        } else if (file.canGetPartialFile()) {
            return new PartialFileTailAlgorithm(file)
                .withNbLine(nbLine);
        }
        else {

            // Should be called only for full read text.
            return new LinumCatAlgorithm(file)
                .withSelect(new FullCatLineSelector())
                .withPrinter(new TailCatPrinter()
                             .withTail(nbLine)
                             .withHeader(new EmptyCatHeader())
                             .withEol(new NewLineEOLCatPrinter()))
                .withStop(new ContinueCatStop());
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
    public int getNbLine() {
        return nbLine;
    }
    public void setNbLine(int nbLine) {
        this.nbLine = nbLine;
    }
    public CatAlgorithmFactory withNbLine(int nbLine) {
        setNbLine(nbLine);
        return this;
    }

    // Attributes
    private boolean squeezeMultipleEmpty;
    private boolean dollar;
    private boolean linum;
    private AlgorithmType algo;
    private int nbLine;
}
