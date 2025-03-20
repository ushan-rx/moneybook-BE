package com.moneybook.dto.user;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NormalUserBriefDto {
    private String email;
    private String firstName;
    private String lastName;
    private String profilePicture;
}
