package com.dataiku.dip.utils;

public class Color {
    public final static String blue = "\u001B[01;34m";
    public final static String purple = "\u001B[01;35m";
    public final static String yellow = "\u001B[01;93m";
    public final static String green = "\u001B[01;32m";
    public final static String grey = "\u001B[01;90m";
    public final static String red = "\u001B[01;31m";
    public final static String reset = "\u001B[0m";

    public static String color(String color, String msg) {
        if (getColor()) {
            return color + msg + reset;
        } else {
            return msg;
        }
    }
    public static String purple(String msg) {
        return color(purple, msg);
    }
    public static String blue(String msg) {
        return color(blue, msg);
    }
    public static String yellow(String msg) {
        return color(yellow, msg);
    }
    public static String green(String msg) {
        return color(green, msg);
    }
    public static String red(String msg) {
        return color(red, msg);
    }
    public static String grey(String msg) {
        return color(grey, msg);
    }

    public static boolean getColor() {
        return color;
    }
    public static void setColor(boolean setColor) {
        color = setColor;
    }

    private static boolean color;
}
