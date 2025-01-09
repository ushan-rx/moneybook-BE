package com.moneybook.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "group_member")
public class GroupMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "membership_id")
    private Long membershipId;

    @Column(name = "group_id", unique = true)
    private String groupId;

    @Column(name = "user_id", unique = true)
    private String userId;

    @CreationTimestamp
    @Column(name = "joined_at", updatable = false, nullable = false)
    private OffsetDateTime joinedAt;
}
