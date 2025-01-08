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
    private Long membership_id;
    @Column(unique = true)
    private String group_id;
    @Column(unique = true)
    private String user_id;

    @CreationTimestamp
    @Column(name = "joined_at", updatable = false, nullable = false)
    private OffsetDateTime joined_at;
}
