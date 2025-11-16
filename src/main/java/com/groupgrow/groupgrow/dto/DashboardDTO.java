package com.groupgrow.groupgrow.dto;

import java.util.List;

// --- DTOs para el Dashboard ---

public class DashboardDTO {
    private DashboardStats stats;
    private List<PerformanceDataPoint> performance;
    private List<PortfolioItem> portfolio;
    private List<UserGroup> groups;
    private List<String> aiTips;

    // Getters y Setters
    public DashboardStats getStats() { return stats; }
    public void setStats(DashboardStats stats) { this.stats = stats; }
    public List<PerformanceDataPoint> getPerformance() { return performance; }
    public void setPerformance(List<PerformanceDataPoint> performance) { this.performance = performance; }
    public List<PortfolioItem> getPortfolio() { return portfolio; }
    public void setPortfolio(List<PortfolioItem> portfolio) { this.portfolio = portfolio; }
    public List<UserGroup> getGroups() { return groups; }
    public void setGroups(List<UserGroup> groups) { this.groups = groups; }
    public List<String> getAiTips() { return aiTips; }
    public void setAiTips(List<String> aiTips) { this.aiTips = aiTips; }
}
