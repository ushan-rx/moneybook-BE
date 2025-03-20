package com.moneybook.dto.group;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupCreateDto {

    @NotBlank(message = "Group name cannot be blank")
    private String userId;

    @NotBlank(message = "Group name cannot be blank")
    @Size(min = 3, max = 90, message = "Group name must be between 3 and 45 characters")
    @Pattern(
            regexp = "^[a-zA-Z0-9_ -]+$",
            message = "Group name can only contain letters, numbers, space, underscores, and hyphens"
    )
    private String groupName;

    @Pattern(
            regexp = "^[a-zA-Z0-9_ .-]+$",
            message = "Description can only contain letters, numbers, dots, underscores, and hyphens"
    )
    private String groupDescription;
}
