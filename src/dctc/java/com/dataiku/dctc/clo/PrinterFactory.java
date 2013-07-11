package com.dataiku.dctc.clo;

public class PrinterFactory {
    public enum PrinterType {
        COLORED
        , SIMPLE
    }
    public Printer build() {
        switch (getType()) {
        case COLORED:
            return new ColorPrinter();
        case SIMPLE:
            return new SimplePrinter();
        default:
            throw new Error("Never reached");
        }
    }
    // Getters - Setters
    public PrinterType getType() {
        return type;
    }
    public void setType(PrinterType type) {
        this.type = type;
    }
    public PrinterFactory withType(PrinterType type) {
        setType(type);
        return this;
    }

    // Attributes
    private PrinterType type;
}
