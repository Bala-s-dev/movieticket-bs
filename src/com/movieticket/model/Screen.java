package com.movieticket.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Screen {
    private long id;
    private String screenName;
    private long theatreId;
    private final Map<Character, List<Seat>> seatLayout = new LinkedHashMap<>();
    private boolean active;

    
    public Screen(long screenId, String screenName, long theatreId) {
        this.id = screenId;
        this.screenName = screenName;
        this.theatreId = theatreId;
        this.active = true;
    }

    public Screen(){

    }

    public long getScreenId() { 
        return id; 
    }

    public void setScreenId(Long id){
        this.id = id;
    }

    public String getScreenName() { 
        return screenName; 
    }
    
    public long getTheatreId() { 
        return theatreId; 
    }

    public void setTheatreId(long theatreId){
        this.theatreId = theatreId;
    }
    
    public boolean isActive() { 
        return active; 
    }

    public void setScreenName(String screenName) { 
        this.screenName = screenName; 
    }
    
    public void setActive(boolean active) { 
        this.active = active; 
    }

    public void addRow(char row, List<Seat> seats) {
        seatLayout.put(row, new ArrayList<>(seats));
    }

    public Map<Character, List<Seat>> getSeatLayout() {
        return Collections.unmodifiableMap(seatLayout);
    }

    public List<Seat> getAllSeats() {

        List<Seat> allSeats = new ArrayList<>();

        for (List<Seat> rowSeats : seatLayout.values()) {
            allSeats.addAll(rowSeats);
        }

        return allSeats;
    }

    public int getTotalSeatCount() {
        return getAllSeats().size();
    }

    @Override
    public String toString() {
        return "Screen{id=" + id + ", name=" + screenName + ", rows=" + seatLayout.size() + "}";
    }
}
