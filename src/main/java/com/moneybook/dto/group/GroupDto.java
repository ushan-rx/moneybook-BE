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

    private String group_id;
    private String group_name;
    private String group_description;
    private String  ownerUsername;
    private OffsetDateTime created_at;
}
