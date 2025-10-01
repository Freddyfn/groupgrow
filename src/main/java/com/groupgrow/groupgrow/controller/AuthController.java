package com.groupgrow.groupgrow.controller;

import com.groupgrow.groupgrow.model.User;
import com.groupgrow.groupgrow.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        return ResponseEntity.ok(authService.register(user));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User loginUser) {
        User user = authService.login(loginUser.getEmail(), loginUser.getPasswordHash());
        if (user != null) {
            return ResponseEntity.ok("Login exitoso: " + user.getId());  // En producción, retorna token
        }
        return ResponseEntity.badRequest().body("Credenciales inválidas");
    }
}