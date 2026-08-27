package com.movieticket.repository.local;

import com.movieticket.model.Screen;
import com.movieticket.repository.ScreenRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class LocalScreenRepository implements ScreenRepository {

    private final Map<Long, Screen> screens = new HashMap<>();

    @Override
    public Screen save(Screen screen) {
        screens.put(screen.getScreenId(), screen);
        return screen;
    }

    @Override
    public Optional<Screen> findById(long id) {
        return Optional.ofNullable(screens.get(id));
    }

    @Override
    public List<Screen> findAll() {
        return new ArrayList<>(screens.values());
    }

    @Override
    public List<Screen> findByTheatreId(long theatreId) {
        return screens.values().stream()
                .filter(s -> s.getTheatreId() == theatreId)
                .toList();
    }

    @Override
    public void deleteById(long id) {
        screens.remove(id);
    }
}
