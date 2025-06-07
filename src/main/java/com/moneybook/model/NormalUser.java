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
    @Column(name = "user_id")
    private String userId;

    private String email;

    @Column(name = "fname")
    private String firstName;

    @Column(name = "lname")
    private String lastName;

    @Column(name = "phone_no")
    private String phoneNumber;

    private String address;

    @Column(name = "profile_pic")
    private String profilePicture;

    @Column(name = "bio")
    private String bio;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private OffsetDateTime createdAt;

    private boolean status = true;

    @OneToMany(mappedBy = "createdBy", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    public List<FriendGroup> groups;
}
















