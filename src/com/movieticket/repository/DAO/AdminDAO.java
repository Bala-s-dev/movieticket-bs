package com.movieticket.repository.DAO;

import java.util.*;

import com.movieticket.model.Admin;
import com.movieticket.repository.AdminRepository;

class AdminDAO implements AdminRepository{
    
    @Override
    public Optional<Admin> findById(long id) {
        
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }

    @Override
    public List<Admin> findAll() {
        
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }

    @Override
    public boolean existsByEmail(String email) {
        
        throw new UnsupportedOperationException("Unimplemented method 'existsByEmail'");
    }

    @Override
    public Admin save(Admin admin) {
        
        String sql = "INSERT INTO admins(name,email,password) VALUES(?,?,?)";
        return admin;

        // try{
        //     Object connection = getConnection();
        //     PreparedStatement ps = connection.prepareStatement(sql);
        //     ps.setString(1, admin.getName());
        //     ps.setString(2, admin.getEmail());
        //     ps.setString(3, admin.getPassword());
        //     ps.executeUpdate();
        //     return admin;
        // }   catch (Exception e) {
        //     System.out.println(e.getMessage());
        //     return admin;
        // }
    }

    @Override
    public Optional<Admin> findByEmail(String email) {
        
        throw new UnsupportedOperationException("Unimplemented method 'findByEmail'");
    }
}