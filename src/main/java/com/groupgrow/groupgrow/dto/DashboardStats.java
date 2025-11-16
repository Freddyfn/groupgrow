package com.groupgrow.groupgrow.dto;

public class DashboardStats {
    private double totalSaved;
    private double totalInvested;
    private double monthlyGrowth;
    private int activeGroups;
    private double nextPaymentAmount;
    private int nextPaymentDays;

    // Getters y Setters
    public double getTotalSaved() { return totalSaved; }
    public void setTotalSaved(double totalSaved) { this.totalSaved = totalSaved; }
    public double getTotalInvested() { return totalInvested; }
    public void setTotalInvested(double totalInvested) { this.totalInvested = totalInvested; }
    public double getMonthlyGrowth() { return monthlyGrowth; }
    public void setMonthlyGrowth(double monthlyGrowth) { this.monthlyGrowth = monthlyGrowth; }
    public int getActiveGroups() { return activeGroups; }
    public void setActiveGroups(int activeGroups) { this.activeGroups = activeGroups; }
    public double getNextPaymentAmount() { return nextPaymentAmount; }
    public void setNextPaymentAmount(double nextPaymentAmount) { this.nextPaymentAmount = nextPaymentAmount; }
    public int getNextPaymentDays() { return nextPaymentDays; }
    public void setNextPaymentDays(int nextPaymentDays) { this.nextPaymentDays = nextPaymentDays; }
}
