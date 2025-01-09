package com.moneybook.model;

import jakarta.persistence.*;
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
    @Column(name = "group_id")
    private String groupId;

    @Column(name = "group_name")
    private String groupName;

    @Column(name = "group_description")
    private String groupDescription;

    private String ownerUsername;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private OffsetDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private NormalUser createdBy; // This field stores the owner (NormalUser) of the group.

}
