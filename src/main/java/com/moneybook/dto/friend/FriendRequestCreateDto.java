package com.moneybook.dto.friend;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendRequestCreateDto {
    @NotNull(message = "senderId cannot be blank")
    private String senderId;

    @NotNull(message = "receiverId cannot be blank")
    private String receiverId;

}
