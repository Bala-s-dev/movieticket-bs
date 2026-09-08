package com.movieticket.model;

public class Theatre {

    private long id;
    private String name;
    private String location;
    private long adminId;
    private boolean active;

    public Theatre(long theatreId, String name, String location, long adminId, boolean active) {
        this.id = theatreId;
        this.name = name;
        this.location = location;
        this.adminId = adminId;
        this.active = active;
    }

    public Theatre(String name, String location, long adminId, boolean active) {
        this.id = 0;
        this.name = name;
        this.location = location;
        this.adminId = adminId;
        this.active = active;
    }

    public long getTheatreId() { 
        return id; 
    }

    public void setTheatreId(long theatreId){
        this.id = theatreId;
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

    public void setAdminId(long adminId){
        this.adminId = adminId;
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
