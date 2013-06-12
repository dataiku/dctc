package com.dataiku.dctc.configuration;

public class Version {
    static public final String gitVersion = "XXX_GIT_VERSION_XXX";

    static public String pretty() {
        return gitVersion;
    }
}
