package com.movieticket.controller;

import com.movieticket.enums.SeatCategory;
import com.movieticket.exception.ApplicationException;
import com.movieticket.model.*;
import com.movieticket.service.*;
import com.movieticket.util.ConsoleUtil;
import com.movieticket.util.DateTimeUtil;
import com.movieticket.util.InputUtil;

import java.util.*;
import java.util.stream.Collectors;

public class UserController {

    private final AuthService authService;
    private final MovieService movieService;
    private final ShowService showService;
    private final ScreenService screenService;
    private final BookingService bookingService;
    private final TheatreService theatreService;
    private final InputUtil input;

    public UserController(AuthService authService, MovieService movieService, ShowService showService,
                          ScreenService screenService, BookingService bookingService,
                          TheatreService theatreService, InputUtil input) {
        this.authService = authService;
        this.movieService = movieService;
        this.showService = showService;
        this.screenService = screenService;
        this.bookingService = bookingService;
        this.theatreService = theatreService;
        this.input = input;
    }

    public void registerFlow() {
        try {
            ConsoleUtil.printHeader("USER REGISTRATION");
            String name = input.readNonEmptyString("Name: ");
            String email = input.readNonEmptyString("Email: ");
            String phone = input.readNonEmptyString("Phone (10 digits): ");
            String password = input.readNonEmptyString("Password: ");
            User user = authService.registerUser(name, email, phone, password);
            ConsoleUtil.printSuccess("User registered successfully with ID: " + user.getUserId());
        } catch (ApplicationException e) {
            ConsoleUtil.printError(e.getMessage());
        }
    }

    public void loginFlow() {
        ConsoleUtil.printHeader("USER LOGIN");
        String email = input.readString("Email: ");
        String password = input.readString("Password: ");
        try {
            User user = authService.loginUser(email, password);
            System.out.println("Welcome, " + user.getName());
            showUserMenu(user);
        } catch (ApplicationException e) {
            ConsoleUtil.printError(e.getMessage());
        }
    }

