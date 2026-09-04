package com.movieticket.model;

public class Theatre {

    private final long id;
    private String name;
    private String location;
    private final long adminId;
    private boolean active;

    public Theatre(long theatreId, String name, String location, long adminId) {
        this.id = theatreId;
        this.name = name;
        this.location = location;
        this.adminId = adminId;
        this.active = true;
    }

    public long getTheatreId() { 
        return id; 
    }

    public String getName() { 
        return name; 
    }

    public String getLocation() { 
        return location; 
    }

    public long getAdminId() { 
        return adminId; 
    }
    
    public boolean isActive() { 
        return active; 
    }

    public void setName(String name) { 
        this.name = name; 
    }

    public void setLocation(String location) { 
        this.location = location; 
    }

    public void setActive(boolean active) { 
        this.active = active; 
    }

    @Override
    public String toString() {
        return "Theatre{id=" + id + ", name=" + name + ", location=" + location + "}";
    }
}
