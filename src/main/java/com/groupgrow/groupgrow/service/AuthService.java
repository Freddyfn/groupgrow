package com.groupgrow.groupgrow.service;

import com.groupgrow.groupgrow.model.User;
import com.groupgrow.groupgrow.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public User register(User user) {
        user.setPasswordHash(encoder.encode(user.getPasswordHash()));  // Hash contraseña
        user.setKycStatus("pending");
        return userRepository.save(user);
    }

    public User login(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user != null && encoder.matches(password, user.getPasswordHash())) {
            return user;  // En producción, genera token
        }
        return null;
    }
}