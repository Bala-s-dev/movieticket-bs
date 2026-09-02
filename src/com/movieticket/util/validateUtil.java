package com.movieticket.util;

import com.movieticket.exception.ValidationException;


public class validateUtil {
    public static void validateName(String name) {
        if (name == null || name.trim().isEmpty() || !name.matches("^[a-zA-Z\\s]+$")) {
            throw new ValidationException("Name cannot be empty and must contain only letters and spaces.");
        }
    }

    public static void validateEmail(String email) {
        if (email == null || !email.matches("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$")) {
            throw new ValidationException("Invalid email format.");
        }
    }

    public static void validatePhone(String phone) {
        if (phone == null || !phone.matches("^\\d{10}$")) {
            throw new ValidationException("Phone number must be exactly 10 digits.");
        }
    }

    public static void validatePassword(String password) {
        if (password == null || password.trim().isEmpty() || password.length() < 6) {
            throw new ValidationException("Password cannot be empty and must be at least 6 characters long.");
        }
    }
}
