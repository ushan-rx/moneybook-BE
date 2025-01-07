package com.moneybook.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
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
