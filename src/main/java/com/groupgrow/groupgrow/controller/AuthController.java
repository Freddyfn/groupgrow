package com.groupgrow.groupgrow.controller; // <-- PAQUETE CORRECTO

import com.groupgrow.groupgrow.dto.LoginResponse;
import com.groupgrow.groupgrow.dto.QrResponse;
import com.groupgrow.groupgrow.dto.VerifyRequest;
import com.groupgrow.groupgrow.model.User;
import com.groupgrow.groupgrow.repository.UserRepository;
import com.groupgrow.groupgrow.security.JwtTokenProvider;
// --- IMPORTA AuthService DESDE EL PAQUETE service ---
import com.groupgrow.groupgrow.service.AuthService;
import com.groupgrow.groupgrow.service.TotpService;
// ---------------------------------------------------
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
// --- EL NOMBRE DE LA CLASE DEBE SER AuthController ---
public class AuthController {

    // --- SERVICIOS INYECTADOS ---
    @Autowired
    private AuthService authService; // <-- Correcto

    @Autowired
    private TotpService totpService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;


    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        return ResponseEntity.ok(authService.register(user));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody User loginUser) {
        LoginResponse response = authService.login(loginUser.getEmail(), loginUser.getPasswordHash());

        if ("FAILURE".equals(response.getStatus())) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    // --- ENDPOINTS 2FA ---
    @PostMapping("/2fa/generate-qr")
    public ResponseEntity<?> generateQrCode(@RequestBody VerifyRequest request) {
        User user = userRepository.findById(request.getUserId()).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body("Usuario no encontrado");
        }
        String secret = totpService.generateNewSecret();
        String qrCodeBase64 = totpService.getQrCodeAsBase64(user.getEmail(), secret);
        user.setTwofaSecret(secret);
        user.setTwofaEnabled(false);
        userRepository.save(user);
        return ResponseEntity.ok(new QrResponse(qrCodeBase64));
    }

    @PostMapping("/2fa/enable-verify")
    public ResponseEntity<?> enableTwoFactor(@RequestBody VerifyRequest request) {
        User user = userRepository.findById(request.getUserId()).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body("Usuario no encontrado");
        }
        if (totpService.verifyCode(user.getTwofaSecret(), request.getCode())) {
            user.setTwofaEnabled(true);
            userRepository.save(user);
            return ResponseEntity.ok().body("2FA activado exitosamente");
        } else {
            return ResponseEntity.badRequest().body("Código incorrecto");
        }
    }

    @PostMapping("/2fa/login-verify")
    public ResponseEntity<?> verifyLogin(@RequestBody VerifyRequest request) {
        User user = userRepository.findById(request.getUserId()).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body("Usuario no encontrado");
        }
        if (totpService.verifyCode(user.getTwofaSecret(), request.getCode())) {
            String token = jwtTokenProvider.generateToken(user);
            return ResponseEntity.ok(new LoginResponse("SUCCESS", token, user.getId()));
        } else {
            return ResponseEntity.badRequest().body("Código incorrecto");
        }
    }
}