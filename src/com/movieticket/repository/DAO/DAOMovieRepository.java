package com.movieticket.repository.DAO;

import com.movieticket.model.Movie;
import com.movieticket.repository.MovieRepository;
import com.movieticket.util.DatabaseManager;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DAOMovieRepository implements MovieRepository {

    @Override
    public Movie save(Movie movie) {
        String sql = "INSERT INTO movies (movie_id, name, description, language, genre, duration_minutes, " +
                "release_date, rating, active) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE name = VALUES(name), description = VALUES(description), " +
                "language = VALUES(language), genre = VALUES(genre), duration_minutes = VALUES(duration_minutes), " +
                "release_date = VALUES(release_date), rating = VALUES(rating), active = VALUES(active)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, movie.getMovieId());
            ps.setString(2, movie.getName());
            ps.setString(3, movie.getDescription());
            ps.setString(4, movie.getLanguage());
            ps.setString(5, movie.getGenre());
            ps.setInt(6, movie.getDurationMinutes());
            ps.setDate(7, movie.getReleaseDate() != null ? Date.valueOf(movie.getReleaseDate()) : null);
            ps.setDouble(8, movie.getRating());
            ps.setBoolean(9, movie.isActive());
            ps.executeUpdate();
            return movie;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save movie: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Movie> findById(long id) {
        String sql = "SELECT * FROM movies WHERE movie_id = ?";
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
            throw new RuntimeException("Failed to fetch movie: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Movie> findAll() {
        String sql = "SELECT * FROM movies";
        List<Movie> movies = new ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                movies.add(mapRow(rs));
            }
            return movies;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch movies: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteById(long id) {
        String sql = "DELETE FROM movies WHERE movie_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete movie: " + e.getMessage(), e);
        }
    }

    private Movie mapRow(ResultSet rs) throws SQLException {
        Movie movie = new Movie(
                rs.getLong("movie_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getString("language"),
                rs.getString("genre"),
                rs.getInt("duration_minutes"),
                rs.getDate("release_date") != null ? rs.getDate("release_date").toLocalDate() : null,
                rs.getDouble("rating")
        );
        movie.setActive(rs.getBoolean("active"));
        return movie;
    }
}
