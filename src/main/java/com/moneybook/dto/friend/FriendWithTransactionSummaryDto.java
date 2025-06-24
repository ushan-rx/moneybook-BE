package com.moneybook.dto.friend;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FriendWithTransactionSummaryDto {
    private String friendName;
    private String avatar;
    private String friendshipId;
    private String lastTransactionName;
    private String lastTransactionType;
    private BigDecimal lastTransactionAmount;
    private OffsetDateTime lastTransactionDate;

}


