package com.movieticket.util;

public final class ConsoleUtil {

    private ConsoleUtil() { }

    public static void printHeader(String title) {
        String line = "=========================================";
        System.out.println(line);
        int padding = (line.length() - title.length()) / 2;
        System.out.println(" ".repeat(Math.max(0, padding)) + title);
        System.out.println(line);
    }

    public static void printLine() {
        System.out.println("=========================================");
    }

    public static void printError(String message) {
        System.out.println("Error: " + message);
    }

    public static void printSuccess(String message) {
        System.out.println(message);
    }
}
