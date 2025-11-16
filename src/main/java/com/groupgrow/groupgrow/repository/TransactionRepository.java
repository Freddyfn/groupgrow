package com.groupgrow.groupgrow.repository;

import com.groupgrow.groupgrow.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query(value = "SELECT COALESCE(SUM(t.amount), 0) FROM transactions t WHERE t.user_id = :userId AND t.type = :type AND t.status = 'completed'", nativeQuery = true)
    BigDecimal sumAmountByUserAndType(@Param("userId") Long userId, @Param("type") String type);

    @Query(value = "SELECT COALESCE(SUM(t.amount), 0) FROM transactions t WHERE t.user_id = :userId AND t.type = :type AND t.status = 'completed' AND t.created_at BETWEEN :start AND :end", nativeQuery = true)
    BigDecimal sumAmountByUserAndTypeBetween(@Param("userId") Long userId,
                                             @Param("type") String type,
                                             @Param("start") LocalDateTime start,
                                             @Param("end") LocalDateTime end);
    
    @Query(value = "SELECT COALESCE(SUM(t.amount), 0) FROM transactions t WHERE t.user_id = :userId AND t.group_id = :groupId AND t.type = :type AND t.status = 'completed'", nativeQuery = true)
    BigDecimal sumByUserAndGroupAndType(@Param("userId") Long userId, @Param("groupId") Long groupId, @Param("type") String type);
    
    List<Transaction> findByGroupId(Long groupId);

    @Query(value = """
            SELECT DATE_FORMAT(t.created_at, '%Y-%m') AS period, SUM(t.amount) AS total
            FROM transactions t
            WHERE t.user_id = :userId
              AND t.type = 'contribution'
              AND t.created_at >= :startDate
            GROUP BY period
            ORDER BY period
            """, nativeQuery = true)
    List<MonthlyContributionProjection> findMonthlyContributions(@Param("userId") Long userId,
                                                                 @Param("startDate") LocalDateTime startDate);

    interface MonthlyContributionProjection {
        String getPeriod();
        BigDecimal getTotal();
    }
}