    private void showUserMenu(User user) {
        boolean logout = false;
        while (!logout) {
            ConsoleUtil.printHeader("USER MENU");
            System.out.println("1. Browse Movies");
            System.out.println("2. Search Movie");
            System.out.println("3. View Shows");
            System.out.println("4. View Available Seats");
            System.out.println("5. Book Movie");
            System.out.println("6. Cancel Booking");
            System.out.println("7. Booking History");
            System.out.println("8. Logout");
            int choice = input.readInt("Enter your choice: ");
            switch (choice) {
                case 1 -> browseMovies();
                case 2 -> searchMovie();
                case 3 -> viewShows();
                case 4 -> viewAvailableSeats();
                case 5 -> bookMovie(user);
                case 6 -> cancelBooking(user);
                case 7 -> bookingHistory(user);
                case 8 -> {
                    logout = true;
                    System.out.println("Logged out successfully.");
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void browseMovies() {
        List<Movie> movies = movieService.browseAvailableMovies();
        MovieController.printMovieTable(movies);
    }

    private void searchMovie() {
        String query = input.readString("Search: ");
        List<Movie> results = movieService.searchMovies(query, true);
        MovieController.printMovieTable(results);
    }

    private void viewShows() {
        long movieId = input.readLong("Enter Movie ID: ");
        try {
            Movie movie = movieService.getMovieOrThrow(movieId);
            List<Show> shows = showService.viewUpcomingShowsForMovie(movieId);
            if (shows.isEmpty()) {
                System.out.println("No upcoming shows for " + movie.getName() + ".");
                return;
            }
            System.out.println("Movie: " + movie.getName());
            int i = 1;
            for (Show s : shows) {
                Screen screen = screenService.getScreenOrThrow(s.getScreenId());
                Theatre theatre = theatreService.getTheatreOrThrow(screen.getTheatreId());
                double minPrice = Math.min(s.getPricing().getPrice(SeatCategory.SILVER),
                        Math.min(s.getPricing().getPrice(SeatCategory.GOLD), s.getPricing().getPrice(SeatCategory.PLATINUM)));
                System.out.println();
                System.out.println(i + ". Theatre: " + theatre.getName());
                System.out.println("   Screen: " + screen.getScreenName());
                System.out.println("   Date: " + DateTimeUtil.formatDate(s.getStartDateTime().toLocalDate()));
                System.out.println("   Time: " + DateTimeUtil.formatTime(s.getStartDateTime().toLocalTime()));
                System.out.println("   Show ID: " + s.getShowId());
                System.out.println("   Starting Price: Rs." + minPrice);
                i++;
            }
        } catch (ApplicationException e) {
            ConsoleUtil.printError(e.getMessage());
        }
    }

    private void viewAvailableSeats() {
        try {
            long showId = input.readLong("Enter Show ID: ");
            Show show = showService.getShowOrThrow(showId);
            Screen screen = screenService.getScreenOrThrow(show.getScreenId());
            List<ShowSeat> showSeats = bookingService.getShowSeatStates(showId);
            printSeatGrid(screen, showSeats, Collections.emptySet());
        } catch (ApplicationException e) {
            ConsoleUtil.printError(e.getMessage());
        }
    }

    private void bookMovie(User user) {
        try {
            long showId = input.readLong("Enter Show ID to book: ");
            Show show = showService.getShowOrThrow(showId);
            Screen screen = screenService.getScreenOrThrow(show.getScreenId());
            List<ShowSeat> showSeats = bookingService.getShowSeatStates(showId);
            printSeatGrid(screen, showSeats, Collections.emptySet());

            String seatInput = input.readNonEmptyString("Enter seats (comma-separated, e.g. A3,A4,B5): ");
            List<String> seatLabels = Arrays.stream(seatInput.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();

            double total = 0.0;
            List<Seat> selected = new ArrayList<>();

            for (String label : seatLabels) {
                Optional<Seat> seatOpt = screen.getAllSeats().stream()
                        .filter(s -> s.getLabel().equalsIgnoreCase(label))
                        .findFirst();
                if (seatOpt.isEmpty()) {
                    ConsoleUtil.printError("Seat " + label + " does not exist on this screen.");
                    return;
                }
                selected.add(seatOpt.get());
            }
            System.out.println();
            System.out.println("Selected Seats:");
            for (Seat seat : selected) {
                double price = show.getPricing().getPrice(seat.getCategory());
                total += price;
                System.out.println(seat.getLabel() + " -> " + seat.getCategory() + " -> Rs." + price);
            }
            System.out.println();
            System.out.println("Total: Rs." + total);

            boolean confirm = input.readYesNo("Confirm booking?");
            if (!confirm) {
                System.out.println("Booking cancelled by user.");
                return;
            }

            Booking booking = bookingService.bookSeats(user.getUserId(), showId, seatLabels);
            printBookingConfirmation(booking, show, screen);
        } catch (ApplicationException e) {
            ConsoleUtil.printError(e.getMessage());
        }
    }

    private void printBookingConfirmation(Booking booking, Show show, Screen screen) {
        Movie movie = movieService.getMovieOrThrow(show.getMovieId());
        Theatre theatre = theatreService.getTheatreOrThrow(screen.getTheatreId());
        System.out.println();
        ConsoleUtil.printHeader("BOOKING CONFIRMED");
        System.out.println("Booking ID : B" + booking.getBookingId());
        System.out.println("Movie      : " + movie.getName());
        System.out.println("Theatre    : " + theatre.getName());
        System.out.println("Screen     : " + screen.getScreenName());
        System.out.println("Date       : " + DateTimeUtil.formatDate(show.getStartDateTime().toLocalDate()));
        System.out.println("Time       : " + DateTimeUtil.formatTime(show.getStartDateTime().toLocalTime()));
        System.out.println();
        System.out.println("Seats:");
        for (Long seatId : booking.getSeatIds()) {
            screen.getAllSeats().stream()
                    .filter(s -> s.getSeatId() == seatId)
                    .findFirst()
                    .ifPresent(s -> System.out.println(s.getLabel()));
        }
        System.out.println();
        System.out.println("Total Amount: Rs." + booking.getTotalAmount());
        System.out.println();
        System.out.println("Status: " + booking.getStatus());
        ConsoleUtil.printLine();
    }

    private void cancelBooking(User user) {
        try {
            long bookingId = input.readLong("Enter Booking ID: ");
            bookingService.cancelBooking(user.getUserId(), bookingId);
            ConsoleUtil.printSuccess("Booking cancelled successfully. Seats are now available again.");
        } catch (ApplicationException e) {
            ConsoleUtil.printError(e.getMessage());
        }
    }

    private void bookingHistory(User user) {
        List<Booking> bookings = bookingService.getBookingHistory(user.getUserId());
        if (bookings.isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }
        ConsoleUtil.printLine();
        System.out.printf("%-10s | %-20s | %-12s | %-20s | %-10s | %-10s%n",
                "BookingID", "Movie", "Date", "Seats", "Amount", "Status");
        ConsoleUtil.printLine();
        for (Booking b : bookings) {
            try {
                Show show = showService.getShowOrThrow(b.getShowId());
                Movie movie = movieService.getMovieOrThrow(show.getMovieId());
                Screen screen = screenService.getScreenOrThrow(show.getScreenId());
                String seatLabels = b.getSeatIds().stream()
                        .map(id -> screen.getAllSeats().stream()
                                .filter(s -> s.getSeatId() == id)
                                .findFirst()
                                .map(Seat::getLabel)
                                .orElse("?"))
                        .collect(Collectors.joining(","));
                System.out.printf("%-10s | %-20s | %-12s | %-20s | %-10.1f | %-10s%n",
                        "B" + b.getBookingId(), movie.getName(),
                        DateTimeUtil.formatDate(show.getStartDateTime().toLocalDate()),
                        seatLabels, b.getTotalAmount(), b.getStatus());
            } catch (ApplicationException e) {
                System.out.printf("%-10s | %-20s%n", "B" + b.getBookingId(), "(unavailable)");
            }
        }
        ConsoleUtil.printLine();
    }

    private void printSeatGrid(Screen screen, List<ShowSeat> showSeats, Set<Long> selectedSeatIds) {
        Map<Long, ShowSeat> stateBySeatId = new HashMap<>();
        for (ShowSeat ss : showSeats) {
            stateBySeatId.put(ss.getSeatId(), ss);
        }
        System.out.println("                 SCREEN");
        System.out.println("        ----------------------------------");
        for (Map.Entry<Character, List<Seat>> entry : screen.getSeatLayout().entrySet()) {
            List<Seat> seats = entry.getValue();
            if (seats.isEmpty()) continue;
            System.out.println();
            System.out.println(seats.get(0).getCategory());
            StringBuilder sb = new StringBuilder();
            for (Seat seat : seats) {
                ShowSeat state = stateBySeatId.get(seat.getSeatId());
                String display;
                if (selectedSeatIds.contains(seat.getSeatId())) {
                    display = "**";
                } else if (state != null && !state.isAvailable()) {
                    display = "XX";
                } else {
                    display = seat.getLabel();
                }
                sb.append("[").append(display).append("] ");
            }
            System.out.println(sb.toString().trim());
        }
        System.out.println();
        System.out.println("[label] = available   [XX] = booked   [**] = selected");
    }
}
