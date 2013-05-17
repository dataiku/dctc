package com.dataiku.dip.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

/**
 * Simple registry system for hierarchical params
 *
 * TODO: Cleanup the get / getMand / getParam which is inconsistent
 * TODO: Shouldn't KeyValue have some functions of WithParams itself for lookups in a subkey?
 * TODO: Shouldn't KeyValue have an optional anchor to know where it is?
 */
public class Params {
    public static class KeyValue {
        public KeyValue() {
        }
        public KeyValue(String key) {
            this.key = key;
        }
        public KeyValue(String key, String value) {
            this.key = key;
            this.value = value;
        }
        public void setValue(String value) {
            this.value = value;
        }

        public String key() {
            return key;
        }
        public String value() {
            return value;
        }

        public String toString() {
            return "[KV k="+ key + " v=" + value + " children=" + getChildren().size() + "]";
        }

        public KeyValue getChild(String childKey) {
            for (KeyValue kv : getChildren()) {
                if (kv.key().equals(childKey)) return kv;
            }
            return null;
        }

        public List<KeyValue> getChildren() {
            if (children == null) children = new ArrayList<Params.KeyValue>();
            return children;
        }

        private String key;
        private String value;
        private List<KeyValue> children;

        public List<String> childrenKeys(String prefix, boolean includeSelfKey) {
            List<String> o = new ArrayList<String>();
            for (KeyValue child: getChildren()) {
                String name = child.key;
                if (includeSelfKey) name = key + "." + name;
                if (prefix != null) name = prefix + "." + name;
                o.add(name);
            }
            return o;
        }
    }

    protected String getDescForError() { return ""; }

    public Params() {
    }

    public Params(Map<String, String> input) {
        for (Map.Entry<String, String> ie : input.entrySet()) {
            add(ie.getKey(), ie.getValue());
        }
    }

    public Map<String, String> getAll() {
        return ImmutableMap.copyOf(params);
    }

    // Fully hierarchical view
    KeyValue rootKV = new KeyValue();
    // Fully flat view
    protected Map<String, String> params = new HashMap<String, String>();
    // Access to intermediate levels
    private Map<String, KeyValue> intermediate = new HashMap<String, Params.KeyValue>();

    private void addRec(KeyValue prevKV, String[]chunks, int curIdx, String value, String curPrefix) {
        String curChunk = chunks[curIdx];

        if (!curPrefix.isEmpty()) curPrefix += '.';
        curPrefix += curChunk;

        KeyValue kv = prevKV.getChild(curChunk);
        if (kv == null) {
            kv = new KeyValue(curChunk);
            intermediate.put(curPrefix, kv);
            prevKV.children.add(kv);
        }

        if (curIdx + 1 == chunks.length) {
            kv.setValue(value);
            params.put(curPrefix, value);
        } else {
            addRec(kv, chunks, curIdx + 1, value, curPrefix);
        }
    }

    public void add(String key, String value) {
        String[] chunks = key.split("\\.");
        addRec(rootKV, chunks, 0, value, "");
    }

    public void add(String key, long value) {
        add(key, Long.toString(value));
    }
    public void add(String key, double value) {
        add(key, Double.toString(value));
    }

    public boolean hasParam(String name) {
        return params.containsKey(name);
    }

    /* Get as string */

    public String getParam(String name) {
        return params.get(name);
    }

    public String getParam(String name, String defaultValue) {
        String s = params.get(name);
        if (s == null) return defaultValue;
        return s;
    }
    public String getParamOrEmpty(String name) {
        return getParam(name, "");
    }
    // Get a mandatory parameter. Throw an illegal argument exception
    // if the paramater is non define.
    public String getMandParam(String name) {
        String s = params.get(name);
        if (s == null) throw new IllegalArgumentException("Missing param '" + name + " " + getDescForError());
        return s;
    }
    public String getNonEmptyMandParam(String name) {
        String s = getMandParam(name);
        if (s.isEmpty()) throw new IllegalArgumentException("Empty param '" + name + " " + getDescForError());
        return s;
    }

    /* Get as bool */

    public boolean getBoolParam(String name, boolean defaultValue) {
        String s = params.get(name);
        if (s == null) return defaultValue;
        if (s.equalsIgnoreCase("true") || s.startsWith("y") || s.startsWith("Y")) return true;
        else return false;
    }

