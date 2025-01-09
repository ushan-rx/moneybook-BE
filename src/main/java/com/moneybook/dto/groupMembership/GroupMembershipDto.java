package com.moneybook.dto.groupMembership;

import lombok.*;

import java.time.OffsetDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupMembershipDto {
    private Long membershipId;
    private String groupId;
    private String userId;
    private OffsetDateTime joinedAt;
}
