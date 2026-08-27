package com.movieticket.repository.local;

import com.movieticket.model.Admin;
import com.movieticket.repository.AdminRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class LocalAdminRepository implements AdminRepository {

    private final Map<Long, Admin> admins = new HashMap<>();

    @Override
    public Admin save(Admin admin) {
        admins.put(admin.getAdminId(), admin);
        return admin;
    }

    @Override
    public Optional<Admin> findById(long id) {
        return Optional.ofNullable(admins.get(id));
    }

    @Override
    public Optional<Admin> findByEmail(String email) {
        return admins.values().stream()
                .filter(a -> a.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    @Override
    public List<Admin> findAll() {
        return new ArrayList<>(admins.values());
    }

    @Override
    public boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }
}
