package com.movieticket.repository.DAO;

import com.movieticket.model.Show;
import com.movieticket.model.TicketPricing;
import com.movieticket.repository.ShowRepository;
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

public class DAOShowRepository implements ShowRepository {

    @Override
    public Show save(Show show) {
        if (show.getShowId() == 0) {
            return insert(show);
        }
        return update(show);
    }

    private Show insert(Show show) {
        String sql = "INSERT INTO shows " +
                "(movie_id, screen_id, start_datetime, end_datetime, price_gold, price_platinum, price_silver, active) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            bindShowFields(ps, show);
            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    show.setShowId(generatedKeys.getLong(1));
                }
            }
            return show;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save show: " + e.getMessage(), e);
        }
    }

    private Show update(Show show) {
        String sql = "UPDATE shows SET movie_id = ?, screen_id = ?, start_datetime = ?, end_datetime = ?, " +
                "price_gold = ?, price_platinum = ?, price_silver = ?, active = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int nextIndex = bindShowFields(ps, show);
            ps.setLong(nextIndex, show.getShowId());
            ps.executeUpdate();
            return show;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update show: " + e.getMessage(), e);
        }
    }

    private int bindShowFields(PreparedStatement ps, Show show) throws SQLException {
        ps.setLong(1, show.getMovieId());
        ps.setLong(2, show.getScreenId());
        ps.setTimestamp(3, Timestamp.valueOf(show.getStartDateTime()));
        ps.setTimestamp(4, Timestamp.valueOf(show.getEndDateTime()));
        ps.setDouble(5, show.getPricing().getPrice(com.movieticket.enums.SeatCategory.GOLD));
        ps.setDouble(6, show.getPricing().getPrice(com.movieticket.enums.SeatCategory.PLATINUM));
        ps.setDouble(7, show.getPricing().getPrice(com.movieticket.enums.SeatCategory.SILVER));
        ps.setBoolean(8, show.isActive());
        return 9;
    }

    @Override
    public Optional<Show> findById(long id) {
        String sql = "SELECT * FROM shows WHERE id = ?";
        
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
            throw new RuntimeException("Failed to fetch show: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Show> findAll() {
        String sql = "SELECT * FROM shows";
        List<Show> shows = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
        
            while (rs.next()) {
                shows.add(mapRow(rs));
            }
            return shows;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch shows: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Show> findByScreenId(long screenId) {
        String sql = "SELECT * FROM shows WHERE screen_id = ?";
        List<Show> shows = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, screenId);
            
            try (ResultSet rs = ps.executeQuery()) {
                
                while (rs.next()) {
                    shows.add(mapRow(rs));
                }
            }
            return shows;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch shows by screen: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Show> findByMovieId(long movieId) {
        String sql = "SELECT * FROM shows WHERE movie_id = ?";
        List<Show> shows = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, movieId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    shows.add(mapRow(rs));
                }
            }
            return shows;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch shows by movie: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteById(long id) {
        String sql = "DELETE FROM shows WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete show: " + e.getMessage(), e);
        }
    }

    private Show mapRow(ResultSet rs) throws SQLException {
        TicketPricing pricing = new TicketPricing(
                rs.getDouble("price_gold"),
                rs.getDouble("price_platinum"),
                rs.getDouble("price_silver")
        );

        Show show = new Show(
                rs.getLong("id"),
                rs.getLong("movie_id"),
                rs.getLong("screen_id"),
                rs.getTimestamp("start_datetime").toLocalDateTime(),
                rs.getTimestamp("end_datetime").toLocalDateTime(),
                pricing
        );
        show.setActive(rs.getBoolean("active"));
        return show;
    }
}
