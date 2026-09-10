package com.movieticket.repository.DAO;

import com.movieticket.model.Movie;
import com.movieticket.repository.MovieRepository;
import com.movieticket.util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DAOMovieRepository implements MovieRepository {

    @Override
    public Movie save(Movie movie) {
        String sql = "INSERT INTO movies (name, description, language, genre, duration_minutes, release_date, active) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, movie.getName());
            pstmt.setString(2, movie.getDescription());
            pstmt.setString(3, movie.getLanguage());
            pstmt.setString(4, movie.getGenre());
            pstmt.setInt(5, movie.getDurationMinutes());
            pstmt.setDate(6, Date.valueOf(movie.getReleaseDate()));
            pstmt.setBoolean(7, movie.isActive());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    movie.setMovieId(generatedKeys.getLong(1));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error saving movie: " + e.getMessage());
        }

        return movie;
    }

    @Override
    public Optional<Movie> findById(long id) {
        String sql = "SELECT * FROM movies WHERE id = ? AND active=true AND release_date>=CURRENT_DATE()";
        Movie movie = null;

        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    movie = mapResultSetToMovie(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error finding movie by ID: " + e.getMessage());
        }

        return Optional.ofNullable(movie);
    }

    @Override
    public List<Movie> findAll() {
        List<Movie> movies = new ArrayList<>();
        String sql = "SELECT * FROM movies WHERE active=true AND release_date>=CURRENT_DATE()";

        try (Connection conn = DatabaseManager.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                movies.add(mapResultSetToMovie(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error finding all movies: " + e.getMessage());
        }

        return movies;
    }

    @Override
    public void deleteById(long id) {
        String sql = "DELETE FROM movies WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error deleting movie: " + e.getMessage());
        }
    }

    private Movie mapResultSetToMovie(ResultSet rs) throws SQLException {
        Movie movie = new Movie();
        movie.setMovieId(rs.getLong("id"));
        movie.setName(rs.getString("name"));
        movie.setDescription(rs.getString("description"));
        movie.setLanguage(rs.getString("language"));
        movie.setGenre(rs.getString("genre"));
        movie.setDurationMinutes(rs.getInt("duration_minutes"));
        movie.setReleaseDate(rs.getDate("release_date").toLocalDate());
        movie.setActive(rs.getBoolean("active"));
        return movie;
    }
}
