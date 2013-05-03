package com.dataiku.dip.output;

import java.util.Map;

import com.dataiku.dip.utils.WithParams;

public class Output extends WithParams {
    public Output(String type, Map<String, String> p) {
        super(p, type);
        this.type = type;
    }

    public String type;

    public Output withParam(String k, String v) {
        this.internalParams.add(k, v);
        return this;
    }

    protected String getDescForError() {
        return "output of type " + type;
    }
}
