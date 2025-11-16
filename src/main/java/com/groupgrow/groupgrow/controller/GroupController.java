package com.groupgrow.groupgrow.controller;

import com.groupgrow.groupgrow.dto.CreateGroupRequest;
import com.groupgrow.groupgrow.dto.GroupResponse;
import com.groupgrow.groupgrow.dto.UpdateGroupRequest;
import com.groupgrow.groupgrow.security.JwtTokenProvider;
import com.groupgrow.groupgrow.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping({"/api/v1/groups", "/api/groups"})
public class GroupController {

    @Autowired
    private GroupService groupService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

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

    @PostMapping
    public ResponseEntity<?> createGroup(@RequestBody CreateGroupRequest request, HttpServletRequest httpRequest) {
        try {
            Long userId = getUserIdFromRequest(httpRequest);
            GroupResponse group = groupService.createGroup(request, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(group);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear el grupo");
        }
    }

    @GetMapping("/my-groups")
    public ResponseEntity<?> getMyGroups(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            List<GroupResponse> groups = groupService.getGroupsByCreator(userId);
            return ResponseEntity.ok(groups);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener los grupos: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getGroupById(@PathVariable Long id, HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            
            // Devolvemos el dashboard completo del grupo con toda la información del usuario
            var dashboard = groupService.getGroupDashboardForUser(id, userId);
            return ResponseEntity.ok(dashboard);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener el grupo");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateGroup(@PathVariable Long id, @RequestBody UpdateGroupRequest request, HttpServletRequest httpRequest) {
        try {
            Long userId = getUserIdFromRequest(httpRequest);
            GroupResponse group = groupService.updateGroup(id, request, userId);
            return ResponseEntity.ok(group);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar el grupo");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGroup(@PathVariable Long id, HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            groupService.deleteGroup(id, userId);
            return ResponseEntity.ok().body("Grupo eliminado exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar el grupo");
        }
    }
    
    @GetMapping("/public")
    public ResponseEntity<?> getPublicGroups(HttpServletRequest request) {
        try {
            // Intentar obtener el userId si existe
            Long userId = null;
            try {
                userId = getUserIdFromRequest(request);
            } catch (Exception e) {
                // Si no hay token, continuar sin userId
            }
            
            List<GroupResponse> publicGroups = groupService.getPublicGroupsForUser(userId);
            return ResponseEntity.ok(publicGroups);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener los grupos públicos");
        }
    }
    
    @PostMapping("/{id}/join")
    public ResponseEntity<?> joinGroup(@PathVariable Long id, HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            groupService.joinGroup(id, userId);
            return ResponseEntity.ok().body("Te has unido al grupo exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al unirse al grupo");
        }
    }
}

