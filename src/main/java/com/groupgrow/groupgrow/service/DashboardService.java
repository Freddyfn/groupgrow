package com.groupgrow.groupgrow.service;

import com.groupgrow.groupgrow.dto.*;
import com.groupgrow.groupgrow.model.GroupDetails;
import com.groupgrow.groupgrow.model.Transaction;
import com.groupgrow.groupgrow.model.User;
import com.groupgrow.groupgrow.repository.GroupDetailsRepository;
import com.groupgrow.groupgrow.repository.GroupMemberRepository;
import com.groupgrow.groupgrow.repository.TransactionRepository;
import com.groupgrow.groupgrow.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupDetailsRepository groupDetailsRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private static final Locale LOCALE_ES = new Locale("es", "ES");

    public DashboardDTO getDashboardData(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        DashboardDTO dashboardData = new DashboardDTO();

        DashboardStats stats = buildStats(userId);
        dashboardData.setStats(stats);

        dashboardData.setPerformance(buildPerformance(userId));
        dashboardData.setPortfolio(buildPortfolio(stats));
        dashboardData.setGroups(buildUserGroups(userId));
        dashboardData.setAiTips(buildAiTips(user, stats));

        return dashboardData;
    }

    private DashboardStats buildStats(Long userId) {
        DashboardStats stats = new DashboardStats();

        BigDecimal totalSaved = transactionRepository.sumAmountByUserAndType(userId, "contribution");
        BigDecimal totalInvested = transactionRepository.sumAmountByUserAndType(userId, "investment");

        stats.setTotalSaved(totalSaved.doubleValue());
        stats.setTotalInvested(totalInvested.doubleValue());

        double monthlyGrowth = calculateMonthlyGrowth(userId);
        stats.setMonthlyGrowth(monthlyGrowth);

        int activeGroups = (int) groupMemberRepository.countDistinctByUserId(userId);
        stats.setActiveGroups(activeGroups);

        NextPaymentInfo nextPaymentInfo = buildNextPaymentInfo(userId);
        stats.setNextPaymentAmount(nextPaymentInfo.amount);
        stats.setNextPaymentDays(nextPaymentInfo.days);

        return stats;
    }

    private double calculateMonthlyGrowth(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime currentStart = now.minusDays(30);
        LocalDateTime previousStart = now.minusDays(60);

        BigDecimal current = transactionRepository.sumAmountByUserAndTypeBetween(
                userId,
                "contribution",
                currentStart,
                now);

        BigDecimal previous = transactionRepository.sumAmountByUserAndTypeBetween(
                userId,
                "contribution",
                previousStart,
                currentStart);

        if (previous.compareTo(BigDecimal.ZERO) == 0) {
            return current.compareTo(BigDecimal.ZERO) > 0 ? 100.0 : 0.0;
        }

        BigDecimal growth = current.subtract(previous)
                .divide(previous, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        return growth.doubleValue();
    }

    private NextPaymentInfo buildNextPaymentInfo(Long userId) {
        List<Long> groupIds = groupMemberRepository.findGroupIdsByUserId(userId);
        if (groupIds.isEmpty()) {
            return new NextPaymentInfo(0.0, 0);
        }

        List<GroupDetails> groups = groupDetailsRepository.findAllById(groupIds);
        BigDecimal totalMinimum = groups.stream()
                .map(GroupDetails::getMinimumContribution)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        LocalDate today = LocalDate.now();
        int nextDays = groups.stream()
                .map(GroupDetails::getDeadline)
                .filter(Objects::nonNull)
                .map(deadline -> (int) ChronoUnit.DAYS.between(today, deadline))
                .filter(days -> days >= 0)
                .min(Integer::compareTo)
                .orElse(0);

        return new NextPaymentInfo(totalMinimum.doubleValue(), nextDays);
    }

    private List<PerformanceDataPoint> buildPerformance(Long userId) {
        LocalDate startMonth = LocalDate.now().withDayOfMonth(1).minusMonths(5);
        LocalDateTime startDate = startMonth.atStartOfDay();
        List<TransactionRepository.MonthlyContributionProjection> projections =
                transactionRepository.findMonthlyContributions(userId, startDate);

        Map<String, BigDecimal> totalsByPeriod = projections.stream()
                .collect(Collectors.toMap(TransactionRepository.MonthlyContributionProjection::getPeriod,
                        TransactionRepository.MonthlyContributionProjection::getTotal));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM", LOCALE_ES);
        List<PerformanceDataPoint> points = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            LocalDate month = startMonth.plusMonths(i);
            String periodKey = month.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            BigDecimal total = totalsByPeriod.getOrDefault(periodKey, BigDecimal.ZERO);
            String label = formatter.format(month).replace(".", "");

            points.add(new PerformanceDataPoint(capitalize(label), total.doubleValue()));
        }

        return points;
    }

    private List<PortfolioItem> buildPortfolio(DashboardStats stats) {
        double saved = stats.getTotalSaved();
        double invested = stats.getTotalInvested();
        double total = saved + invested;

        if (total == 0) {
            return List.of(
                    new PortfolioItem("Ahorros", 0, "#8884d8"),
                    new PortfolioItem("Inversiones", 0, "#82ca9d")
            );
        }

        double savedPct = round(saved / total * 100);
        double investedPct = round(invested / total * 100);

        return List.of(
                new PortfolioItem("Ahorros", savedPct, "#8884d8"),
                new PortfolioItem("Inversiones", investedPct, "#82ca9d")
        );
    }

    private List<UserGroup> buildUserGroups(Long userId) {
        List<Long> groupIds = groupMemberRepository.findGroupIdsByUserId(userId);
        if (groupIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<GroupDetails> groups = groupDetailsRepository.findAllById(groupIds);
        return groups.stream()
                .map(group -> {
                    UserGroup dto = new UserGroup();
                    dto.setId(group.getId());
                    dto.setName(group.getName());
                    dto.setMembers((int) groupMemberRepository.countByGroupId(group.getId()));
                    dto.setTarget(group.getTargetAmount());
                    dto.setCurrent(group.getCurrentAmount());
                    dto.setMonthlyContribution(group.getMinimumContribution());
                    dto.setDeadline(group.getDeadline());
                    dto.setStatus("active");
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private List<String> buildAiTips(User user, DashboardStats stats) {
        List<String> tips = new ArrayList<>();
        tips.add(String.format(Locale.US,
                "%s, llevas ahorrado $%,.2f. Mantén el ritmo para alcanzar tus metas.",
                user.getFirstName(), stats.getTotalSaved()));
        tips.add(String.format(Locale.US,
                "Tienes %d grupo(s) activo(s). Revisa los plazos para no perder ningún aporte.",
                stats.getActiveGroups()));
        return tips;
    }

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return text.substring(0, 1).toUpperCase(LOCALE_ES) + text.substring(1).toLowerCase(LOCALE_ES);
    }

    private double round(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private record NextPaymentInfo(double amount, int days) {}
}
