package com.moneybook.dto.ai;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionSummaryDto {

    // Personal Transaction Summary
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal netSavings;
    private Map<String, BigDecimal> expenseByCategory;
    private Map<String, BigDecimal> incomeByCategory;

    // Mutual Transaction Summary
    private BigDecimal totalLent;
    private BigDecimal totalBorrowed;
    private BigDecimal netMutualBalance; // positive = more lent, negative = more borrowed
    private Integer pendingTransactions;

    // Analysis Period
    private OffsetDateTime analysisFromDate;
    private OffsetDateTime analysisToDate;
    private Long totalTransactionCount;
}
