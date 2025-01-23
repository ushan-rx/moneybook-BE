package com.moneybook.dto.transaction;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MutualTransactionQr {
    @NotNull(message = "otp is required")
    String hashedOtp;
    @NotNull(message = "userID is required")
    String userID;
}
