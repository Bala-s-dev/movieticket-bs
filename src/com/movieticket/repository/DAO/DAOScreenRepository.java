package com.movieticket.repository.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.movieticket.model.Screen;
import com.movieticket.util.DatabaseManager;
import com.movieticket.repository.ScreenRepository;

public class DAOScreenRepository implements ScreenRepository {
    
    @Override
    public Screen save(Screen screen) {
        String sql = "INSERT INTO screens (screen_name, theatre_id) " +
                "VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "screen_name = VALUES(screen_name), " +
                "theatre_id = VALUES(theatre_id)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, screen.getScreenName());
            ps.setLong(2, screen.getTheatreId());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    screen.setScreenId(rs.getLong(1));
                }
            }
            return screen;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save screen: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Screen> findById(long id) {
        String sql = "SELECT id, screen_name, theatre_id FROM screens WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Screen screen = new Screen();
                    screen.setScreenId(rs.getLong(1));
                    screen.setScreenName(rs.getString(2));
                    screen.setTheatreId(rs.getLong(3));
                    return Optional.of(screen);
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find screen: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Screen> findAll() {
        String sql = "SELECT * FROM screens";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                List<Screen> screens = new ArrayList<>();
                while (rs.next()) {
                    Screen screen = new Screen();
                    screen.setScreenId(rs.getLong(1));
                    screen.setScreenName(rs.getString(2));
                    screen.setTheatreId(rs.getLong(3));
                    screens.add(screen);
                }
                return screens;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find screens: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Screen> findByTheatreId(long theatreId) {
        String sql = "SELECT * FROM screens WHERE theatre_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, theatreId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Screen> screens = new ArrayList<>();
                while (rs.next()) {
                    Screen screen = new Screen();
                    screen.setScreenId(rs.getLong(1));
                    screen.setScreenName(rs.getString(2));
                    screen.setTheatreId(rs.getLong(3));
                    screens.add(screen);
                }
                return screens;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find screens: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteById(long id) {
        String sql = "DELETE FROM screens WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete screen: " + e.getMessage(), e);
        }
    }
    
}
