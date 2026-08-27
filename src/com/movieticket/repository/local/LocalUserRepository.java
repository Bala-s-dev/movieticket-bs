package com.movieticket.repository.local;

import com.movieticket.model.User;
import com.movieticket.repository.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class LocalUserRepository implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User save(User user) {
        users.put(user.getUserId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return users.values().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }
}
