package com.movieticket.repository.DAO;

import com.movieticket.model.Admin;
import com.movieticket.repository.AdminRepository;
import com.movieticket.util.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DAOAdminRepository implements AdminRepository {

    @Override
    public Admin save(Admin admin) {
        String sql = "INSERT INTO admins (admin_id, name, email, phone, password) VALUES (?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE name = VALUES(name), phone = VALUES(phone), password = VALUES(password)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, admin.getAdminId());
            ps.setString(2, admin.getName());
            ps.setString(3, admin.getEmail());
            ps.setString(4, admin.getPhone());
            ps.setString(5, admin.getPassword());
            ps.executeUpdate();
            return admin;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save admin: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Admin> findById(long id) {
        String sql = "SELECT * FROM admins WHERE admin_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch admin: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Admin> findByEmail(String email) {
        String sql = "SELECT * FROM admins WHERE email = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch admin by email: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Admin> findAll() {
        String sql = "SELECT * FROM admins";
        List<Admin> admins = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                admins.add(mapRow(rs));
            }
            return admins;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch admins: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }

    private Admin mapRow(ResultSet rs) throws SQLException {
        return new Admin(
                rs.getLong("admin_id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getString("password")
        );
    }
}
