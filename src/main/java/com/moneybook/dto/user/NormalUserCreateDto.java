package com.moneybook.dto.user;

import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NormalUserCreateDto {

    @NotBlank(message = "User ID cannot be blank")
    @NotNull(message = "User ID cannot be blank!")
    private String userId;

    @NotBlank(message = "Email cannot be blank")
    @NotNull(message = "Email cannot be blank!")
    @Email(message = "Email should be valid")
    private String email;

    @URL(protocol = "https")
    private String profilePicture;
}
