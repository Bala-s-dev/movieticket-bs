package com.movieticket.model;

import java.util.Objects;

public class User {

    private final long id;
    private String name;
    private String email;
    private String phone;
    private String password; 

    public User(long userId, String name, String email, String phone, String password) {
        this.id = userId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }

    public long getUserId() { 
        return id; 
    }

    public String getName() { 
        return name; 
    }

    public String getEmail() { 
        return email; 
    }

    public String getPhone() { 
        return phone; 
    }

    public String getPassword() { 
        return password; 
    }

    public void setName(String name) { 
        this.name = name; 
    }
    
    public void setPhone(String phone) { 
        this.phone = phone; 
    }   

    public void setPassword(String password) { 
        this.password = password; 
    }

    public boolean checkPassword(String rawPassword) {
        return Objects.equals(this.password, rawPassword);
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", name=" + name + ", email=" + email + "}";
    }
}
