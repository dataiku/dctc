package com.dataiku.dctc.configuration;

public class Version {
    static public final String gitVersion = "XXX_GIT_HASH_XXX";
    static public final String version = "0.2.0";

    static public String pretty() {
        return "v" + version + " (" + gitVersion + ")";
    }
}
