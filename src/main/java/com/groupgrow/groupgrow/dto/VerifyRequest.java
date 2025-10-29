package com.groupgrow.groupgrow.dto;

// Esta clase transportará las solicitudes de verificación
public class VerifyRequest {
    private Long userId;
    private String code;

    // Getters y Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
}