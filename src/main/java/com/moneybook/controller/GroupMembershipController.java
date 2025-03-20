package com.moneybook.controller;

import com.moneybook.dto.groupMembership.GroupMemberCreateDto;
import com.moneybook.dto.groupMembership.GroupMembershipDto;
import com.moneybook.service.group.GroupMembershipService;
import com.moneybook.dto.api.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("${api.base-path}/group-memberships")
public class GroupMembershipController {
    @Autowired
    private GroupMembershipService service;

    @PostMapping("/join")
    public ResponseEntity<ApiResponse<?>> joinGroup(@RequestBody GroupMemberCreateDto groupMemberCreateDto) {
        final GroupMembershipDto addedMember = service.addGroupMember(groupMemberCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CREATED.value())
                .message("Group member added successfully")
                .data(addedMember)
                .build());
    }


}
