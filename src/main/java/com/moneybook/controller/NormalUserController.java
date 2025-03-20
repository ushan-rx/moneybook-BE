package com.moneybook.controller;


import com.moneybook.dto.api.ApiResponse;
import com.moneybook.dto.user.NormalUserCreateDto;
import com.moneybook.dto.user.NormalUserDto;
import com.moneybook.dto.user.NormalUserUpdateDto;
import com.moneybook.exception.ResourceNotFoundException;
import com.moneybook.service.NormalUserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/update-user/{userId}")
    public ResponseEntity<ApiResponse<?>> updateUser(
            @PathVariable String userId,
            @Valid @RequestBody NormalUserUpdateDto normalUserUpdateDto)
            throws ResourceNotFoundException {
        final NormalUserDto user = userService.updateNormalUser(userId, normalUserUpdateDto);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("User updated successfully")
                .data(user)
                .build());
    }
}
