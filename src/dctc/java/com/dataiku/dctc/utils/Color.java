package com.dataiku.dctc.utils;

public class Color {
    public final static String blue = "34m";
    public final static String purple = "35m";
    public final static String yellow = "93m";
    public final static String green = "32m";
    public final static String grey = "90m";
    public final static String red = "31m";
    public final static String reset = "0m";
    public final static String bold = "1m";
    private final static String colorChar = "\u001B[";

    public static String color(String color, String msg) {
        return colorChar + color + msg + colorChar + reset;
    }
    public static String bcolor(String color, String msg) {
        return colorChar
            + "0;1m"
            + colorChar
            + color
            + msg
            + colorChar
            + reset;
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

    public static String purple(char msg) {
        return color(purple, Character.toString(msg));
    }
    public static String blue(char msg) {
        return color(blue, Character.toString(msg));
    }
    public static String yellow(char msg) {
        return color(yellow, Character.toString(msg));
    }
    public static String green(char msg) {
        return color(green, Character.toString(msg));
    }
    public static String red(char msg) {
        return color(red, Character.toString(msg));
    }
    public static String grey(char msg) {
        return color(grey, Character.toString(msg));
    }

    public static String bpurple(String msg) {
        return color(purple, msg);
    }
    public static String bblue(String msg) {
        return color(blue, msg);
    }
    public static String byellow(String msg) {
        return color(yellow, msg);
    }
    public static String bgreen(String msg) {
        return color(green, msg);
    }
    public static String bred(String msg) {
        return color(red, msg);
    }
    public static String bgrey(String msg) {
        return color(grey, msg);
    }
    public static String bold(String msg) {
        return color(bold, msg);
    }

    public static boolean getColor() {
        return color;
    }
    public static void setColor(boolean setColor) {
        color = setColor;
    }

    private static boolean color;
}
