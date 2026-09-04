package com.movieticket.model;

import java.time.LocalDateTime;

public class Show {
    
    private final long id;
    private final long movieId;
    private final long screenId;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private final TicketPricing pricing;
    private boolean active;

    public Show(long showId, long movieId, long screenId, LocalDateTime startDateTime,
                LocalDateTime endDateTime, TicketPricing pricing) {
        this.id = showId;
        this.movieId = movieId;
        this.screenId = screenId;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.pricing = pricing;
        this.active = true;
    }

    public long getShowId() { 
        return id; 
    }
    
    public long getMovieId() { 
        return movieId; 
    }

    public long getScreenId() { 
        return screenId; 
    }

    public LocalDateTime getStartDateTime() { 
        return startDateTime; 
    }

    public LocalDateTime getEndDateTime() { 
        return endDateTime; 
    }

    public TicketPricing getPricing() { 
        return pricing; 
    }

    public boolean isActive() { 
        return active; 
    }

    public void setActive(boolean active) { 
        this.active = active; 
    }

    public boolean overlapsWith(LocalDateTime otherStart, LocalDateTime otherEnd) {
        return startDateTime.isBefore(otherEnd) && otherStart.isBefore(endDateTime);
    }

    public boolean isUpcomingOrOngoing(LocalDateTime reference) {
        return !startDateTime.isBefore(reference);
    }

    @Override
    public String toString() {
        return "Show{id=" + id + ", movieId=" + movieId + ", screenId=" + screenId +
                ", start=" + startDateTime + "}";
    }
}
