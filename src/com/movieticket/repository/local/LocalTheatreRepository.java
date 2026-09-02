package com.movieticket.repository.local;

import com.movieticket.model.Theatre;
import com.movieticket.repository.TheatreRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class LocalTheatreRepository implements TheatreRepository {

    private final Map<Long, Theatre> theatres = new HashMap<>();

    @Override
    public Theatre save(Theatre theatre) {
        
        if(theatre == null){
            throw new IllegalArgumentException("Theatre cannot be null.");
        }

        theatres.put(theatre.getTheatreId(), theatre);
        return theatre;
    }

    @Override
    public Optional<Theatre> findById(long id) {
        return Optional.ofNullable(theatres.get(id));
    }

    @Override
    public List<Theatre> findAll() {
        return new ArrayList<>(theatres.values());
    }

    @Override
    public List<Theatre> findByAdminId(long adminId) {
        return theatres.values().stream()
                .filter(t -> t.getAdminId() == adminId)
                .toList();
    }

    @Override
    public void deleteById(long id) {
        theatres.remove(id);
    }
}
