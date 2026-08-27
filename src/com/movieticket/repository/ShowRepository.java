package com.movieticket.repository;

import com.movieticket.model.Show;

import java.util.List;
import java.util.Optional;

public interface ShowRepository {
    Show save(Show show);
    Optional<Show> findById(long id);
    List<Show> findAll();
    List<Show> findByScreenId(long screenId);
    List<Show> findByMovieId(long movieId);
    void deleteById(long id);
}
