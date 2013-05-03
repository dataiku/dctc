package com.dataiku.dip.input;

import java.util.HashMap;
import java.util.Map;

import com.dataiku.dip.utils.WithParams;

public class Format extends WithParams {
    public Format(String type, Map<String, String> params) {
        super(params, type);
        this.type = type;
    }
    
    public Format(String type) {
        super(new HashMap<String, String>(), type);
        this.type = type;
    }

    private String type;

    public Format withParam(String k, String v) {
        this.internalParams.add(k, v);
        return this;
    }

    protected String getDescForError() {
        return "format of type " + type;
    }

    public String getType() {
        return type;
    }
}
