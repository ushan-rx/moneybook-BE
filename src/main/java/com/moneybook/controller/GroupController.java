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
@RequestMapping("${api.base-path}/groups")
public class GroupController {

    @Autowired
    private GroupService service;

    @PostMapping("/create-group")
    public ResponseEntity<ApiResponse<?>> createGroup( @Valid @RequestBody GroupCreateDto groupCreateDto) throws ResourceNotFoundException {
        final GroupDto group = service.saveGroup(groupCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CREATED.value())
                .message("Group created successfully")
                .data(group)
                .build());
    }
}
