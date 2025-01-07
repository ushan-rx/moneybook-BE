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
    private String group_id;

    @NotNull(message = "User ID cannot be blank!")
    private String user_id;

}
