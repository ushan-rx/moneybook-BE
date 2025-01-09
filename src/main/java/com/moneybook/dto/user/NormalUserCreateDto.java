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

    @NotBlank(message = "Username cannot be blank")
    @NotNull(message = "Username cannot be blank!")
    @Size(min = 3, max = 45, message = "Username must be between 3 and 50 characters")
    @Pattern(
            regexp = "^[a-zA-Z0-9_.-]+$",
            message = "Username can only contain letters, numbers, dots, underscores, and hyphens"
    )
    private String username;

    @NotBlank(message = "Email cannot be blank")
    @NotNull(message = "Email cannot be blank!")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "First name cannot be blank")
    @NotNull(message = "First name cannot be blank!")
    @Size(max = 45, message = "First name must not exceed 50 characters")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    @NotNull(message = "Last name cannot be blank!")
    @Size(max = 45, message = "Last name must not exceed 50 characters")
    private String lastName;

    @NotBlank(message = "Phone number cannot be blank")
    @NotNull(message = "Phone number cannot be blank!")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number must be valid")
    private String phoneNumber;

    @NotBlank(message = "Address cannot be blank")
    private String address;

    @URL(protocol = "https")
    private String profilePicture;
}
