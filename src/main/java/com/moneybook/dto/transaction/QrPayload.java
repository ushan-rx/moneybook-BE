package com.moneybook.dto.transaction;

import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class QrPayload {
    private UUID transactionID;
    private String otpHash;
}
