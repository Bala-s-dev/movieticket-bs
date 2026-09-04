package com.movieticket.service;

import com.movieticket.exception.AuthenticationException;
import com.movieticket.exception.ValidationException;
import com.movieticket.model.Admin;
import com.movieticket.model.User;
import com.movieticket.repository.AdminRepository;
import com.movieticket.repository.UserRepository;
import com.movieticket.util.IdGenerator;
import com.movieticket.util.PasswordUtil;

public class AuthService {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;

    public AuthService(UserRepository userRepository, AdminRepository adminRepository) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
    }

    public User registerUser(String name, String email, String phone, String password) {
        
        if (userRepository.existsByEmail(email)) {
            throw new ValidationException("A user with this email is already registered.");
        }
        String hashedPassword = PasswordUtil.hashPassword(password);
        User user = new User(IdGenerator.nextUserId(), name, email, phone, hashedPassword);
        
        return userRepository.save(user);
    }

    public Admin registerAdmin(String name, String email, String phone, String password) {

        if (adminRepository.existsByEmail(email)) {
            throw new ValidationException("An admin with this email is already registered.");
        }
        
        String hashedPassword = PasswordUtil.hashPassword(password);

        Admin admin = new Admin(IdGenerator.nextAdminId(), name, email, phone, hashedPassword);

        return adminRepository.save(admin);
    }

    public User loginUser(String email, String password) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationException("Invalid email or password."));

        String hashedPassword = PasswordUtil.hashPassword(password);

        if (!user.checkPassword(hashedPassword)) {
            throw new AuthenticationException("Invalid password.");
        }

        return user;
    }

    public Admin loginAdmin(String email, String password) {

        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationException("Invalid email or password."));

        String hashedPassword = PasswordUtil.hashPassword(password);

        if (!admin.checkPassword(hashedPassword)) {
            throw new AuthenticationException("Invalid password.");
        }

        return admin;
    }

}
