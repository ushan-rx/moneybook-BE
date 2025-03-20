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
    private String userId;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private String profilePicture;
    private OffsetDateTime createdAt;
}
