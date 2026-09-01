package com.movieticket.model;

import java.util.Objects;

public class Admin {
    private final long adminId;
    private String name;
    private String email;
    private String phone;
    private String password;

    public Admin(long adminId, String name, String email, String phone, String password) {
        this.adminId = adminId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }

    public long getAdminId(){ 
        return adminId; 
    }

    public String getName() { 
        return name; 
    }

    public String getEmail() { 
        return email; 
    }
    
    public String getPhone() { return phone; }
    public String getPassword() { return password; }

    public void setName(String name) { this.name = name; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setPassword(String password) { this.password = password; }

    public boolean checkPassword(String rawPassword) {
        return Objects.equals(this.password, rawPassword);
    }

    @Override
    public String toString() {
        return "Admin{id=" + adminId + ", name=" + name + ", email=" + email + "}";
    }
}
