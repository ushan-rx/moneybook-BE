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
public class FriendSummaryDto {
    private String friendName;
    private BigDecimal balance;
    private BigDecimal totalLent;
    private BigDecimal totalBorrowed;
    private OffsetDateTime friendsSince;
    private String avatar;
    private String friendID;

}

