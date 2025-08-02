package com.moneybook.dto.transaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonalTransactionBriefDto {
    private List<CategoryExpenseBriefDTO> categoryExpenses;
    private BigDecimal totalEarnings;
    private BigDecimal totalSpends;
}
