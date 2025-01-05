package com.moneybook.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@NoArgsConstructor
@Getter
@Setter
@ToString
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
}
