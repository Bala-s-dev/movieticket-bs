package com.movieticket.controller;

import com.movieticket.exception.ApplicationException;
import com.movieticket.model.Admin;
import com.movieticket.model.Theatre;
import com.movieticket.service.TheatreService;
import com.movieticket.util.ConsoleUtil;
import com.movieticket.util.InputUtil;

import java.util.List;

public class TheatreController {

    private final TheatreService theatreService;
    private final InputUtil input;

    public TheatreController(TheatreService theatreService, InputUtil input) {
        this.theatreService = theatreService;
        this.input = input;
    }

    public void showTheatreMenu(Admin admin) {
        boolean back = false;
        while (!back) {
            ConsoleUtil.printHeader("THEATRE SECTION");
            System.out.println("1. Add Theatre");
            System.out.println("2. Remove Theatre");
            System.out.println("3. View My Theatres");
            System.out.println("4. Back");
            int choice = input.readInt("Enter your choice: ");
            switch (choice) {
                case 1 -> addTheatre(admin);
                case 2 -> removeTheatre(admin);
                case 3 -> viewMyTheatres(admin);
                case 4 -> back = true;
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void addTheatre(Admin admin) {
        try {
            String name = input.readNonEmptyString("Theatre name: ");
            String location = input.readNonEmptyString("Location: ");
            Theatre theatre = theatreService.addTheatre(admin.getAdminId(), name, location);
            ConsoleUtil.printSuccess("Theatre added successfully with ID: " + theatre.getTheatreId());
        } catch (ApplicationException e) {
            ConsoleUtil.printError(e.getMessage());
        }
    }

    private void removeTheatre(Admin admin) {
        try {
            long theatreId = input.readLong("Enter Theatre ID to remove: ");
            theatreService.removeTheatre(theatreId, admin.getAdminId());
            ConsoleUtil.printSuccess("Theatre removed successfully.");
        } catch (ApplicationException e) {
            ConsoleUtil.printError(e.getMessage());
        }
    }

    public void viewMyTheatres(Admin admin) {
        List<Theatre> theatres = theatreService.viewMyTheatres(admin.getAdminId());
        if (theatres.isEmpty()) {
            System.out.println("You have no theatres yet.");
            return;
        }
        ConsoleUtil.printLine();
        System.out.printf("%-8s | %-25s | %-20s | %-8s%n", "ID", "Name", "Location", "Status");
        ConsoleUtil.printLine();
        for (Theatre t : theatres) {
            System.out.printf("%-8d | %-25s | %-20s | %-8s%n",
                    t.getTheatreId(), t.getName(), t.getLocation(), t.isActive() ? "ACTIVE" : "REMOVED");
        }
        ConsoleUtil.printLine();
    }
}
