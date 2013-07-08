package com.dataiku.dctc.command.policy;

class QuashPolicy implements YellPolicy {
    public void yell(String what, String error, Throwable exception) {
        // Can't talk, have a cushion on the face.
    }
}
