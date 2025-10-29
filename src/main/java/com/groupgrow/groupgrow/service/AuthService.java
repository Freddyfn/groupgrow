package com.groupgrow.groupgrow.service;

import com.groupgrow.groupgrow.dto.LoginResponse; // Asegúrate que este import esté bien
import com.groupgrow.groupgrow.model.User;
import com.groupgrow.groupgrow.repository.UserRepository;
import com.groupgrow.groupgrow.security.JwtTokenProvider; // Asegúrate que este import esté bien
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
// --- EL NOMBRE DE LA CLASE DEBE SER AuthService ---
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public User register(User user) {
        user.setPasswordHash(encoder.encode(user.getPasswordHash()));
        user.setKycStatus("pending");
        user.setTwofaEnabled(false);
        user.setTwofaSecret(null);
        return userRepository.save(user);
    }

    public LoginResponse login(String email, String password) {
        User user = userRepository.findByEmail(email);

        if (user != null && encoder.matches(password, user.getPasswordHash())) {

            if (user.isTwofaEnabled()) {
                return new LoginResponse("2FA_REQUIRED", null, user.getId());
            } else {
                String token = jwtTokenProvider.generateToken(user);
                return new LoginResponse("SUCCESS", token, user.getId());
            }
        }

        return new LoginResponse("FAILURE", null, null);
    }
}