package com.moneybook.dto.friend;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendRequestResponseDto {
    @NotNull(message = "senderId cannot be blank")
    private String senderId;

    @NotNull(message = "receiverId cannot be blank")
    private String receiverId;

    @Pattern(regexp = "accepted|rejected", message = "status must be 'accepted' or 'rejected'")
    private String status;
}
