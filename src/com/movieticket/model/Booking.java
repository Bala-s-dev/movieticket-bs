package com.movieticket.model;

import com.movieticket.enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class Booking {
    
    private final long bookingId;
    private final long userId;
    private final long showId;
    private final LocalDateTime bookingDateTime;
    private final List<Long> seatIds; 
    private final double totalAmount;
    private BookingStatus status;

    public Booking(long bookingId, long userId, long showId, LocalDateTime bookingDateTime,
                   List<Long> seatIds, double totalAmount) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.showId = showId;
        this.bookingDateTime = bookingDateTime;
        this.seatIds = seatIds;
        this.totalAmount = totalAmount;
        this.status = BookingStatus.CONFIRMED;
    }

    public long getBookingId() { 
        return bookingId; 
    }
    
    public long getUserId() { 
        return userId; 
    }

    public long getShowId() { 
        return showId; 
    }

    public LocalDateTime getBookingDateTime() { 
        return bookingDateTime; 
    }

    public List<Long> getSeatIds() { 
        return Collections.unmodifiableList(seatIds); 
    }

    public double getTotalAmount() { 
        return totalAmount; 
    }

    public BookingStatus getStatus() { 
        return status; 
    }

    public void cancel() { 
        this.status = BookingStatus.CANCELLED; 
    }

    @Override
    public String toString() {
        return "Booking{id=" + bookingId + ", userId=" + userId + ", showId=" + showId +
                ", status=" + status + ", total=" + totalAmount + "}";
    }
}
