package com.movieticket.controller;

import com.movieticket.util.ConsoleUtil;
import com.movieticket.util.InputUtil;

public class MainMenuController {

    private final UserController userController;
    private final AdminController adminController;
    private final InputUtil input;

    public MainMenuController(UserController userController, AdminController adminController, InputUtil input) {
        this.userController = userController;
        this.adminController = adminController;
        this.input = input;
    }

    public void run() {
        boolean exit = false;
        while (!exit) {
            ConsoleUtil.printHeader("MOVIE TICKET BOOKING SYSTEM");
            System.out.println("1. User Login");
            System.out.println("2. User Registration");
            System.out.println("3. Admin Login");
            System.out.println("4. Admin Registration");
            System.out.println("5. Exit");
            int choice = input.readInt("Enter your choice: ");
            switch (choice) {
                case 1 -> userController.loginFlow();
                case 2 -> userController.registerFlow();
                case 3 -> adminController.loginFlow();
                case 4 -> adminController.registerFlow();
                case 5 -> {
                    exit = true;
                    System.out.println("Thank you for using Movie Ticket Booking System. Goodbye!");
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
