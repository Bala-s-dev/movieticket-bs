package com.movieticket.service;

import com.movieticket.exception.ResourceNotFoundException;
import com.movieticket.exception.ValidationException;
import com.movieticket.model.Movie;
import com.movieticket.model.Show;
import com.movieticket.repository.MovieRepository;
import com.movieticket.repository.ShowRepository;
import com.movieticket.util.IdGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class MovieService {

    private final MovieRepository movieRepository;
    private final ShowRepository showRepository;

    public MovieService(MovieRepository movieRepository, ShowRepository showRepository) {
        this.movieRepository = movieRepository;
        this.showRepository = showRepository;
    }

    public Movie addMovie(String name, String description, String language, String genre,
                          int durationMinutes, LocalDate releaseDate, double rating) {

        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Movie name cannot be empty.");
        }

        if (durationMinutes <= 0) {
            throw new ValidationException("Movie duration must be positive.");
        }

        if (rating < 0 || rating > 10) {
            throw new ValidationException("Rating must be between 0 and 10.");
        }

        if (releaseDate == null) {
            throw new ValidationException("Release date must be valid.");
        }

        Movie movie = new Movie(IdGenerator.nextMovieId(), name, description, language,
                genre, durationMinutes, releaseDate, rating);
                
        return movieRepository.save(movie);
    }

    public void removeMovie(long movieId) {

        Movie movie = getMovie(movieId);
        LocalDateTime now = LocalDateTime.now();
        
        boolean hasActiveOrFutureShow = showRepository.findByMovieId(movieId).stream()
                .anyMatch(s -> s.isActive() && !s.getStartDateTime().isBefore(now));
        
        if (hasActiveOrFutureShow) {
            throw new ValidationException(
                    "Cannot remove movie '" + movie.getName() + "': it has active/future shows scheduled.");
        }

        movie.setActive(false);
        movieRepository.save(movie);
    }

    public Movie getMovie(long movieId) {
        return movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with ID: " + movieId));
    }

    public List<Movie> viewAllMovies() {
        return movieRepository.findAll();
    }

    public List<Movie> browseAvailableMovies() {

        LocalDateTime now = LocalDateTime.now();

        List<Long> movieIdsWithFutureShows = showRepository.findAll().stream()
                .filter(s -> s.isActive() && s.isUpcomingOrOngoing(now))
                .map(Show::getMovieId)
                .distinct()
                .toList();

        return movieRepository.findAll().stream()
                .filter(Movie::isActive)
                .filter(m -> movieIdsWithFutureShows.contains(m.getMovieId()))
                .toList();
    }

    public List<Movie> searchMovies(String query, boolean onlyWithFutureShows) {

        String q = query == null ? "" : query.trim().toLowerCase();
        List<Movie> base = onlyWithFutureShows ? browseAvailableMovies() : movieRepository.findAll();
        
        return base.stream()
                .filter(m -> m.getName().toLowerCase().contains(q))
                .toList();
    }
}
