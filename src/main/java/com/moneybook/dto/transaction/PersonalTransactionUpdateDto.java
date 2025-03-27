package com.moneybook.dto.transaction;

import com.moneybook.model.TransactionCategory;
import jakarta.persistence.Column;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PersonalTransactionUpdateDto {
    private String transactionName;
    @Pattern(regexp = "^(Income|Expense)$", message = "Transaction type must be either Income or Expense")
    private String transactionType;
    @Pattern(regexp = "^(" + TransactionCategory.TRANSACTION_TYPES_REGEX + ")$",
            message = "Category must be either Food, Transport, Shopping, Health, Entertainment, or Others")
    private String category;
    private String description;
    @NotNull(message = "Transaction date must be provided")
    private OffsetDateTime transactionDate;
    @Column(name = "amount", precision = 10, scale = 2)
    @DecimalMin(value = "0.0", inclusive = false, message = "Transaction amount must be greater than 0")
    private BigDecimal transactionAmount;
}
