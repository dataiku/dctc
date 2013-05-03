package com.dataiku.dip.utils;

public class Assertions {
    public static void ass(boolean ass, String msg) {
        if (!ass) {
            System.err.println("assert: !(" + Color.red(msg) + ")");
            assert false;
        }
    }
    public static void assertInstanceof(Object obj, Class<?> required) {
        if (!required.isInstance(obj)) {
            throw new AssertionError("Object is not of required type " + required + ": " + obj);
        }
    }
}
