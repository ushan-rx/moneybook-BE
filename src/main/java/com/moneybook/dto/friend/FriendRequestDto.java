package com.moneybook.dto.friend;

import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendRequestDto {
    private Long requestId;
    private String senderId;
    private String receiverId;
    private String status;
    private OffsetDateTime createdAt;
}
