package com.movieticket.repository.DAO;

import com.movieticket.enums.SeatCategory;
import com.movieticket.model.Screen;
import com.movieticket.model.Seat;
import com.movieticket.repository.ScreenRepository;
import com.movieticket.util.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DAOScreenRepository implements ScreenRepository {

    @Override
    public Screen save(Screen screen) {
        String screenSql = "INSERT INTO screens (screen_id, screen_name, theatre_id, active) VALUES (?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE screen_name = VALUES(screen_name), active = VALUES(active)";
        String seatSql = "INSERT IGNORE INTO seats (seat_id, screen_id, row_letter, seat_number, category, row_order) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conn.prepareStatement(screenSql)) {
                    ps.setLong(1, screen.getScreenId());
                    ps.setString(2, screen.getScreenName());
                    ps.setLong(3, screen.getTheatreId());
                    ps.setBoolean(4, screen.isActive());
                    ps.executeUpdate();
                }

                int rowOrder = 0;
                try (PreparedStatement ps = conn.prepareStatement(seatSql)) {
                    for (Map.Entry<Character, List<Seat>> entry : screen.getSeatLayout().entrySet()) {
                        for (Seat seat : entry.getValue()) {
                            ps.setLong(1, seat.getSeatId());
                            ps.setLong(2, seat.getScreenId());
                            ps.setString(3, String.valueOf(seat.getRow()));
                            ps.setInt(4, seat.getSeatNumber());
                            ps.setString(5, seat.getCategory().name());
                            ps.setInt(6, rowOrder);
                            ps.addBatch();
                        }
                        rowOrder++;
                    }
                    ps.executeBatch();
                }

                conn.commit();
                return screen;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save screen: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Screen> findById(long id) {
        String screenSql = "SELECT * FROM screens WHERE screen_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(screenSql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Screen screen = mapScreenRow(rs);
                    loadSeats(conn, screen);
                    return Optional.of(screen);
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch screen: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Screen> findAll() {
        String sql = "SELECT * FROM screens";
        List<Screen> screens = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                screens.add(mapScreenRow(rs));
            }
            for (Screen screen : screens) {
                loadSeats(conn, screen);
            }
            return screens;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch screens: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Screen> findByTheatreId(long theatreId) {
        String sql = "SELECT * FROM screens WHERE theatre_id = ?";
        List<Screen> screens = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, theatreId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    screens.add(mapScreenRow(rs));
                }
            }
            for (Screen screen : screens) {
                loadSeats(conn, screen);
            }
            return screens;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch screens for theatre: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteById(long id) {
        String sql = "DELETE FROM screens WHERE screen_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete screen: " + e.getMessage(), e);
        }
    }

    private Screen mapScreenRow(ResultSet rs) throws SQLException {
        Screen screen = new Screen(
                rs.getLong("screen_id"),
                rs.getString("screen_name"),
                rs.getLong("theatre_id")
        );
        screen.setActive(rs.getBoolean("active"));
        return screen;
    }

    /** Loads seats ordered by row_order then seat_number, and reconstructs the row layout in original order. */
    private void loadSeats(Connection conn, Screen screen) throws SQLException {
        String sql = "SELECT * FROM seats WHERE screen_id = ? ORDER BY row_order ASC, seat_number ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, screen.getScreenId());
            try (ResultSet rs = ps.executeQuery()) {
                Map<Character, List<Seat>> rows = new LinkedHashMap<>();
                while (rs.next()) {
                    char row = rs.getString("row_letter").charAt(0);
                    Seat seat = new Seat(
                            rs.getLong("seat_id"),
                            rs.getLong("screen_id"),
                            row,
                            rs.getInt("seat_number"),
                            SeatCategory.valueOf(rs.getString("category"))
                    );
                    rows.computeIfAbsent(row, k -> new ArrayList<>()).add(seat);
                }
                for (Map.Entry<Character, List<Seat>> entry : rows.entrySet()) {
                    screen.addRow(entry.getKey(), entry.getValue());
                }
            }
        }
    }
}
