package com.movieticket.repository.local;

import com.movieticket.model.Movie;
import com.movieticket.repository.MovieRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class LocalMovieRepository implements MovieRepository {

    private final Map<Long, Movie> movies = new HashMap<>();

    @Override
    public Movie save(Movie movie) {
        movies.put(movie.getMovieId(), movie);
        return movie;
    }

    @Override
    public Optional<Movie> findById(long id) {
        return Optional.ofNullable(movies.get(id));
    }

    @Override
    public List<Movie> findAll() {
        return new ArrayList<>(movies.values());
    }

    @Override
    public void deleteById(long id) {
        movies.remove(id);
    }
}
