package com.groupgrow.groupgrow.dto;

import java.math.BigDecimal;

public class PaymentResponse {
    private Long transactionId;
    private String status;
    private BigDecimal amount;
    private String message;

    public PaymentResponse() {}

    public PaymentResponse(Long transactionId, String status, BigDecimal amount, String message) {
        this.transactionId = transactionId;
        this.status = status;
        this.amount = amount;
        this.message = message;
    }

    // Getters y Setters
    public Long getTransactionId() { return transactionId; }
    public void setTransactionId(Long transactionId) { this.transactionId = transactionId; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}

