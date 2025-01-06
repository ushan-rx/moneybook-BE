package com.moneybook.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "normaluser")
public class NormalUser {

    @Id
    @NotBlank(message = "User ID cannot be blank")
    @NotNull(message = "User ID cannot be blank!")
    private String user_id;

    private String username;

    private String email;

    private String fname;

    private String lname;

    private String phone_no;

    private String address;

    private String profile_pic;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private OffsetDateTime created_at;

    private boolean status = true;

    @OneToMany(mappedBy = "created_by", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<FriendGroup> groups;
}
















