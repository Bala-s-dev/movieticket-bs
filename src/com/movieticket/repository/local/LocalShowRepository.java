package com.movieticket.repository.local;

import com.movieticket.model.Show;
import com.movieticket.repository.ShowRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class LocalShowRepository implements ShowRepository {

    private final Map<Long, Show> shows = new HashMap<>();

    @Override
    public Show save(Show show) {
        shows.put(show.getShowId(), show);
        return show;
    }

    @Override
    public Optional<Show> findById(long id) {
        return Optional.ofNullable(shows.get(id));
    }

    @Override
    public List<Show> findAll() {
        return new ArrayList<>(shows.values());
    }

    @Override
    public List<Show> findByScreenId(long screenId) {
        return shows.values().stream()
                .filter(s -> s.getScreenId() == screenId)
                .toList();
    }

    @Override
    public List<Show> findByMovieId(long movieId) {
        return shows.values().stream()
                .filter(s -> s.getMovieId() == movieId)
                .toList();
    }

    @Override
    public void deleteById(long id) {
        if(id <= 0){
            throw new IllegalArgumentException("Invalid show id.");
        }   
        if(shows.containsKey(id)){
            shows.remove(id);
        } else {
            throw new IllegalArgumentException("Show with id " + id + " does not exist.");
        }
    }
}
