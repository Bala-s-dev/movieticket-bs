package com.movieticket.repository.DAO;

import com.movieticket.model.User;
import com.movieticket.repository.UserRepository;
import com.movieticket.util.DatabaseManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.sql.Statement;

public class DAOUserRepository implements UserRepository {

    @Override
    public User save(User user) {
        String sql = "INSERT INTO users (name, email, phone, password) " +
                "VALUES (?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "id = LAST_INSERT_ID(id), " +
                "name = VALUES(name), " +
                "phone = VALUES(phone), " +
                "password = VALUES(password)";

        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    sql,
                    Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhone());
            ps.setString(4, user.getPassword());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setUserId(rs.getLong(1));
                }
            }

            return user;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save User: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<User> findById(long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
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
            throw new RuntimeException("Failed to fetch user: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch User by email: " + e.getMessage(), e);
        }
    }

    // @Override
    // public List<Admin> findAll() {
    //     String sql = "SELECT * FROM admins";
    //     List<Admin> admins = new ArrayList<>();
    //     try (Connection conn = DatabaseManager.getConnection();
    //          PreparedStatement ps = conn.prepareStatement(sql);
    //          ResultSet rs = ps.executeQuery()) {
    //         while (rs.next()) {
    //             admins.add(mapRow(rs));
    //         }
    //         return admins;
    //     } catch (SQLException e) {
    //         throw new RuntimeException("Failed to fetch admins: " + e.getMessage(), e);
    //     }
    // }

    @Override
    public boolean existsByEmail(String email) {
        return findByEmail(email).isPresent();
    }

    private User mapRow(ResultSet rs) throws SQLException {
        return new User(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getString("password")
        );
    }

	@Override
	public List<User> findAll() {
		// TODO
		throw new UnsupportedOperationException("Unimplemented method 'findAll'");
	}
}