package com.moneybook.dto.transaction;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MutualTransactionManual {
    @NotNull(message = "OTP cannot be null")
    String otp;
    @NotNull(message = "User ID cannot be null")
    String userID;
}
