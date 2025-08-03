package com.moneybook.repository;

import com.moneybook.dto.transaction.CategoryExpenseBriefDTO;
import com.moneybook.model.PersonalTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface PersonalTransactionRepo extends JpaRepository<PersonalTransaction, UUID>, JpaSpecificationExecutor<PersonalTransaction> {

    @Query("SELECT new com.moneybook.dto.transaction.CategoryExpenseBriefDTO(pt.category, SUM(pt.transactionAmount)) " +
            "FROM PersonalTransaction pt " +
            "WHERE pt.transactionType = 'Expense' " +
            "AND pt.transactionDate >= :dateFrom " +
            "AND pt.transactionDate <= :dateTo " +
            "AND pt.userId = :userId " +
            "GROUP BY pt.category " +
            "ORDER BY SUM(pt.transactionAmount) DESC")
            List<CategoryExpenseBriefDTO> getPersonalExpenseSummaryByCategory(@Param("dateFrom") OffsetDateTime dateFrom,
                                                                                @Param("dateTo") OffsetDateTime dateTo,
                                                                                @Param("userId") String userId);

    @Query("SELECT SUM(pt.transactionAmount) " +
            "FROM PersonalTransaction pt " +
            "WHERE pt.transactionType = 'Income' " +
            "AND pt.transactionDate >= :dateFrom " +
            "AND pt.transactionDate <= :dateTo " +
            "AND pt.userId = :userId")
    BigDecimal getTotalIncomeForDateRange(@Param("dateFrom") OffsetDateTime dateFrom,
                                        @Param("dateTo") OffsetDateTime dateTo,
                                        @Param("userId") String userId);

    @Query("SELECT SUM(pt.transactionAmount) " +
           "FROM PersonalTransaction pt " +
           "WHERE pt.transactionType = 'Expense' " +
           "AND pt.transactionDate >= :dateFrom " +
           "AND pt.transactionDate <= :dateTo " +
           "AND pt.userId = :userId")
    BigDecimal getTotalExpenseForDateRange(@Param("dateFrom") OffsetDateTime dateFrom,
                                         @Param("dateTo") OffsetDateTime dateTo,
                                         @Param("userId") String userId);

    @Query("SELECT COUNT(pt) " +
           "FROM PersonalTransaction pt " +
           "WHERE pt.userId = :userId " +
           "AND pt.transactionDate >= :dateFrom " +
           "AND pt.transactionDate <= :dateTo")
    Long countByUserIdAndDateRange(@Param("userId") String userId,
                                  @Param("dateFrom") OffsetDateTime dateFrom,
                                  @Param("dateTo") OffsetDateTime dateTo);
}
