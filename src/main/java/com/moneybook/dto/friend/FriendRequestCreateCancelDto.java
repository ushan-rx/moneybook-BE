package com.moneybook.dto.friend;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendRequestCreateCancelDto {
    @NotNull(message = "senderId cannot be blank")
    private String senderId;

    @NotNull(message = "receiverId cannot be blank")
    private String receiverId;

}
