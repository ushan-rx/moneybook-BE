package com.moneybook.util;

import java.util.UUID;

public class GenerateQRPayload {
    public static String generate(UUID transactionId, String otpHash) {
        return String.format("{\"transactionId\":\"%s\",\"otpHash\":\"%s\"}",
                transactionId, otpHash);
    }


}
