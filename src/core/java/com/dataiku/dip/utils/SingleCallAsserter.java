package com.dataiku.dip.utils;

public class SingleCallAsserter {
    public void call(String message) {
        if (called) {
            throw new AssertionError("Method has already been called: " + (message == null ? "" : message));
        }
        called = true;
    }
    private boolean called;
}
