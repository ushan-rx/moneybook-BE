package com.moneybook.dto.transaction;

import com.moneybook.model.enums.TransactionStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MutualTransactionManual {
    @NotNull(message = "OTP cannot be null")
    String otp;
    @NotNull(message = "Transaction status cannot be null")
    TransactionStatus status;
}
