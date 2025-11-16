package com.groupgrow.groupgrow.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class UserGroup {
    private Long id;
    private String name;
    private int members;
    private BigDecimal target;
    private BigDecimal current;
    private BigDecimal monthlyContribution;
    private LocalDate deadline;
    private String status;

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getMembers() { return members; }
    public void setMembers(int members) { this.members = members; }
    public BigDecimal getTarget() { return target; }
    public void setTarget(BigDecimal target) { this.target = target; }
    public BigDecimal getCurrent() { return current; }
    public void setCurrent(BigDecimal current) { this.current = current; }
    public BigDecimal getMonthlyContribution() { return monthlyContribution; }
    public void setMonthlyContribution(BigDecimal monthlyContribution) { this.monthlyContribution = monthlyContribution; }
    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
