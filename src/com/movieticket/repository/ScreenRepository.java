package com.movieticket.repository;

import com.movieticket.model.Screen;

import java.util.List;
import java.util.Optional;

public interface ScreenRepository {
    Screen save(Screen screen);
    Optional<Screen> findById(long id);
    List<Screen> findAll();
    List<Screen> findByTheatreId(long theatreId);
    void deleteById(long id);
}
