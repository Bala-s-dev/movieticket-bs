package com.movieticket.repository.local;

import com.movieticket.model.Booking;
import com.movieticket.repository.BookingRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class LocalBookingRepository implements BookingRepository {

    private final Map<Long, Booking> bookings = new HashMap<>();

    @Override
    public Booking save(Booking booking) {
        bookings.put(booking.getBookingId(), booking);
        return booking;
    }

    @Override
    public Optional<Booking> findById(long id) {
        return Optional.ofNullable(bookings.get(id));
    }

    @Override
    public List<Booking> findAll() {
        return new ArrayList<>(bookings.values());
    }

    @Override
    public List<Booking> findByUserId(long userId) {
        return bookings.values().stream()
                .filter(b -> b.getUserId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findByShowId(long showId) {
        return bookings.values().stream()
                .filter(b -> b.getShowId() == showId)
                .collect(Collectors.toList());
    }
}
