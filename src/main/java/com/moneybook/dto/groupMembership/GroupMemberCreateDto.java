package com.moneybook.dto.groupMembership;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupMemberCreateDto {
    @NotNull(message = "Group ID cannot be blank!")
    private String groupId;

    @NotNull(message = "User ID cannot be blank!")
    private String userId;

}
