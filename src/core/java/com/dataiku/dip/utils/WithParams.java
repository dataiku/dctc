package com.dataiku.dip.utils;

import java.util.List;
import java.util.Map;

/**
 * Simple delegator for Params backend storage class
 */
public class WithParams {
	protected Params internalParams;

	public WithParams(Map<String, String> p, String id) {
	    internalParams = new Params(p);
	}

	public Params getParams() {
	    return internalParams;
	}

	public void addParam(String name, String value) { internalParams.add(name, value); }
	public void addParam(String name, int value) { internalParams.add(name, value); }

    /* Get as string */

    public String getParam(String name) { return internalParams.getParam(name); }
    public String getParam(String name, String defaultValue) { return internalParams.getParam(name, defaultValue); }
    public String getMandParam(String name) { return internalParams.getMandParam(name); }
    public String getNonEmptyMandParam(String name) { return internalParams.getNonEmptyMandParam(name); }

    /* Get as bool */

    public boolean getBoolParam(String name, boolean defaultValue) { return internalParams.getBoolParam(name, defaultValue); }

    /* Get as numerical */

    public int getIntParam(String name, Integer defaultValue) { return internalParams.getIntParam(name, defaultValue); }
    public int getIntParam(String name) { return internalParams.getIntParam(name); }

    public long getLongParam(String name, long defaultValue) { return internalParams.getLongParam(name, defaultValue); }
    public long getLongParam(String name) { return internalParams.getLongParam(name); }

    public double getDoubleParam(String name, long defaultValue) { return internalParams.getDoubleParam(name, defaultValue); }
    public double getDoubleParam(String name) { return internalParams.getDoubleParam(name); }

    /* Get as char */
    public char getCharParam(String name) { return internalParams.getCharParam(name); }

    /* Get as CSV */
    public List<String> getCSVParamAsList(String name, String defaultValue) { return internalParams.getCSVParamAsList(name, defaultValue); }
    public String[] getCSVParamAsArray(String name, String defaultValue) { return internalParams.getCSVParam(name, defaultValue); }
}
