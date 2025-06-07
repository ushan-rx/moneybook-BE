package com.moneybook.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NormalUserBriefDto {
    private String userId;
    private String email;
    private String firstName;
    private String lastName;
    private String profilePicture;
    private String bio;
}
