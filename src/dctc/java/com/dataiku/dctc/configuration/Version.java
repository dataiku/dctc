package com.dataiku.dctc.configuration;

public class Version {
    static public final String gitVersion = "XXX_GIT_HASH_XXX";
    static public final String version = "0.2";
    static public final boolean isStable = false;

    static public String pretty() {
        StringBuilder b = new StringBuilder();

        b.append("v" + version + "(" + gitVersion + ")");
        if (isStable) {
            b.append(" not stable");
        }
        return b.toString();
    }
}
