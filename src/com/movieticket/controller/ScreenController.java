package com.movieticket.controller;

import com.movieticket.enums.SeatCategory;
import com.movieticket.exception.ApplicationException;
import com.movieticket.model.Admin;
import com.movieticket.model.Screen;
import com.movieticket.model.Seat;
import com.movieticket.service.ScreenService;
import com.movieticket.util.ConsoleUtil;
import com.movieticket.util.InputUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScreenController {

    private final ScreenService screenService;
    private final InputUtil input;

    public ScreenController(ScreenService screenService, InputUtil input) {
        this.screenService = screenService;
        this.input = input;
    }

    public void showScreenMenu(Admin admin) {
        boolean back = false;
        while (!back) {
            ConsoleUtil.printHeader("SCREEN SECTION");
            System.out.println("1. Add Screen");
            System.out.println("2. Remove Screen");
            System.out.println("3. View Screens");
            System.out.println("4. View Seat Layout");
            System.out.println("5. Back");
            int choice = input.readInt("Enter your choice: ");
            switch (choice) {
                case 1 -> addScreen(admin);
                case 2 -> removeScreen(admin);
                case 3 -> viewScreens(admin);
                case 4 -> viewSeatLayout();
                case 5 -> back = true;
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void addScreen(Admin admin) {
        try {
            long theatreId = input.readLong("Enter Theatre ID: ");
            String screenName = input.readNonEmptyString("Screen name: ");
            int rowCount = input.readInt("How many rows to configure? ");
            if (rowCount <= 0) {
                ConsoleUtil.printError("At least one row must be configured.");
                return;
            }
            List<ScreenService.RowConfig> rowConfigs = new ArrayList<>();
            for (int i = 0; i < rowCount; i++) {
                System.out.println("--- Row " + (i + 1) + " ---");
                String rowLetterStr = input.readNonEmptyString("Row letter (e.g. A): ").toUpperCase();
                char rowLetter = rowLetterStr.charAt(0);
                SeatCategory category = readCategory();
                int seatCount = input.readInt("Number of seats in row " + rowLetter + ": ");
                rowConfigs.add(new ScreenService.RowConfig(rowLetter, category, seatCount));
            }
            Screen screen = screenService.addScreen(theatreId, admin.getAdminId(), screenName, rowConfigs);
            ConsoleUtil.printSuccess("Screen added successfully with ID: " + screen.getScreenId() +
                    " (" + screen.getTotalSeatCount() + " seats)");
        } catch (ApplicationException e) {
            ConsoleUtil.printError(e.getMessage());
        }
    }

    private SeatCategory readCategory() {
        while (true) {
            System.out.println("Category: 1. GOLD  2. PLATINUM  3. SILVER");
            int choice = input.readInt("Enter choice: ");
            switch (choice) {
                case 1: return SeatCategory.GOLD;
                case 2: return SeatCategory.PLATINUM;
                case 3: return SeatCategory.SILVER;
                default: System.out.println("Invalid choice.");
            }
        }
    }

    private void removeScreen(Admin admin) {
        try {
            long screenId = input.readLong("Enter Screen ID to remove: ");
            screenService.removeScreen(screenId, admin.getAdminId());
            ConsoleUtil.printSuccess("Screen removed successfully.");
        } catch (ApplicationException e) {
            ConsoleUtil.printError(e.getMessage());
        }
    }

    private void viewScreens(Admin admin) {
        try {
            long theatreId = input.readLong("Enter Theatre ID: ");
            List<Screen> screens = screenService.viewScreens(theatreId, admin.getAdminId());
            if (screens.isEmpty()) {
                System.out.println("No screens found for this theatre.");
                return;
            }
            ConsoleUtil.printLine();
            System.out.printf("%-10s | %-20s | %-10s | %-8s%n", "ID", "Name", "Seats", "Status");
            ConsoleUtil.printLine();
            for (Screen s : screens) {
                System.out.printf("%-10d | %-20s | %-10d | %-8s%n",
                        s.getScreenId(), s.getScreenName(), s.getTotalSeatCount(), s.isActive() ? "ACTIVE" : "REMOVED");
            }
            ConsoleUtil.printLine();
        } catch (ApplicationException e) {
            ConsoleUtil.printError(e.getMessage());
        }
    }

    private void viewSeatLayout() {
        try {
            long screenId = input.readLong("Enter Screen ID: ");
            Screen screen = screenService.getScreenOrThrow(screenId);
            printSeatLayout(screen);
        } catch (ApplicationException e) {
            ConsoleUtil.printError(e.getMessage());
        }
    }

    static void printSeatLayout(Screen screen) {
        System.out.println("                 SCREEN");
        System.out.println("        -------------------------");
        for (Map.Entry<Character, List<Seat>> entry : screen.getSeatLayout().entrySet()) {
            List<Seat> seats = entry.getValue();
            SeatCategory category = seats.isEmpty() ? null : seats.get(0).getCategory();
            System.out.println();
            System.out.println(category);
            StringBuilder sb = new StringBuilder();
            for (Seat seat : seats) {
                sb.append(seat.getLabel()).append(" ");
            }
            System.out.println(sb.toString().trim());
        }
    }
}
