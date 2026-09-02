package com.movieticket.controller;

import com.movieticket.exception.ApplicationException;
import com.movieticket.model.Admin;
import com.movieticket.service.AuthService;
import com.movieticket.util.ConsoleUtil;
import com.movieticket.util.InputUtil;
import com.movieticket.util.validateUtil;

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

        ConsoleUtil.printHeader("ADMIN REGISTRATION");

        String name = input.readNonEmptyStringWithValidation("Name: ", "^[a-zA-Z\\s]+$", "Invalid name. Please enter a valid name.");

        String email = input.readNonEmptyStringWithValidation("Email: ", "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", "Invalid email. Please enter a valid email.");
        // validateUtil.validateEmail(email);

        String phone = input.readNonEmptyStringWithValidation("Phone: ", "^\\d{10}$", "Invalid phone number. Please enter a 10-digit phone number.");
        // validateUtil.validatePhone(phone);

        String password = input.readNonEmptyStringWithValidation("Password: ", "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$", "Invalid password. Please enter a valid password.");
        // validateUtil.validatePassword(password);

        try {
            Admin admin = authService.registerAdmin(name, email, phone, password);
            ConsoleUtil.printSuccess("Admin registered successfully with ID: " + admin.getAdminId());

            loginAdmin(email, password);
            
        } catch (ApplicationException e) {
            ConsoleUtil.printError(e.getMessage());
            System.out.println("Please try again.");
        }
        
    }

    public void loginFlow() {
        ConsoleUtil.printHeader("ADMIN LOGIN");
        String email = input.readString("Email: ");
        validateUtil.validateEmail(email);
        String password = input.readString("Password: ");
        validateUtil.validatePassword(password);

        loginAdmin(email, password);
        
    }

    public void loginAdmin(String email, String password){
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
