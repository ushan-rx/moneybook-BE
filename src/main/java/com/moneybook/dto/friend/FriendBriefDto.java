package com.moneybook.dto.friend;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FriendBriefDto {
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String profilePicture;
}
