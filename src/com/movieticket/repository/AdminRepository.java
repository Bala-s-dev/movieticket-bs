package com.movieticket.repository;

import com.movieticket.model.Admin;

import java.util.List;
import java.util.Optional;

public interface AdminRepository {

    Admin save(Admin admin);
    Optional<Admin> findById(long id);
    Optional<Admin> findByEmail(String email);
    List<Admin> findAll();
    boolean existsByEmail(String email);
    
}
