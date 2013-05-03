package com.dataiku.dctc.file;

import java.util.HashMap;
import java.util.Map;

public class Acl {
    // Setters
    public boolean asGroup(String group) {
        return group.equals("user");
    }
    public void setRead(String group, Boolean perm) {
        read.put(group, perm);
    }
    public void setWrite(String group, Boolean perm) {
        write.put(group, perm);
    }
    public void setExec(String group, Boolean perm) {
        exec.put(group, perm);
    }
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
    public Boolean getRead(String group) {
        return read.get(group);
    }
    public Boolean getWrite(String group) {
        return write.get(group);
    }
    public Boolean getExec(String group) {
        return exec.get(group);
    }
    public String getFileType() {
        return fileType;
    }

    public String getMode(String group) {
        String res = "";
        Boolean read = getRead(group);
        Boolean write = getWrite(group);
        Boolean exec = getExec(group);

        if (read == null) {
            res += "?";
        } else if (read) {
            res += "r";
        } else {
            res += "-";
        }

        if (write == null) {
            res += "?";
        } else if (write) {
            res += "w";
        } else {
            res += "-";
        }

        if (exec == null) {
            res += "?";
        } else if (exec) {
            res += "x";
        } else {
            res += "-";
        }

        return res;
    }
    public String getMode() {
        return getFileType()
            + getMode("user")
            + getMode("group")
            + getMode("world");
    }

    private Map<String, Boolean> read = new HashMap<String, Boolean>();
    private Map<String, Boolean> write = new HashMap<String, Boolean>();
    private Map<String, Boolean> exec = new HashMap<String, Boolean>();
    private String fileType;
}
