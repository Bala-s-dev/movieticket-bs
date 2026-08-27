package com.movieticket.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Screen {
    private final long screenId;
    private String screenName;
    private final long theatreId;
    private final Map<Character, List<Seat>> seatLayout = new LinkedHashMap<>();
    private boolean active;

    public Screen(long screenId, String screenName, long theatreId) {
        this.screenId = screenId;
        this.screenName = screenName;
        this.theatreId = theatreId;
        this.active = true;
    }

    public long getScreenId() { return screenId; }
    public String getScreenName() { return screenName; }
    public long getTheatreId() { return theatreId; }
    public boolean isActive() { return active; }

    public void setScreenName(String screenName) { this.screenName = screenName; }
    public void setActive(boolean active) { this.active = active; }

    public void addRow(char row, List<Seat> seats) {
        seatLayout.put(row, new ArrayList<>(seats));
    }

    public Map<Character, List<Seat>> getSeatLayout() {
        return Collections.unmodifiableMap(seatLayout);
    }

    public List<Seat> getAllSeats() {
        List<Seat> all = new ArrayList<>();
        for (List<Seat> rowSeats : seatLayout.values()) {
            all.addAll(rowSeats);
        }
        return all;
    }

    public int getTotalSeatCount() {
        return getAllSeats().size();
    }

    @Override
    public String toString() {
        return "Screen{id=" + screenId + ", name=" + screenName + ", rows=" + seatLayout.size() + "}";
    }
}
