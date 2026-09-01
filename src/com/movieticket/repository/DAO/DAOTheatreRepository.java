package com.movieticket.repository.DAO;

import com.movieticket.model.Theatre;
import com.movieticket.repository.TheatreRepository;
import com.movieticket.util.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DAOTheatreRepository implements TheatreRepository {

    @Override
    public Theatre save(Theatre theatre) {
        String sql = "INSERT INTO theatres (theatre_id, name, location, admin_id, active) VALUES (?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE name = VALUES(name), location = VALUES(location), active = VALUES(active)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, theatre.getTheatreId());
            ps.setString(2, theatre.getName());
            ps.setString(3, theatre.getLocation());
            ps.setLong(4, theatre.getAdminId());
            ps.setBoolean(5, theatre.isActive());
            ps.executeUpdate();
            return theatre;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save theatre: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Theatre> findById(long id) {
        String sql = "SELECT * FROM theatres WHERE theatre_id = ?";
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
            throw new RuntimeException("Failed to fetch theatre: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Theatre> findAll() {
        String sql = "SELECT * FROM theatres";
        List<Theatre> theatres = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                theatres.add(mapRow(rs));
            }
            return theatres;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch theatres: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Theatre> findByAdminId(long adminId) {
        String sql = "SELECT * FROM theatres WHERE admin_id = ?";
        List<Theatre> theatres = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, adminId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    theatres.add(mapRow(rs));
                }
            }
            return theatres;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch theatres for admin: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteById(long id) {
        String sql = "DELETE FROM theatres WHERE theatre_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete theatre: " + e.getMessage(), e);
        }
    }

    private Theatre mapRow(ResultSet rs) throws SQLException {
        Theatre theatre = new Theatre(
                rs.getLong("theatre_id"),
                rs.getString("name"),
                rs.getString("location"),
                rs.getLong("admin_id")
        );
        theatre.setActive(rs.getBoolean("active"));
        return theatre;
    }
}
