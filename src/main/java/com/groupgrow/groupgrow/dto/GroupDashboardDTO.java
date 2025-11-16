package com.groupgrow.groupgrow.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class GroupDashboardDTO {
    private String id;
    private String name;
    private String description;
    private String type;
    private BigDecimal target;
    private BigDecimal current;
    private BigDecimal monthlyContribution;
    private LocalDate deadline;
    private LocalDate investmentTerm;
    private String status;
    private BigDecimal userContribution;
    private BigDecimal userProfits;
    private BigDecimal totalProfits;
    private List<MemberDTO> members;
    private List<PerformanceDTO> performance;
    private List<TransactionDTO> transactions;
    private ActiveVotingDTO activeVoting;

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public BigDecimal getTarget() { return target; }
    public void setTarget(BigDecimal target) { this.target = target; }
    public BigDecimal getCurrent() { return current; }
    public void setCurrent(BigDecimal current) { this.current = current; }
    public BigDecimal getMonthlyContribution() { return monthlyContribution; }
    public void setMonthlyContribution(BigDecimal monthlyContribution) { this.monthlyContribution = monthlyContribution; }
    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }
    public LocalDate getInvestmentTerm() { return investmentTerm; }
    public void setInvestmentTerm(LocalDate investmentTerm) { this.investmentTerm = investmentTerm; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public BigDecimal getUserContribution() { return userContribution; }
    public void setUserContribution(BigDecimal userContribution) { this.userContribution = userContribution; }
    public BigDecimal getUserProfits() { return userProfits; }
    public void setUserProfits(BigDecimal userProfits) { this.userProfits = userProfits; }
    public BigDecimal getTotalProfits() { return totalProfits; }
    public void setTotalProfits(BigDecimal totalProfits) { this.totalProfits = totalProfits; }
    public List<MemberDTO> getMembers() { return members; }
    public void setMembers(List<MemberDTO> members) { this.members = members; }
    public List<PerformanceDTO> getPerformance() { return performance; }
    public void setPerformance(List<PerformanceDTO> performance) { this.performance = performance; }
    public List<TransactionDTO> getTransactions() { return transactions; }
    public void setTransactions(List<TransactionDTO> transactions) { this.transactions = transactions; }
    public ActiveVotingDTO getActiveVoting() { return activeVoting; }
    public void setActiveVoting(ActiveVotingDTO activeVoting) { this.activeVoting = activeVoting; }

    // DTOs anidados
    public static class MemberDTO {
        private Long id;
        private String name;
        private String avatar;
        private BigDecimal contribution;
        private String status;

        public MemberDTO(Long id, String name, String avatar, BigDecimal contribution, String status) {
            this.id = id;
            this.name = name;
            this.avatar = avatar;
            this.contribution = contribution;
            this.status = status;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getAvatar() { return avatar; }
        public void setAvatar(String avatar) { this.avatar = avatar; }
        public BigDecimal getContribution() { return contribution; }
        public void setContribution(BigDecimal contribution) { this.contribution = contribution; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    public static class PerformanceDTO {
        private String month;
        private BigDecimal amount;
        private BigDecimal target;

        public PerformanceDTO(String month, BigDecimal amount, BigDecimal target) {
            this.month = month;
            this.amount = amount;
            this.target = target;
        }

        public String getMonth() { return month; }
        public void setMonth(String month) { this.month = month; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public BigDecimal getTarget() { return target; }
        public void setTarget(BigDecimal target) { this.target = target; }
    }

    public static class TransactionDTO {
        private Long id;
        private String type;
        private BigDecimal amount;
        private String member;
        private String description;
        private String date;
        private String status;

        public TransactionDTO(Long id, String type, BigDecimal amount, String member, String description, String date, String status) {
            this.id = id;
            this.type = type;
            this.amount = amount;
            this.member = member;
            this.description = description;
            this.date = date;
            this.status = status;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public String getMember() { return member; }
        public void setMember(String member) { this.member = member; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }

    public static class ActiveVotingDTO {
        private Long id;
        private String title;
        private String description;
        private BigDecimal amount;
        private int votesFor;
        private int votesAgainst;
        private int totalMembers;
        private String deadline;
        private String details;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public int getVotesFor() { return votesFor; }
        public void setVotesFor(int votesFor) { this.votesFor = votesFor; }
        public int getVotesAgainst() { return votesAgainst; }
        public void setVotesAgainst(int votesAgainst) { this.votesAgainst = votesAgainst; }
        public int getTotalMembers() { return totalMembers; }
        public void setTotalMembers(int totalMembers) { this.totalMembers = totalMembers; }
        public String getDeadline() { return deadline; }
        public void setDeadline(String deadline) { this.deadline = deadline; }
        public String getDetails() { return details; }
        public void setDetails(String details) { this.details = details; }
    }
}

