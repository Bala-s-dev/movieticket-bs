package com.movieticket.model;

import com.movieticket.enums.SeatCategory;

public class Seat {

    private final long seatId;
    private final long screenId;
    private final char row;
    private final int seatNumber;
    private final SeatCategory category;

    public Seat(long seatId, long screenId, char row, int seatNumber, SeatCategory category) {
        this.seatId = seatId;
        this.screenId = screenId;
        this.row = row;
        this.seatNumber = seatNumber;
        this.category = category;
    }

    public long getSeatId() { 
        return seatId; 
    }

    public long getScreenId() { 
        return screenId; 
    }

    public char getRow() { 
        return row; 
    }

    public int getSeatNumber() { 
        return seatNumber; 
    }
    
    public SeatCategory getCategory() { 
        return category; 
    }

    public String getLabel() {
        return "" + row + seatNumber;
    }

    @Override
    public String toString() {
        return getLabel() + "(" + category + ")";
    }
}
