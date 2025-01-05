package com.moneybook.dto.user;

import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NormalUserDto {

    private String user_id;
    private String username;
    private String email;
    private String fname;
    private String lname;
    private String phone_no;
    private String address;
    private String profile_pic;
    private OffsetDateTime created_at;
}
