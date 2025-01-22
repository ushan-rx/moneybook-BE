package com.moneybook.dto.transaction;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MutualTransactionFilter {
    private String transactionType;
    private String transactionDateFrom; // Start date for filtering
    private String transactionDateTo;   // End date for filtering
}
