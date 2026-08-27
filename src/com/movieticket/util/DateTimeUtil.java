package com.movieticket.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class DateTimeUtil {

    public static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("HH:mm");

    public static final DateTimeFormatter DISPLAY_DATE =
            DateTimeFormatter.ofPattern("dd-MMM-yyyy");

    public static final DateTimeFormatter DISPLAY_TIME =
            DateTimeFormatter.ofPattern("hh:mm a");

    private DateTimeUtil() { }

    public static LocalDate parseDate(String text) {
        try {
            LocalDate date = LocalDate.parse(text.trim(), DATE_FORMAT);
            // System.out.println(date);
            // if (date.isBefore(LocalDate.now())) {
            //     throw new IllegalArgumentException(
            //             "Date cannot be before the current date."
            //     );
            // }

            return date;

        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Invalid date format. Expected dd-MM-yyyy."
            );
        }
    }

    public static LocalTime parseTime(String text) {
        try {
            LocalTime time = LocalTime.parse(text.trim(), TIME_FORMAT);

            return time;

        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Invalid time format. Expected HH:mm (24-hour)."
            );
        }
    }

    public static void validateDateTime(LocalDate date, LocalTime time) {

        LocalDateTime selectedDateTime = LocalDateTime.of(date, time);
        LocalDateTime currentDateTime = LocalDateTime.now();

        if (!selectedDateTime.isAfter(currentDateTime)) {
            throw new IllegalArgumentException(
                    "Selected date and time must be in the future."
            );
        }
    }

    public static String formatDate(LocalDate date) {
        return date.format(DISPLAY_DATE);
    }

    public static String formatTime(LocalTime time) {
        return time.format(DISPLAY_TIME);
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime.toLocalDate().format(DISPLAY_DATE)
                + " "
                + dateTime.toLocalTime().format(DISPLAY_TIME);
    }
}