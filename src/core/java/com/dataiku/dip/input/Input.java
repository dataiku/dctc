package com.dataiku.dip.input;

import java.util.Map;

import com.dataiku.dip.utils.WithParams;

public class Input extends WithParams {
    public Input(String type, Map<String, String> params) {
        super(params, type);
        this.type = type;
    }

    public Input withParam(String k, String v) {
        this.internalParams.add(k, v);
        return this;
    }

    protected String getDescForError() {
        return "input of type " + getType();
    }

    public String getType() {
        return type;
    }

    public Format getFormat() {
        return format;
    }
    public Input setFormat(Format format) {
        this.format = format;
        return this;
    }

    // Attributes
    private String type;
    private Format format;
}
