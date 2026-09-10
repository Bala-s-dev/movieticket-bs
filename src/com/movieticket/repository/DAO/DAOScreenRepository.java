package com.movieticket.repository.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.movieticket.enums.SeatCategory;
import com.movieticket.model.Screen;
import com.movieticket.model.Seat;
import com.movieticket.util.DatabaseManager;
import com.movieticket.repository.ScreenRepository;

public class DAOScreenRepository implements ScreenRepository {

    @Override
    public Screen save(Screen screen) {
        try (Connection conn = DatabaseManager.getConnection()) {

            if (screen.getScreenId() == 0) {
                insertScreen(conn, screen);
            } else {
                updateScreen(conn, screen);
            }

            saveSeats(conn, screen);

            return screen;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save screen: " + e.getMessage(), e);
        }
    }

    private void insertScreen(Connection conn, Screen screen) throws SQLException {
        String sql = "INSERT INTO screens (screen_name, theatre_id, active) VALUES (?, ?, ?)";
        
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, screen.getScreenName());
            ps.setLong(2, screen.getTheatreId());
            ps.setBoolean(3, screen.isActive());
            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    screen.setScreenId(generatedKeys.getLong(1));
                }
            }
        }
    }

    private void updateScreen(Connection conn, Screen screen) throws SQLException {
        String sql = "UPDATE screens SET screen_name = ?, theatre_id = ?, active = ? WHERE id = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, screen.getScreenName());
            ps.setLong(2, screen.getTheatreId());
            ps.setBoolean(3, screen.isActive());
            ps.setLong(4, screen.getScreenId());
            ps.executeUpdate();
        }
    }

    private void saveSeats(Connection conn, Screen screen) throws SQLException {
        String updateSql = "UPDATE seats SET row_letter = ?, seat_number = ?, category = ?, row_order = ? WHERE id = ?";
        String insertSql = "INSERT INTO seats (screen_id, row_letter, seat_number, category, row_order) " +
                "VALUES (?, ?, ?, ?, ?)";

        List<Seat> newSeats = new ArrayList<>();
        List<Integer> newSeatRowOrders = new ArrayList<>();

        try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
            int rowOrder = 0;
            boolean hasUpdates = false;
            
            for (Map.Entry<Character, List<Seat>> rowEntry : screen.getSeatLayout().entrySet()) {
                
                for (Seat seat : rowEntry.getValue()) {
                    
                    if (seat.getSeatId() == 0) {
                        newSeats.add(seat);
                        newSeatRowOrders.add(rowOrder);
                        continue;
                    }
                    updatePs.setString(1, String.valueOf(seat.getRow()));
                    updatePs.setInt(2, seat.getSeatNumber());
                    updatePs.setString(3, seat.getCategory().name());
                    updatePs.setInt(4, rowOrder);
                    updatePs.setLong(5, seat.getSeatId());
                    updatePs.addBatch();
                    hasUpdates = true;
                }
                rowOrder++;
            }

            if (hasUpdates) {
                updatePs.executeBatch();
            }
        }

        if (newSeats.isEmpty()) {
            return;
        }

        try (PreparedStatement insertPs = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < newSeats.size(); i++) {
                Seat seat = newSeats.get(i);
                insertPs.setLong(1, screen.getScreenId());
                insertPs.setString(2, String.valueOf(seat.getRow()));
                insertPs.setInt(3, seat.getSeatNumber());
                insertPs.setString(4, seat.getCategory().name());
                insertPs.setInt(5, newSeatRowOrders.get(i));
                insertPs.addBatch();
            }
            insertPs.executeBatch();

            try (ResultSet generatedKeys = insertPs.getGeneratedKeys()) {
                int i = 0;
                
                while (generatedKeys.next() && i < newSeats.size()) {
                    newSeats.get(i).setSeatId(generatedKeys.getLong(1));
                    i++;
                }
            }
        }
    }

    @Override
    public Optional<Screen> findById(long id) {
        String sql = "SELECT id, screen_name, theatre_id, active FROM screens WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Screen screen = mapRow(rs);
                    loadSeats(conn, screen);
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
        String sql = "SELECT id, screen_name, theatre_id, active FROM screens";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Screen> screens = new ArrayList<>();
            
            while (rs.next()) {
                Screen screen = mapRow(rs);
                loadSeats(conn, screen);
                screens.add(screen);
            }
            return screens;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find screens: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Screen> findByTheatreId(long theatreId) {
        String sql = "SELECT id, screen_name, theatre_id, active FROM screens WHERE theatre_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, theatreId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Screen> screens = new ArrayList<>();
                while (rs.next()) {
                    Screen screen = mapRow(rs);
                    loadSeats(conn, screen);
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
        try (Connection conn = DatabaseManager.getConnection()) {
            
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM seats WHERE screen_id = ?")) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }
            
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM screens WHERE id = ?")) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete screen: " + e.getMessage(), e);
        }
    }

    private Screen mapRow(ResultSet rs) throws SQLException {
        Screen screen = new Screen(rs.getLong("id"), rs.getString("screen_name"), rs.getLong("theatre_id"));
        screen.setActive(rs.getBoolean("active"));
        return screen;
    }

    private void loadSeats(Connection conn, Screen screen) throws SQLException {
        String sql = "SELECT id, screen_id, row_letter, seat_number, category, row_order " +
                "FROM seats WHERE screen_id = ? ORDER BY row_order ASC, seat_number ASC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, screen.getScreenId());
            
            try (ResultSet rs = ps.executeQuery()) {
                Map<Character, List<Seat>> grouped = new LinkedHashMap<>();
                
                while (rs.next()) {
                    char row = rs.getString("row_letter").charAt(0);
                    Seat seat = new Seat(
                            rs.getLong("id"),
                            rs.getLong("screen_id"),
                            row,
                            rs.getInt("seat_number"),
                            SeatCategory.valueOf(rs.getString("category"))
                    );
                    grouped.computeIfAbsent(row, k -> new ArrayList<>()).add(seat);
                }
                
                for (Map.Entry<Character, List<Seat>> entry : grouped.entrySet()) {
                    screen.addRow(entry.getKey(), entry.getValue());
                }
            }
        }
    }
}
