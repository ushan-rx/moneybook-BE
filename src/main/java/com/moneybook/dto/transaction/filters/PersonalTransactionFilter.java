package com.moneybook.dto.transaction.filters;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PersonalTransactionFilter {
    private String transactionType;
    @Pattern(regexp = "^(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(?:\\.\\d+)?(?:Z|[+-]\\d{2}:\\d{2})(?:\\[.*])?)$",
            message = "Transaction date must be in ISO 8601 format")
    private String transactionDate_lte;   // End date for filtering
    @Pattern(regexp = "^(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(?:\\.\\d+)?(?:Z|[+-]\\d{2}:\\d{2})(?:\\[.*])?)$",
            message = "Transaction date must be in ISO 8601 format")
    private String transactionDate_gte;   // start date for filtering
    @Pattern(regexp = "^(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(?:\\.\\d+)?(?:Z|[+-]\\d{2}:\\d{2})(?:\\[.*])?)$",
            message = "Transaction date must be in ISO 8601 format")
    private String transactionDate;   // exact date for filtering
    private String transactionName;
    private String category;
}
