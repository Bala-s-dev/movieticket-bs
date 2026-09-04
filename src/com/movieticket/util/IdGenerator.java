package com.movieticket.util;

import java.util.concurrent.atomic.AtomicLong;

public final class IdGenerator {

    private static final AtomicLong userId = new AtomicLong(1000);
    private static final AtomicLong adminId = new AtomicLong(2000);
    private static final AtomicLong movieId = new AtomicLong(3000);
    private static final AtomicLong theatreId = new AtomicLong(4000);
    private static final AtomicLong screenId = new AtomicLong(5000);
    private static final AtomicLong seatId = new AtomicLong(1);
    private static final AtomicLong showId = new AtomicLong(6000);
    private static final AtomicLong bookingId = new AtomicLong(7000);

    private IdGenerator() { }

    public static long nextUserId() {
        return userId.incrementAndGet(); 
    }

    public static long nextAdminId() { 
        return adminId.incrementAndGet(); 
    }

    public static long nextMovieId() { 
        return movieId.incrementAndGet(); 
    }

    public static long nextTheatreId() { 
        return theatreId.incrementAndGet(); 
    }

    public static long nextScreenId() { 
        return screenId.incrementAndGet(); 
    }

    public static long nextSeatId() { 
        return seatId.incrementAndGet(); 
    }

    public static long nextShowId() { 
        return showId.incrementAndGet(); 
    }

    public static long nextBookingId() { 
        return bookingId.incrementAndGet(); 
    }
}
