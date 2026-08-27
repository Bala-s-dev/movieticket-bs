package com.movieticket.controller;

import com.movieticket.exception.ApplicationException;
import com.movieticket.model.Admin;
import com.movieticket.service.AuthService;
import com.movieticket.util.ConsoleUtil;
import com.movieticket.util.InputUtil;

public class AdminController {

    private final AuthService authService;
    private final MovieController movieController;
    private final TheatreController theatreController;
    private final ScreenController screenController;
    private final ShowController showController;
    private final ReportController reportController;
    private final InputUtil input;

    public AdminController(AuthService authService, MovieController movieController,
                           TheatreController theatreController, ScreenController screenController,
                           ShowController showController, ReportController reportController,
                           InputUtil input) {
        this.authService = authService;
        this.movieController = movieController;
        this.theatreController = theatreController;
        this.screenController = screenController;
        this.showController = showController;
        this.reportController = reportController;
        this.input = input;
    }

    public void registerFlow() {
        try {
            ConsoleUtil.printHeader("ADMIN REGISTRATION");
            String name = input.readNonEmptyString("Name: ");
            String email = input.readNonEmptyString("Email: ");
            String phone = input.readNonEmptyString("Phone (10 digits): ");
            String password = input.readNonEmptyString("Password: ");
            Admin admin = authService.registerAdmin(name, email, phone, password);
            ConsoleUtil.printSuccess("Admin registered successfully with ID: " + admin.getAdminId());
        } catch (ApplicationException e) {
            ConsoleUtil.printError(e.getMessage());
        }
    }

    public void loginFlow() {
        ConsoleUtil.printHeader("ADMIN LOGIN");
        String email = input.readString("Email: ");
        String password = input.readString("Password: ");
        try {
            Admin admin = authService.loginAdmin(email, password);
            System.out.println("Welcome, " + admin.getName());
            showAdminMenu(admin);
        } catch (ApplicationException e) {
            ConsoleUtil.printError(e.getMessage());
        }
    }

    private void showAdminMenu(Admin admin) {
        boolean logout = false;
        while (!logout) {
            ConsoleUtil.printHeader("ADMIN MENU");
            System.out.println("1. Movie Section");
            System.out.println("2. Theatre Section");
            System.out.println("3. Screen Section");
            System.out.println("4. Show Section");
            System.out.println("5. Revenue Report");
            System.out.println("6. Logout");
            int choice = input.readInt("Enter your choice: ");
            switch (choice) {
                case 1 -> movieController.showMovieMenu();
                case 2 -> theatreController.showTheatreMenu(admin);
                case 3 -> screenController.showScreenMenu(admin);
                case 4 -> showController.showShowMenu(admin);
                case 5 -> reportController.showRevenueReport(admin);
                case 6 -> {
                    logout = true;
                    System.out.println("Logged out successfully.");
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
