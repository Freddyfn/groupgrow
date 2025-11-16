package com.groupgrow.groupgrow.controller; // <-- PAQUETE CORRECTO

import com.groupgrow.groupgrow.dto.LoginResponse;
import com.groupgrow.groupgrow.dto.QrResponse;
import com.groupgrow.groupgrow.dto.UpdateProfileRequest;
import com.groupgrow.groupgrow.dto.UserProfileResponse;
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

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping({"/api/v1/auth", "/api/auth"})
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

    // --- ENDPOINTS DE PERFIL ---
    private Long getUserIdFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtTokenProvider.validateToken(token)) {
                return jwtTokenProvider.getUserIdFromJWT(token);
            }
        }
        throw new RuntimeException("Token inválido o no proporcionado");
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                return ResponseEntity.badRequest().body("Usuario no encontrado");
            }
            UserProfileResponse profile = new UserProfileResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                user.getRiskProfile(),
                user.isTwofaEnabled(),
                user.getKycStatus()
            );
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al obtener el perfil");
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody UpdateProfileRequest request, HttpServletRequest httpRequest) {
        try {
            Long userId = getUserIdFromRequest(httpRequest);
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                return ResponseEntity.badRequest().body("Usuario no encontrado");
            }
            if (request.getFirstName() != null) {
                user.setFirstName(request.getFirstName());
            }
            if (request.getLastName() != null) {
                user.setLastName(request.getLastName());
            }
            if (request.getPhone() != null) {
                user.setPhone(request.getPhone());
            }
            if (request.getRiskProfile() != null) {
                user.setRiskProfile(request.getRiskProfile());
            }
            userRepository.save(user);
            UserProfileResponse profile = new UserProfileResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhone(),
                user.getRiskProfile(),
                user.isTwofaEnabled(),
                user.getKycStatus()
            );
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al actualizar el perfil");
        }
    }

    @PostMapping("/2fa/disable")
    public ResponseEntity<?> disable2FA(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                return ResponseEntity.badRequest().body("Usuario no encontrado");
            }
            // Desactivar 2FA y eliminar el secret para que pueda volver a activarlo
            user.setTwofaEnabled(false);
            user.setTwofaSecret(null);
            userRepository.save(user);
            return ResponseEntity.ok().body("2FA desactivado exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al desactivar 2FA");
        }
    }
}