package com.movieticket.repository.DAO;

import com.movieticket.enums.BookingStatus;
import com.movieticket.model.Booking;
import com.movieticket.repository.BookingRepository;
import com.movieticket.util.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DAOBookingRepository implements BookingRepository {

    @Override
    public Booking save(Booking booking) {
        if (booking.getBookingId() == 0) {
            return insert(booking);
        }
        return updateStatus(booking);
    }

    private Booking insert(Booking booking) {
        String sql = "INSERT INTO bookings (user_id, show_id, booking_datetime, total_amount, status) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, booking.getUserId());
            ps.setLong(2, booking.getShowId());
            ps.setTimestamp(3, Timestamp.valueOf(booking.getBookingDateTime()));
            ps.setDouble(4, booking.getTotalAmount());
            ps.setString(5, booking.getStatus().name());
            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    booking.setBookingId(generatedKeys.getLong(1));
                }
            }

            saveBookingSeats(conn, booking);
            return booking;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save booking: " + e.getMessage(), e);
        }
    }

    private Booking updateStatus(Booking booking) {
        String sql = "UPDATE bookings SET status = ? WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, booking.getStatus().name());
            ps.setLong(2, booking.getBookingId());
            ps.executeUpdate();
            return booking;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update booking: " + e.getMessage(), e);
        }
    }

    private void saveBookingSeats(Connection conn, Booking booking) throws SQLException {
        String sql = "INSERT IGNORE INTO booking_seats (booking_id, seat_id) VALUES (?, ?)";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Long seatId : booking.getSeatIds()) {
                ps.setLong(1, booking.getBookingId());
                ps.setLong(2, seatId);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    @Override
    public Optional<Booking> findById(long id) {
        String sql = "SELECT * FROM bookings WHERE id = ?";
        
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
            throw new RuntimeException("Failed to fetch bookings by user: " + e.getMessage(), e);
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
            throw new RuntimeException("Failed to fetch bookings by show: " + e.getMessage(), e);
        }
    }

    private List<Long> findSeatIds(Connection conn, long bookingId) throws SQLException {
        String sql = "SELECT seat_id FROM booking_seats WHERE booking_id = ?";
        List<Long> seatIds = new ArrayList<>();
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, bookingId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    seatIds.add(rs.getLong("seat_id"));
                }
            }
        }
        return seatIds;
    }

    private Booking mapRow(Connection conn, ResultSet rs) throws SQLException {
        long bookingId = rs.getLong("id");
        List<Long> seatIds = findSeatIds(conn, bookingId);

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
