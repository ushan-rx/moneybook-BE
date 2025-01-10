package com.moneybook.dto.friend;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendDto {
    private String userId;
    private String username;
}
