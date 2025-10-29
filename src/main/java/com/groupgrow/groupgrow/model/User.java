package com.groupgrow.groupgrow.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(unique = true)
    private String email;

    private String phone;

    @Column(name = "risk_profile")
    private String riskProfile;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "twofa_secret")
    private String twofaSecret;

    @Column(name = "kyc_status")
    private String kycStatus;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // --- CAMPO AÑADIDO ---
    @Column(name = "twofa_enabled")
    private boolean twofaEnabled = false;

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getRiskProfile() { return riskProfile; }
    public void setRiskProfile(String riskProfile) { this.riskProfile = riskProfile; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getTwofaSecret() { return twofaSecret; }
    public void setTwofaSecret(String twofaSecret) { this.twofaSecret = twofaSecret; }
    public String getKycStatus() { return kycStatus; }
    public void setKycStatus(String kycStatus) { this.kycStatus = kycStatus; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // --- GETTER Y SETTER AÑADIDOS ---
    public boolean isTwofaEnabled() { return twofaEnabled; }
    public void setTwofaEnabled(boolean twofaEnabled) { this.twofaEnabled = twofaEnabled; }
}