package com.moneybook.dto.group;

import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupDto {
    private String groupId;
    private String groupName;
    private String groupDescription;
    private String ownerName;
    private OffsetDateTime createdAt;
}
