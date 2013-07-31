package com.dataiku.dctc.command.cat;

class NotCatLineSelector implements CatLineSelector {
    public boolean needPrint(String line) {
        return !selector.needPrint(line);
    }

    // Getters - Setters
    public CatLineSelector getSelector() {
        return selector;
    }
    public void setSelector(CatLineSelector selector) {
        this.selector = selector;
    }
    public NotCatLineSelector withSelector(CatLineSelector selector) {
        setSelector(selector);
        return this;
    }

    // Attributes
    private CatLineSelector selector;
}
