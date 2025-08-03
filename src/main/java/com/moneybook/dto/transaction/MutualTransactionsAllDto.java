package com.moneybook.dto.transaction;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.moneybook.model.enums.TransactionStatus;
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
public class MutualTransactionsAllDto {

    private UUID transactionID;
    private String transactionName;
    private BigDecimal amount;
    private OffsetDateTime transactionDate;
    private String transactionType;
    private String description;
    private String friendName;
    private TransactionStatus status;
}
