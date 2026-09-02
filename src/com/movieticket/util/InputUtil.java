package com.movieticket.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Scanner;

public class InputUtil {

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
            String value = scanner.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("Input cannot be empty. Please try again.");
        }
    }

    public String readNonEmptyStringWithValidation(String prompt, String regex, String errorMessage) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            if (!value.isEmpty() && value.matches(regex)) {
                return value;
            }
            System.out.println(errorMessage);
        }
    }

    public int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please enter a valid integer.");
            }
        }
    }

    public long readLong(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please enter a valid ID.");
            }
        }
    }

    public double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please enter a valid price/amount.");
            }
        }
    }

    public LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt + " (dd-MM-yyyy): ");
            String value = scanner.nextLine().trim();
            try {
                return DateTimeUtil.parseDate(value);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public LocalDateTime readDateTime(String prompt) {
        while (true) {
            System.out.print(prompt + " date (dd-MM-yyyy): ");
            String dateValue = scanner.nextLine().trim();
            System.out.print(prompt + " time (HH:mm, 24-hour): ");
            String timeValue = scanner.nextLine().trim();
            try {
                LocalDate date = DateTimeUtil.parseDate(dateValue);
                LocalTime time = DateTimeUtil.parseTime(timeValue);
                DateTimeUtil.validateDateTime(date, time);
                return LocalDateTime.of(date, time);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public boolean readYesNo(String prompt) {
        while (true) {
            System.out.print(prompt + " (1=Yes, 2=No): ");
            String value = scanner.nextLine().trim();
            if (value.equals("1")) return true;
            if (value.equals("2")) return false;
            System.out.println("Please enter 1 or 2.");
        }
    }
}
