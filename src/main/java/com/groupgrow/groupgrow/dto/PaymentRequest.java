package com.groupgrow.groupgrow.dto;

import java.math.BigDecimal;

public class PaymentRequest {
    private Long groupId;
    private BigDecimal amount;
    private String cardNumber;  // Solo para validación, no se guarda
    private String cardName;
    private String description;

    // Getters y Setters
    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    
    public String getCardName() { return cardName; }
    public void setCardName(String cardName) { this.cardName = cardName; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}

