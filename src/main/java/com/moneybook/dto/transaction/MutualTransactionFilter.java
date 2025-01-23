package com.moneybook.dto.transaction;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MutualTransactionFilter {
    private String transactionType;
    private String transactionName;
    private String dateFrom; // Start date for filtering
    private String dateTo;   // End date for filtering
}
