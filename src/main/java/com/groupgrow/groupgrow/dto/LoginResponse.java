package com.groupgrow.groupgrow.dto;

// Esta clase transportará la respuesta de login al frontend
public class LoginResponse {
    private String status; // "SUCCESS", "2FA_REQUIRED", "FAILURE"
    private String token;  // El JWT
    private Long userId;

    public LoginResponse(String status, String token, Long userId) {
        this.status = status;
        this.token = token;
        this.userId = userId;
    }

    // Getters y Setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}