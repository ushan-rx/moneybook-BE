package com.moneybook.dto.transaction;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PersonalTransactionFilter {
    private String transactionType;
    @Pattern(regexp = "^(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(?:\\.\\d+)?(?:Z|[+-]\\d{2}:\\d{2})(?:\\[.*])?)$",
            message = "Transaction date must be in ISO 8601 format")
    private String dateFrom; // Start date for filtering
    @Pattern(regexp = "^(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(?:\\.\\d+)?(?:Z|[+-]\\d{2}:\\d{2})(?:\\[.*])?)$",
            message = "Transaction date must be in ISO 8601 format")
    private String dateTo;   // End date for filtering
    private String transactionName;
    private String category;
}
