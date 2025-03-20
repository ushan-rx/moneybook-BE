package com.moneybook.dto.transaction;

import jakarta.persistence.Column;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
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
public class MutualTransCreateDto {

    @NotNull(message = "Transaction name cannot be null")
    @Pattern(regexp = "^[a-zA-Z0-9 ]+$", message = "Transaction name must contain only alphanumeric characters")
    private String transactionName;

    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "1.0", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @Pattern(regexp = "^[a-zA-Z0-9 .]+$", message = "Description must contain only alphanumeric characters")
    @Max(value = 100, message = "Description must be less than 100 characters")
    private String description;

    @NotNull(message = "Transaction type cannot be null")
    @Pattern(regexp = "^(LOAN|BORROW)$", message = "Transaction type must be either Loan or Borrow")
    private String transactionType;

    @NotNull(message = "Transaction date cannot be null")
    private OffsetDateTime transactionDate;

    @NotNull(message = "Borrower ID cannot be null")
    private String borrowerID;

    @NotNull(message = "Lender ID cannot be null")
    private String lenderID;
}
