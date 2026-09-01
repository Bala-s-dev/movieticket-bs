package com.movieticket.repository.DAO;

import com.movieticket.enums.SeatStatus;
import com.movieticket.model.ShowSeat;
import com.movieticket.repository.ShowSeatRepository;
import com.movieticket.util.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DAOShowSeatRepository implements ShowSeatRepository {

    @Override
    public ShowSeat save(ShowSeat showSeat) {
        String sql = "INSERT INTO show_seats (show_id, seat_id, status) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE status = VALUES(status)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, showSeat.getShowId());
            ps.setLong(2, showSeat.getSeatId());
            ps.setString(3, showSeat.getStatus().name());
            ps.executeUpdate();
            return showSeat;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save show seat state: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<ShowSeat> findByShowIdAndSeatId(long showId, long seatId) {
        String sql = "SELECT * FROM show_seats WHERE show_id = ? AND seat_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, showId);
            ps.setLong(2, seatId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch show seat state: " + e.getMessage(), e);
        }
    }

    @Override
    public List<ShowSeat> findByShowId(long showId) {
        String sql = "SELECT * FROM show_seats WHERE show_id = ?";
        List<ShowSeat> result = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, showId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch show seat states: " + e.getMessage(), e);
        }
    }

    @Override
    public void initializeForShow(long showId, List<Long> seatIds) {
        String sql = "INSERT IGNORE INTO show_seats (show_id, seat_id, status) VALUES (?, ?, 'AVAILABLE')";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Long seatId : seatIds) {
                ps.setLong(1, showId);
                ps.setLong(2, seatId);
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize show seats: " + e.getMessage(), e);
        }
    }

    private ShowSeat mapRow(ResultSet rs) throws SQLException {
        ShowSeat showSeat = new ShowSeat(rs.getLong("show_id"), rs.getLong("seat_id"));
        if (SeatStatus.valueOf(rs.getString("status")) == SeatStatus.BOOKED) {
            showSeat.markBooked();
        }
        return showSeat;
    }
}
