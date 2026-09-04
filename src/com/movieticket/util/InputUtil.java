package com.movieticket.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Scanner;

public class InputUtil {

    private static final String DATE_FORMAT = "dd-MM-yyyy";
    private static final String TIME_FORMAT = "HH:mm";

    private final Scanner scanner;

    public InputUtil(Scanner scanner) {
        this.scanner = scanner;
    }

    public String readLine() {
        return scanner.nextLine();
    }

    public String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public String readNonEmptyString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (!input.isEmpty()) {
                return input;
            }

            System.out.println("Input cannot be empty. Please try again.");
        }
    }

    public String readNonEmptyStringWithValidation(
            String prompt,
            String regex,
            String errorMessage) {

        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (!input.isEmpty() && input.matches(regex)) {
                return input;
            }

            System.out.println(errorMessage);
        }
    }

    public int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException exception) {
                System.out.println("Invalid number. Please enter a valid integer.");
            }
        }
    }

    public long readLong(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                return Long.parseLong(input);
            } catch (NumberFormatException exception) {
                System.out.println("Invalid number. Please enter a valid ID.");
            }
        }
    }

    public double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException exception) {
                System.out.println("Invalid number. Please enter a valid price/amount.");
            }
        }
    }

    public LocalDate readDate(String prompt) {
        while (true) {
            System.out.printf("%s (%s): ", prompt, DATE_FORMAT);
            String input = scanner.nextLine().trim();

            try {
                return DateTimeUtil.parseDate(input);
            } catch (IllegalArgumentException exception) {
                System.out.println(exception.getMessage());
            }
        }
    }

    public LocalDateTime readDateTime(String prompt) {
        while (true) {
            System.out.printf("%s date (%s): ", prompt, DATE_FORMAT);
            String dateInput = scanner.nextLine().trim();

            System.out.printf("%s time (%s, 24-hour): ", prompt, TIME_FORMAT);
            String timeInput = scanner.nextLine().trim();

            try {
                LocalDate date = DateTimeUtil.parseDate(dateInput);
                LocalTime time = DateTimeUtil.parseTime(timeInput);

                DateTimeUtil.validateDateTime(date, time);

                return LocalDateTime.of(date, time);
            } catch (IllegalArgumentException exception) {
                System.out.println(exception.getMessage());
            }
        }
    }

    public boolean readYesNo(String prompt) {
        while (true) {
            System.out.print(prompt + " (1=Yes, 2=No): ");
            String input = scanner.nextLine().trim();

            if ("1".equals(input)) {
                return true;
            }

            if ("2".equals(input)) {
                return false;
            }

            System.out.println("Please enter 1 or 2.");
        }
    }
}