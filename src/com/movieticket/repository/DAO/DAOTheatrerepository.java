package com.movieticket.repository.DAO;

import com.movieticket.model.Theatre;
import com.movieticket.repository.TheatreRepository;
import com.movieticket.util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DAOTheatrerepository implements  TheatreRepository{
    private static final String TABLE_NAME = "theatres";
    
    @Override
    public Theatre save(Theatre theatre) {
        String sql = "INSERT INTO " + TABLE_NAME + " (name, location, admin_id, is_active) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, theatre.getName());
            pstmt.setString(2, theatre.getLocation());
            pstmt.setLong(3, theatre.getAdminId());
            pstmt.setBoolean(4, theatre.isActive());
            pstmt.executeUpdate();  

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    theatre.setTheatreId(generatedKeys.getLong(1));
                }
            }
            return theatre;

        } catch (SQLException e) {
            throw new RuntimeException("Error saving theatre: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Theatre> findById(long id) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToTheatre(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding theatre by ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Theatre> findAll() {
        List<Theatre> theatres = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NAME;
        try (Connection conn = DatabaseManager.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                theatres.add(mapResultSetToTheatre(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all theatres: " + e.getMessage(), e);
        }
        return theatres;
    }

    @Override
    public List<Theatre> findByAdminId(long adminId) {
        List<Theatre> theatres = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE admin_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, adminId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    theatres.add(mapResultSetToTheatre(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding theatres by admin ID: " + e.getMessage(), e);
        }
        return theatres;
    }

    @Override
    public void deleteById(long id) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting theatre: " + e.getMessage(), e);
        }
    }

    private Theatre mapResultSetToTheatre(ResultSet rs) throws SQLException {
        return new Theatre(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("location"),
            rs.getLong("admin_id"),
            rs.getBoolean("is_active")
        );
    }
}
