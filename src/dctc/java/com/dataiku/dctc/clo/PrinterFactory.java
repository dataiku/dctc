package com.dataiku.dctc.clo;

public class PrinterFactory {
    public enum Type {
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
    public Type getType() {
        return type;
    }
    public void setType(Type type) {
        this.type = type;
    }
    public PrinterFactory withType(Type type) {
        setType(type);
        return this;
    }

    // Attributes
    private Type type;
}

