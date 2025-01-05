package com.moneybook.dto.user;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NormalUserBriefDto {


    private String username;

    private String email;

    private String fname;

    private String lname;

    private String profile_pic;
}
