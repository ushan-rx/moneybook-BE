package com.moneybook.dto.friend;

import com.moneybook.dto.user.NormalUserBriefDto;
import com.moneybook.model.enums.FriendRequestStatus;
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
    private FriendRequestStatus status;
    private OffsetDateTime createdAt;
    private NormalUserBriefDto sender;
}
