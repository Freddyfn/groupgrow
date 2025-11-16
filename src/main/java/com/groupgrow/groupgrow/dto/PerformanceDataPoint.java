package com.groupgrow.groupgrow.dto;

public class PerformanceDataPoint {
    private String month;
    private double amount;

    public PerformanceDataPoint(String month, double amount) {
        this.month = month;
        this.amount = amount;
    }

    // Getters y Setters
    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
}
