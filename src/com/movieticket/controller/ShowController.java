package com.movieticket.controller;

import com.movieticket.exception.ApplicationException;
import com.movieticket.model.Admin;
import com.movieticket.model.Movie;
import com.movieticket.model.Show;
import com.movieticket.model.TicketPricing;
import com.movieticket.service.MovieService;
import com.movieticket.service.PricingConfigService;
import com.movieticket.service.ShowService;
import com.movieticket.util.ConsoleUtil;
import com.movieticket.util.DateTimeUtil;
import com.movieticket.util.InputUtil;

import java.time.LocalDateTime;
import java.util.List;

public class ShowController {

    private final ShowService showService;
    private final MovieService movieService;
    private final PricingConfigService pricingConfigService;
    private final InputUtil input;

    public ShowController(ShowService showService, MovieService movieService,
                          PricingConfigService pricingConfigService, InputUtil input) {
        this.showService = showService;
        this.movieService = movieService;
        this.pricingConfigService = pricingConfigService;
        this.input = input;
    }

    public void showShowMenu(Admin admin) {
        boolean back = false;
        while (!back) {
            ConsoleUtil.printHeader("SHOW SECTION");
            System.out.println("1. Add Show");
            System.out.println("2. Remove Show");
            System.out.println("3. View Shows");
            System.out.println("4. Search Shows");
            System.out.println("5. Back");
            int choice = input.readInt("Enter your choice: ");
            switch (choice) {
                case 1 -> addShow(admin);
                case 2 -> removeShow(admin);
                case 3 -> viewShows(admin);
                case 4 -> searchShows();
                case 5 -> back = true;
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void addShow(Admin admin) {
        try {
            long theatreId = input.readLong("Enter Theatre ID: ");
            long screenId = input.readLong("Enter Screen ID: ");
            long movieId = input.readLong("Enter Movie ID: ");
            LocalDateTime start = input.readDateTime("Show start");
            LocalDateTime end;
            boolean deriveEnd = input.readYesNo("Derive end time from movie duration?");
            if (deriveEnd) {
                Movie movie = movieService.getMovie(movieId);
                end = start.plusMinutes(movie.getDurationMinutes());
                System.out.println("Derived end time: " + DateTimeUtil.formatDateTime(end));
            } else {
                end = input.readDateTime("Show end");
            }

            TicketPricing pricing;
            System.out.println("1. Use Default Pricing  2. Customize Pricing");
            int pricingChoice = input.readInt("Enter choice: ");
            if (pricingChoice == 2) {
                double gold = input.readDouble("Enter Gold Price: ");
                double platinum = input.readDouble("Enter Platinum Price: ");
                double silver = input.readDouble("Enter Silver Price: ");
                if (gold < 0 || platinum < 0 || silver < 0) {
                    ConsoleUtil.printError("Prices must be non-negative.");
                    return;
                }
                pricing = new TicketPricing(gold, platinum, silver);
            } else {
                double price = input.readDouble("Enter Price: ");
                pricing = pricingConfigService.getDefaultPricing(price);
            }

            Show show = showService.addShow(admin.getAdminId(), theatreId, screenId, movieId, start, end, pricing);
            ConsoleUtil.printSuccess("Show added successfully with ID: " + show.getShowId());
        } catch (ApplicationException e) {
            ConsoleUtil.printError(e.getMessage());
        }
    }

    private void removeShow(Admin admin) {
        try {
            long showId = input.readLong("Enter Show ID to remove: ");
            showService.removeShow(showId, admin.getAdminId());
            ConsoleUtil.printSuccess("Show removed successfully.");
        } catch (ApplicationException e) {
            ConsoleUtil.printError(e.getMessage());
        }
    }

    private void viewShows(Admin admin) {
        List<Show> shows = showService.viewShowsForAdmin(admin.getAdminId());
        printShowTable(shows);
    }

    private void searchShows() {
        long movieId = input.readLong("Enter Movie ID to search shows for: ");
        List<Show> shows = showService.viewUpcomingShowsForMovie(movieId);
        printShowTable(shows);
    }

    static void printShowTable(List<Show> shows) {
        if (shows.isEmpty()) {
            System.out.println("No shows found.");
            return;
        }
        ConsoleUtil.printLine();
        System.out.printf("%-8s | %-8s | %-8s | %-20s | %-8s%n", "ShowID", "MovieID", "ScreenID", "Start", "Status");
        ConsoleUtil.printLine();
        for (Show s : shows) {
            System.out.printf("%-8d | %-8d | %-8d | %-20s | %-8s%n",
                    s.getShowId(), s.getMovieId(), s.getScreenId(),
                    DateTimeUtil.formatDateTime(s.getStartDateTime()), s.isActive() ? "ACTIVE" : "REMOVED");
        }
        ConsoleUtil.printLine();
    }
}
