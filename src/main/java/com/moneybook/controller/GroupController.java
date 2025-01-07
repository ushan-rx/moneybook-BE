package com.moneybook.controller;

import com.moneybook.dto.group.GroupCreateDto;
import com.moneybook.dto.group.GroupDto;
import com.moneybook.exception.ResourceNotFoundException;
import com.moneybook.service.GroupService;
import com.moneybook.util.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("${api.base-path}/group")
public class GroupController {

    @Autowired
    private GroupService service;

    @PostMapping("/create-group/{userId}")
    public ResponseEntity<ApiResponse<?>> createGroup(@PathVariable String userId, @Valid @RequestBody GroupCreateDto groupCreateDto) throws ResourceNotFoundException {
        final GroupDto group = service.saveGroup(userId, groupCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CREATED.value())
                .message("Group created successfully")
                .data(group)
                .build());
    }
}
