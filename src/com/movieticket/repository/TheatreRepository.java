package com.movieticket.repository;

import com.movieticket.model.Theatre;

import java.util.List;
import java.util.Optional;

public interface TheatreRepository {
    Theatre save(Theatre theatre);
    Optional<Theatre> findById(long id);
    List<Theatre> findAll();
    List<Theatre> findByAdminId(long adminId);
    void deleteById(long id);
}
