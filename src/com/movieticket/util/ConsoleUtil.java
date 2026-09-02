package com.movieticket.util;

public final class ConsoleUtil {

    private ConsoleUtil() { }

    public static void printHeader(String title) {
        System.out.println();
        System.out.println(title);
        System.out.println();
    }

    public static void printLine() {
        System.out.println();
    }

    public static void printError(String message) {
        System.out.println("Error: " + message);
    }

    public static void printSuccess(String message) {
        System.out.println(message);
    }
}
