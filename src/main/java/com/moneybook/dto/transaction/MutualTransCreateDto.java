package com.moneybook.dto.transaction;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.math.BigDecimal;

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

    @NotNull(message = "Transaction type cannot be null")
    @Pattern(regexp = "^(LOAN|BORROW)$", message = "Transaction type must be either Loan or Borrow")
    private String transactionType;

    @NotNull(message = "Borrower ID cannot be null")
    private String borrowerID;

    @NotNull(message = "Lender ID cannot be null")
    private String lenderID;
}
