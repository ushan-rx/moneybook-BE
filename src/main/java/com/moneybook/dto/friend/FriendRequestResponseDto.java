package com.moneybook.dto.friend;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendRequestResponseDto {
    private String senderId;
    private String receiverId;
    private String status;
}
