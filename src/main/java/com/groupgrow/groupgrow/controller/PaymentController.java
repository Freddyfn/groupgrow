package com.groupgrow.groupgrow.controller;

import com.groupgrow.groupgrow.dto.PaymentRequest;
import com.groupgrow.groupgrow.dto.PaymentResponse;
import com.groupgrow.groupgrow.repository.GroupMemberRepository;
import com.groupgrow.groupgrow.security.JwtTokenProvider;
import com.groupgrow.groupgrow.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/v1/payments", "/api/payments"})
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @Autowired
    private GroupMemberRepository groupMemberRepository;

    private Long getUserIdFromRequest(HttpServletRequest request, Long groupId) {
        // 1. Intentar obtener el userId desde el token JWT
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtTokenProvider.validateToken(token)) {
                return jwtTokenProvider.getUserIdFromJWT(token);
            }
        }
        
        // 2. Para testing: Obtener el creador del grupo o el primer miembro
        if (groupId != null) {
            List<Long> memberIds = groupMemberRepository.findUserIdsByGroupId(groupId);
            if (!memberIds.isEmpty()) {
                Long userId = memberIds.get(0); // Toma el primer miembro
                System.out.println("ADVERTENCIA: No se encontró token JWT. Usando user_id=" + userId + " (primer miembro del grupo)");
                return userId;
            }
        }
        
        // 3. Fallback: retornar ID por defecto
        System.out.println("ADVERTENCIA: No se pudo determinar el usuario. Usando user_id=1 por defecto");
        return 1L;
    }

    @PostMapping("/process")
    public ResponseEntity<?> processPayment(@RequestBody PaymentRequest request, HttpServletRequest httpRequest) {
        try {
            Long userId = getUserIdFromRequest(httpRequest, request.getGroupId());
            System.out.println("Procesando pago: user_id=" + userId + ", group_id=" + request.getGroupId() + ", amount=$" + request.getAmount());
            PaymentResponse response = paymentService.processPayment(request, userId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al procesar el pago: " + e.getMessage());
        }
    }
}

