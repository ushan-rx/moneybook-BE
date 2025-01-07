package com.moneybook.dto.groupMembership;

import lombok.*;

import java.time.OffsetDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupMembershipDto {
    private Long membership_id;
    private String group_id;
    private String user_id;
    private OffsetDateTime joined_at;
}
