package com.moneybook.dto.transaction;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MutualTransactionDto {

    private UUID transactionID;
    private String transactionName;
    private BigDecimal amount;
    private OffsetDateTime transactionDate;
    private String transactionType;
    private String description;
    private String borrowerID;
    private String lenderID;
    private String otp;
    private String status;
    private String requestedTo;
    private QrPayload qrPayload;
}
