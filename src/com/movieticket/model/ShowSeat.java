package com.movieticket.model;

import com.movieticket.enums.SeatStatus;

public class ShowSeat {

    private final long showId;
    private final long seatId;
    private SeatStatus status;

    public ShowSeat(long showId, long seatId) {
        this.showId = showId;
        this.seatId = seatId;
        this.status = SeatStatus.AVAILABLE;
    }

    public long getShowId() { 
        return showId; 
    }

    public long getSeatId() { 
        return seatId; 
    }

    public SeatStatus getStatus() { 
        return status; 
    }

    public void markBooked() { 
        this.status = SeatStatus.BOOKED; 
    }

    public void markAvailable() { 
        this.status = SeatStatus.AVAILABLE; 
    }

    public boolean isAvailable() { 
        return status == SeatStatus.AVAILABLE; 
    }
    
}
