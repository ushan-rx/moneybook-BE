package com.moneybook.util;

import java.util.UUID;

public class GenerateQRPayload {
    public static String generate(UUID transactionId, String borrowerId, String otpHash) {
        return String.format("{\"transactionId\":\"%s\",\"borrowerId\":\"%s\",\"otpHash\":\"%s\"}",
                transactionId, borrowerId, otpHash);
    }


}
