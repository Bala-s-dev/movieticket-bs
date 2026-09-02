package com.movieticket.repository.local;

import com.movieticket.model.ShowSeat;
import com.movieticket.repository.ShowSeatRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class LocalShowSeatRepository implements ShowSeatRepository {

    // key = showId, value = (seatId -> ShowSeat)
    private final Map<Long, Map<Long, ShowSeat>> showSeats = new HashMap<>();

    @Override
    public ShowSeat save(ShowSeat showSeat) {
 
        long showId = showSeat.getShowId();
        long seatId = showSeat.getSeatId();

        Map<Long, ShowSeat> forShow = showSeats.get(showId);

        if (forShow == null) {
            forShow = new HashMap<>();
            showSeats.put(showId, forShow);
        }

        forShow.put(seatId, showSeat);
        return showSeat;
    }

    @Override
    public Optional<ShowSeat> findByShowIdAndSeatId(long showId, long seatId) {

        Map<Long, ShowSeat> forShow = showSeats.get(showId);

        if (forShow == null)
            return Optional.empty();

        return Optional.ofNullable(forShow.get(seatId));
    }

    @Override
    public List<ShowSeat> findByShowId(long showId) {

        Map<Long, ShowSeat> forShow = showSeats.get(showId);

        if (forShow == null)
            return new ArrayList<>();

        return forShow.values().stream().toList();
    }

    @Override
    public void initializeForShow(long showId, List<Long> seatIds) {

        Map<Long, ShowSeat> forShow = showSeats.get(showId);

        if (forShow == null) {
            forShow = new HashMap<>();
            showSeats.put(showId, forShow);
        }
        
        for (Long seatId : seatIds) {
            
            if (!forShow.containsKey(seatId)) {
                forShow.put(seatId, new ShowSeat(showId, seatId));
            }
            
        }
    }
}
