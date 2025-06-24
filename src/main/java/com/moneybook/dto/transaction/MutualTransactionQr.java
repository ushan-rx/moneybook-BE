package com.moneybook.dto.transaction;

import com.moneybook.model.enums.TransactionStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MutualTransactionQr {
    @NotNull(message = "otp is required")
    String hashedOtp;
    @NotNull(message = "Transaction status cannot be null")
    TransactionStatus status;
}
