package com.moneybook.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;


@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "friend_group")
public class FriendGroup {

    @Id
    @NotBlank(message = "Group ID cannot be blank")
    @NotNull(message = "Group ID cannot be blank!")
    private String group_id;
    private String group_name;
    private String group_description;
    private String ownerUsername;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private OffsetDateTime created_at;

    @ManyToOne(fetch = FetchType.LAZY)
    private NormalUser created_by; // This field stores the owner (NormalUser) of the group.
}
