package com.moneybook.controller;


import com.moneybook.dto.user.NormalUserCreateDto;
import com.moneybook.dto.user.NormalUserDto;
import com.moneybook.service.NormalUserService;
import com.moneybook.dto.api.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("${api.base-path}/users")
public class NormalUserController {

    @Autowired
    private NormalUserService userService;

    @PostMapping("/create-user")
    public ResponseEntity<ApiResponse<?>> createUser(@Valid @RequestBody NormalUserCreateDto normalUserCreateDto) {
        final NormalUserDto user = userService.saveNormalUser(normalUserCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CREATED.value())
                .message("User created successfully")
                .data(user)
                .build());
    }
}