    /* Get as numerical */

    public int getIntParam(String name, Integer defaultValue) {
        String s = params.get(name);
        if (s == null) return defaultValue;
        return Integer.parseInt(s);
    }
    public int getIntParam(String name) {
        String s = getMandParam(name);
        return Integer.parseInt(s);
    }

    public short getShortParam(String name, Short defaultValue) {
        String s = params.get(name);
        if (s == null) return defaultValue;
        return Short.parseShort(s);
    }
    public short getShortParam(String name) {
        String s = getMandParam(name);
        return Short.parseShort(s);
    }

    public int getUShortParam(String name, Integer defaultValue) {
        String s = params.get(name);
        if (s == null) return defaultValue;
        int res = Integer.parseInt(s);
        if (!ushortBoundsCheck(res)) {
            throw new NumberFormatException(s + " is not an unsigned short.");
        }
        return res;
    }
    public int getUShortParam(String name) {
        return getUShortParam(name, null);
    }
    private boolean ushortBoundsCheck(int i) {
        return i >= 0 && i <= 65535;
    }

    public long getLongParam(String name, long defaultValue) {
        String s = params.get(name);
        if (s == null) return defaultValue;
        return Long.parseLong(s);
    }
    public long getLongParam(String name) {
        String s = getMandParam(name);
        return Long.parseLong(s);
    }

    public double getDoubleParam(String name, long defaultValue) {
        String s = params.get(name);
        if (s == null) return defaultValue;
        return Double.parseDouble(s);
    }
    public double getDoubleParam(String name) {
        String s = getMandParam(name);
        return Double.parseDouble(s);
    }

    /* Get as char */
 
    public char getCharParam(String name) {
        String s = getMandParam(name);
        if (s.length() != 1) {
            if (s.startsWith("\\u")) {
                try {
                    int code = Integer.parseInt(s.substring(2));
                    return (char)code;
                } catch (Exception e) {
                    throw exceptSingleChar(name);
                }
            } else if (s.startsWith("u") && s.length() == 5) {
                try {
                    int code = Integer.parseInt(s.substring(1));
                    return (char)code;
                } catch (Exception e) {
                    throw exceptSingleChar(name);
                }
            }
            throw exceptSingleChar(name);
        }
        return s.charAt(0);
    }
    private IllegalArgumentException exceptSingleChar(String param) {
        return new IllegalArgumentException("Expected single char in param '" + param + "' for " + getDescForError());
    }

    /* Get as CSV */

    public List<String> getCSVParamAsList(String name, String defaultValue) {
        String v = getParam(name, defaultValue);
        String[] chunks = v.split(",");
        List<String> out = new ArrayList<String>();
        for (String s : chunks) out.add(s);
        return out;
    }
    public String[] getCSVParamAsArray(String name, String defaultValue) {
        String v = getParam(name, defaultValue);
        return v.split(",");
    }

    public KeyValue getAsKV(String name) {
        return intermediate.get(name);
    }

    public String get(String prefix, String suffix) {
        return getParam(prefix + "." + suffix);
    }

    public List<String> getChildrenAsIntList(String prefix) {
        KeyValue kv = intermediate.get(prefix);
        if (kv == null) return null;
        List<String> validList = new ArrayList<String>();

        int i = 0;
        while (true) {
            KeyValue child = kv.getChild("" + i);
            if (child == null) break;
            else {
                validList.add("" + i);
                i++;
            }
        }

        /* Now, check that there are no invalid values */
        for (KeyValue child : kv.getChildren()) {
            String childKey = child.key;
            if (!validList.contains(childKey)) {
                throw new IllegalArgumentException("In prefix " + prefix + ", key " + childKey + " does not follow a valid list");
            }
        }

        Collections.sort(validList, new Comparator<String>() {
                @Override
                    public int compare(String o1, String o2) {
                    int i1 = Integer.parseInt(o1);
                    int i2 = Integer.parseInt(o2);
                    return i1 > i2 ? 1 : (i1 < i2 ? - 1 : 0);
                }
            });
        return validList;
    }

    public List<String> childrenFullNames(String prefix) {
        KeyValue kv = intermediate.get(prefix);
        if (kv == null) return null;
        List<String> o = new ArrayList<String>();
        for (KeyValue child : kv.getChildren()) {
            o.add(prefix + "." + child.key());
        }
        return o;
    }
}
