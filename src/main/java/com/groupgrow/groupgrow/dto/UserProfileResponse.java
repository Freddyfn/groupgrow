package com.groupgrow.groupgrow.dto;

public class UserProfileResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String riskProfile;
    private boolean twofaEnabled;
    private String kycStatus;

    // Constructor
    public UserProfileResponse() {}

    public UserProfileResponse(Long id, String firstName, String lastName, String email, 
                              String phone, String riskProfile, boolean twofaEnabled, String kycStatus) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.riskProfile = riskProfile;
        this.twofaEnabled = twofaEnabled;
        this.kycStatus = kycStatus;
    }

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
    public boolean isTwofaEnabled() { return twofaEnabled; }
    public void setTwofaEnabled(boolean twofaEnabled) { this.twofaEnabled = twofaEnabled; }
    public String getKycStatus() { return kycStatus; }
    public void setKycStatus(String kycStatus) { this.kycStatus = kycStatus; }
}

