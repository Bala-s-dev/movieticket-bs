package com.movieticket.repository;

import com.movieticket.model.Booking;

import java.util.List;
import java.util.Optional;

public interface BookingRepository {
    Booking save(Booking booking);
    Optional<Booking> findById(long id);
    List<Booking> findAll();
    List<Booking> findByUserId(long userId);
    List<Booking> findByShowId(long showId);
}
