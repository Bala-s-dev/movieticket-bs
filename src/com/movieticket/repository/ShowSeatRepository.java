package com.movieticket.repository;

import com.movieticket.model.ShowSeat;

import java.util.List;
import java.util.Optional;

public interface ShowSeatRepository {
    ShowSeat save(ShowSeat showSeat);
    Optional<ShowSeat> findByShowIdAndSeatId(long showId, long seatId);
    List<ShowSeat> findByShowId(long showId);
    void initializeForShow(long showId, List<Long> seatIds);
}
