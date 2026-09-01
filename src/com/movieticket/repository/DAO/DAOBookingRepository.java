package com.movieticket.repository.DAO;

import com.movieticket.enums.BookingStatus;
import com.movieticket.model.Booking;
import com.movieticket.repository.BookingRepository;
import com.movieticket.util.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DAOBookingRepository implements BookingRepository {

    @Override
    public Booking save(Booking booking) {
        String bookingSql = "INSERT INTO bookings (booking_id, user_id, show_id, booking_datetime, " +
                "total_amount, status) VALUES (?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE status = VALUES(status), total_amount = VALUES(total_amount)";
        String deleteSeatsSql = "DELETE FROM booking_seats WHERE booking_id = ?";
        String insertSeatSql = "INSERT IGNORE INTO booking_seats (booking_id, seat_id) VALUES (?, ?)";

        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conn.prepareStatement(bookingSql)) {
                    ps.setLong(1, booking.getBookingId());
                    ps.setLong(2, booking.getUserId());
                    ps.setLong(3, booking.getShowId());
                    ps.setTimestamp(4, Timestamp.valueOf(booking.getBookingDateTime()));
                    ps.setDouble(5, booking.getTotalAmount());
                    ps.setString(6, booking.getStatus().name());
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = conn.prepareStatement(deleteSeatsSql)) {
                    ps.setLong(1, booking.getBookingId());
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = conn.prepareStatement(insertSeatSql)) {
                    for (Long seatId : booking.getSeatIds()) {
                        ps.setLong(1, booking.getBookingId());
                        ps.setLong(2, seatId);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }

                conn.commit();
                return booking;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save booking: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Booking> findById(long id) {
        String sql = "SELECT * FROM bookings WHERE booking_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(conn, rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch booking: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Booking> findAll() {
        String sql = "SELECT * FROM bookings";
        List<Booking> bookings = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                bookings.add(mapRow(conn, rs));
            }
            return bookings;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch bookings: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Booking> findByUserId(long userId) {
        String sql = "SELECT * FROM bookings WHERE user_id = ?";
        List<Booking> bookings = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    bookings.add(mapRow(conn, rs));
                }
            }
            return bookings;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch bookings for user: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Booking> findByShowId(long showId) {
        String sql = "SELECT * FROM bookings WHERE show_id = ?";
        List<Booking> bookings = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, showId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    bookings.add(mapRow(conn, rs));
                }
            }
            return bookings;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch bookings for show: " + e.getMessage(), e);
        }
    }

    private Booking mapRow(Connection conn, ResultSet rs) throws SQLException {
        long bookingId = rs.getLong("booking_id");
        List<Long> seatIds = new ArrayList<>();
        String seatSql = "SELECT seat_id FROM booking_seats WHERE booking_id = ?";
        try (PreparedStatement seatPs = conn.prepareStatement(seatSql)) {
            seatPs.setLong(1, bookingId);
            try (ResultSet seatRs = seatPs.executeQuery()) {
                while (seatRs.next()) {
                    seatIds.add(seatRs.getLong("seat_id"));
                }
            }
        }

        Booking booking = new Booking(
                bookingId,
                rs.getLong("user_id"),
                rs.getLong("show_id"),
                rs.getTimestamp("booking_datetime").toLocalDateTime(),
                seatIds,
                rs.getDouble("total_amount")
        );
        if (BookingStatus.valueOf(rs.getString("status")) == BookingStatus.CANCELLED) {
            booking.cancel();
        }
        return booking;
    }
}
