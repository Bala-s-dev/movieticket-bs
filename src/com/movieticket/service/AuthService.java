package com.movieticket.service;

import com.movieticket.exception.AuthenticationException;
import com.movieticket.exception.ValidationException;
import com.movieticket.model.Admin;
import com.movieticket.model.User;
import com.movieticket.repository.AdminRepository;
import com.movieticket.repository.UserRepository;
import com.movieticket.util.IdGenerator;

public class AuthService {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;

    public AuthService(UserRepository userRepository, AdminRepository adminRepository) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
    }

    public User registerUser(String name, String email, String phone, String password) {
        validateRegistration(name, email, phone, password);
        if (userRepository.existsByEmail(email)) {
            throw new ValidationException("A user with this email is already registered.");
        }
        User user = new User(IdGenerator.nextUserId(), name, email, phone, password);
        return userRepository.save(user);
    }

    public Admin registerAdmin(String name, String email, String phone, String password) {
        validateRegistration(name, email, phone, password);
        if (adminRepository.existsByEmail(email)) {
            throw new ValidationException("An admin with this email is already registered.");
        }
        Admin admin = new Admin(IdGenerator.nextAdminId(), name, email, phone, password);
        return adminRepository.save(admin);
    }

    public User loginUser(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationException("Invalid email or password."));
        if (!user.checkPassword(password)) {
            throw new AuthenticationException("Invalid password.");
        }
        return user;
    }

    public Admin loginAdmin(String email, String password) {
        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationException("Invalid email or password."));
        if (!admin.checkPassword(password)) {
            throw new AuthenticationException("Invalid email or password.");
        }
        return admin;
    }

    private void validateRegistration(String name, String email, String phone, String password) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Name cannot be empty.");
        }
        if (email == null || !email.matches("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$")) {
            throw new ValidationException("Invalid email format.");
        }
        if (phone == null || !phone.matches("^\\d{10}$")) {
            throw new ValidationException("Phone number must be exactly 10 digits.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new ValidationException("Password cannot be empty.");
        }
    }
}
