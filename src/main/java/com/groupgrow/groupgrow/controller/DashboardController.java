package com.groupgrow.groupgrow.controller;

import com.groupgrow.groupgrow.dto.DashboardDTO;
import com.groupgrow.groupgrow.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/{userId}")
    public ResponseEntity<DashboardDTO> getDashboardData(@PathVariable Long userId) {
        try {
            // Obtener el ID del usuario autenticado del contexto de seguridad
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || authentication.getPrincipal() == null) {
                return ResponseEntity.status(401).build(); // No autenticado
            }
            
            Long authenticatedUserId = (Long) authentication.getPrincipal();
            
            // Validar que el usuario solo pueda acceder a su propio dashboard
            if (!authenticatedUserId.equals(userId)) {
                return ResponseEntity.status(403).build(); // Forbidden: intentando acceder al dashboard de otro usuario
            }
            
            DashboardDTO data = dashboardService.getDashboardData(userId);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            // Considerar un manejo de errores más específico
            return ResponseEntity.status(500).build();
        }
    }
}
