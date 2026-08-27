package com.movieticket.controller;

import com.movieticket.exception.ApplicationException;
import com.movieticket.model.Movie;
import com.movieticket.service.MovieService;
import com.movieticket.util.ConsoleUtil;
import com.movieticket.util.InputUtil;

import java.time.LocalDate;
import java.util.List;

public class MovieController {

    private final MovieService movieService;
    private final InputUtil input;

    public MovieController(MovieService movieService, InputUtil input) {
        this.movieService = movieService;
        this.input = input;
    }

    public void showMovieMenu() {
        boolean back = false;
        while (!back) {
            ConsoleUtil.printHeader("MOVIE SECTION");
            System.out.println("1. Add Movie");
            System.out.println("2. Remove Movie");
            System.out.println("3. View Movies");
            System.out.println("4. Search Movie");
            System.out.println("5. Back");
            int choice = input.readInt("Enter your choice: ");
            switch (choice) {
                case 1 -> addMovie();
                case 2 -> removeMovie();
                case 3 -> viewMovies();
                case 4 -> searchMovie();
                case 5 -> back = true;
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void addMovie() {
        try {
            String name = input.readNonEmptyString("Movie name: ");
            String description = input.readString("Description: ");
            String language = input.readNonEmptyString("Language: ");
            String genre = input.readNonEmptyString("Genre: ");
            int duration = input.readInt("Duration (minutes): ");
            LocalDate releaseDate = input.readDate("Release date");
            double rating = input.readDouble("Rating (0-10): ");

            Movie movie = movieService.addMovie(name, description, language, genre, duration, releaseDate, rating);
            ConsoleUtil.printSuccess("Movie added successfully with ID: " + movie.getMovieId());
        } catch (ApplicationException | IllegalArgumentException e) {
            ConsoleUtil.printError(e.getMessage());
        }
    }

    private void removeMovie() {
        try {
            long movieId = input.readLong("Enter Movie ID to remove: ");
            movieService.removeMovie(movieId);
            ConsoleUtil.printSuccess("Movie removed successfully.");
        } catch (ApplicationException e) {
            ConsoleUtil.printError(e.getMessage());
        }
    }

    private void viewMovies() {
        List<Movie> movies = movieService.viewAllMovies();
        printMovieTable(movies);
    }

    private void searchMovie() {
        String query = input.readString("Enter search text: ");
        List<Movie> results = movieService.searchMovies(query, false);
        printMovieTable(results);
    }

    static void printMovieTable(List<Movie> movies) {
        if (movies.isEmpty()) {
            System.out.println("No movies found.");
            return;
        }
        ConsoleUtil.printLine();
        System.out.printf("%-6s | %-25s | %-10s | %-12s | %-6s | %-8s%n",
                "ID", "Name", "Language", "Genre", "Rating", "Status");
        ConsoleUtil.printLine();
        for (Movie m : movies) {
            System.out.printf("%-6d | %-25s | %-10s | %-12s | %-6.1f | %-8s%n",
                    m.getMovieId(), m.getName(), m.getLanguage(), m.getGenre(), m.getRating(),
                    m.isActive() ? "ACTIVE" : "REMOVED");
        }
        ConsoleUtil.printLine();
    }
}
