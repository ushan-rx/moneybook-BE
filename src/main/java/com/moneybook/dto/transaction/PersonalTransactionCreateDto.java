package com.moneybook.dto.transaction;

import com.moneybook.model.TransactionCategory;
import jakarta.persistence.Column;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PersonalTransactionCreateDto {

    @NotNull(message = "Transaction name cannot be null")
    private String transactionName;

    @NotNull(message = "Transaction type cannot be null")
    @Pattern(regexp = "^(Income|Expense)$", message = "Transaction type must be either Income or Expense")
    private String transactionType;

    @NotNull(message = "Category cannot be null")
    @Pattern(regexp = "^(" + TransactionCategory.TRANSACTION_TYPES_REGEX + ")$",
            message = "Category must be a valid category")
    private String category;

    @Size(max = 150, message = "Description must be less than 100 characters")
    private String description;

    @NotNull(message = "Transaction date cannot be null")
    private OffsetDateTime transactionDate;

    @NotNull(message = "Transaction amount cannot be null")
    @Column(name = "amount", precision = 10, scale = 2)
    @DecimalMin(value = "0.0", inclusive = false, message = "Transaction amount must be greater than 0")
    private BigDecimal transactionAmount;

    @NotNull(message = "User ID cannot be null")
    private String userId;
}
