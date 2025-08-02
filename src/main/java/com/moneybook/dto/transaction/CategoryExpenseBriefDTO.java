package com.moneybook.dto.transaction;

import com.moneybook.model.enums.TransactionCategories;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryExpenseBriefDTO {
    private TransactionCategories category;
    private BigDecimal totalExpense;
}
