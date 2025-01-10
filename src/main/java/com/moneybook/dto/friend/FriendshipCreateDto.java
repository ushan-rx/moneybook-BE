package com.moneybook.dto.friend;

import jakarta.validation.constraints.NotNull;
import lombok.*;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendshipCreateDto {
    @NotNull(message = "userId can not be null")
    private String userId;
    @NotNull(message = "userId can not be null")
    private String friendId;

}
