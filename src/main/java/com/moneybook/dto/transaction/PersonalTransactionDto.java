package com.moneybook.dto.transaction;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonalTransactionDto {
    private String transactionId;
    private String transactionName;
    private String transactionType;
    private String category;
    private String description;
    private String transactionDate;
    @JsonSerialize(using = ToStringSerializer.class)
    private BigDecimal transactionAmount;
    private String userId;
}