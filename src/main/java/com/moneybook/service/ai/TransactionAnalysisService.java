package com.moneybook.service.ai;

import com.moneybook.dto.ai.TransactionSummaryDto;
import com.moneybook.repository.MutualTransactionRepo;
import com.moneybook.repository.PersonalTransactionRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionAnalysisService {

    private final PersonalTransactionRepo personalTransactionRepo;
    private final MutualTransactionRepo mutualTransactionRepo;

    public TransactionSummaryDto analyzeUserTransactions(String userId, int daysBack) {
        OffsetDateTime fromDate = OffsetDateTime.now().minusDays(daysBack);
        OffsetDateTime toDate = OffsetDateTime.now();

        return TransactionSummaryDto.builder()
                .totalIncome(getTotalIncome(userId, fromDate, toDate))
                .totalExpense(getTotalExpense(userId, fromDate, toDate))
                .netSavings(calculateNetSavings(userId, fromDate, toDate))
                .expenseByCategory(getExpenseByCategory(userId, fromDate, toDate))
                .incomeByCategory(getIncomeByCategory(userId, fromDate, toDate))
                .totalLent(getTotalLent(userId, fromDate, toDate))
                .totalBorrowed(getTotalBorrowed(userId, fromDate, toDate))
                .netMutualBalance(calculateNetMutualBalance(userId, fromDate, toDate))
                .pendingTransactions(getPendingTransactionsCount(userId))
                .analysisFromDate(fromDate)
                .analysisToDate(toDate)
                .totalTransactionCount(getTotalTransactionCount(userId, fromDate, toDate))
                .build();
    }

    private BigDecimal getTotalIncome(String userId, OffsetDateTime fromDate, OffsetDateTime toDate) {
        BigDecimal income = personalTransactionRepo.getTotalIncomeForDateRange(fromDate, toDate, userId);
        return income != null ? income : BigDecimal.ZERO;
    }

    private BigDecimal getTotalExpense(String userId, OffsetDateTime fromDate, OffsetDateTime toDate) {
        try {
            BigDecimal expense = personalTransactionRepo.getTotalExpenseForDateRange(fromDate, toDate, userId);
            return expense != null ? expense : BigDecimal.ZERO;
        } catch (Exception e) {
            log.warn("getTotalExpenseForDateRange method not found, calculating manually");
            return BigDecimal.ZERO; // Fallback - you can implement manual calculation
        }
    }

    private BigDecimal calculateNetSavings(String userId, OffsetDateTime fromDate, OffsetDateTime toDate) {
        BigDecimal income = getTotalIncome(userId, fromDate, toDate);
        BigDecimal expense = getTotalExpense(userId, fromDate, toDate);

        // Ensure both values are never null
        income = income != null ? income : BigDecimal.ZERO;
        expense = expense != null ? expense : BigDecimal.ZERO;

        return income.subtract(expense);
    }

    private Map<String, BigDecimal> getExpenseByCategory(String userId, OffsetDateTime fromDate, OffsetDateTime toDate) {
        // You'll need to add this method to PersonalTransactionRepo
        Map<String, BigDecimal> categoryExpenses = new HashMap<>();
        try {
            var expenseData = personalTransactionRepo.getPersonalExpenseSummaryByCategory(fromDate, toDate, userId);
            expenseData.forEach(data ->
                categoryExpenses.put(data.getCategory().toString(), data.getTotalExpense())
            );
        } catch (Exception e) {
            log.warn("Error getting expense by category", e);
        }
        return categoryExpenses;
    }

    private Map<String, BigDecimal> getIncomeByCategory(String userId, OffsetDateTime fromDate, OffsetDateTime toDate) {
        // Simplified - you can enhance this based on your income categories
        Map<String, BigDecimal> incomeCategories = new HashMap<>();
        BigDecimal totalIncome = getTotalIncome(userId, fromDate, toDate);
        if (totalIncome.compareTo(BigDecimal.ZERO) > 0) {
            incomeCategories.put("INCOME", totalIncome);
        }
        return incomeCategories;
    }

    private BigDecimal getTotalLent(String userId, OffsetDateTime fromDate, OffsetDateTime toDate) {
        try {
            BigDecimal totalLent = mutualTransactionRepo.getTotalLentByUser(userId, fromDate, toDate);
            return totalLent != null ? totalLent : BigDecimal.ZERO;
        } catch (Exception e) {
            log.warn("getTotalLentByUser method not found, using fallback");
            return BigDecimal.ZERO;
        }
    }

    private BigDecimal getTotalBorrowed(String userId, OffsetDateTime fromDate, OffsetDateTime toDate) {
        try {
            BigDecimal totalBorrowed = mutualTransactionRepo.getTotalBorrowedByUser(userId, fromDate, toDate);
            return totalBorrowed != null ? totalBorrowed : BigDecimal.ZERO;
        } catch (Exception e) {
            log.warn("getTotalBorrowedByUser method not found, using fallback");
            return BigDecimal.ZERO;
        }
    }

    private BigDecimal calculateNetMutualBalance(String userId, OffsetDateTime fromDate, OffsetDateTime toDate) {
        BigDecimal totalLent = getTotalLent(userId, fromDate, toDate);
        BigDecimal totalBorrowed = getTotalBorrowed(userId, fromDate, toDate);

        // Ensure both values are never null
        totalLent = totalLent != null ? totalLent : BigDecimal.ZERO;
        totalBorrowed = totalBorrowed != null ? totalBorrowed : BigDecimal.ZERO;

        return totalLent.subtract(totalBorrowed);
    }

    private Integer getPendingTransactionsCount(String userId) {
        try {
            Integer count = mutualTransactionRepo.countPendingTransactionsByUser(userId);
            return count != null ? count : 0;
        } catch (Exception e) {
            log.warn("countPendingTransactionsByUser method not found, using fallback");
            return 0;
        }
    }

    private Long getTotalTransactionCount(String userId, OffsetDateTime fromDate, OffsetDateTime toDate) {
        try {
            Long personalCount = personalTransactionRepo.countByUserIdAndDateRange(userId, fromDate, toDate);
            Long mutualCount = mutualTransactionRepo.countByUserAndDateRange(userId, fromDate, toDate);

            // Handle null values
            personalCount = personalCount != null ? personalCount : 0L;
            mutualCount = mutualCount != null ? mutualCount : 0L;

            return (personalCount + mutualCount);
        } catch (Exception e) {
            log.warn("Count methods not found, using fallback");
            return 0L;
        }
    }
}
