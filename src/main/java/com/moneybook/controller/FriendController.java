package com.moneybook.controller;

import com.moneybook.dto.friend.*;
import com.moneybook.service.FriendshipService;
import com.moneybook.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("${api.base-path}/friends")
@AllArgsConstructor
public class FriendController {

    private FriendshipService friendshipService;

    @PostMapping("/sendFriendRequest")
    public ResponseEntity<ApiResponse<?>> sendFriendRequest(@Valid @RequestBody FriendRequestCreateDto friendRequestCreateDto) {
        FriendRequestDto sentRequest = friendshipService.sendFriendRequest(friendRequestCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CREATED.value())
                .message("Friend request sent successfully.")
                .data(sentRequest)
                .build());
    }

    @PostMapping("/respondFriendRequest")
    public ResponseEntity<ApiResponse<?>> respondFriendRequest(@Valid @RequestBody FriendRequestResponseDto friendRequestResponseDto) {
        FriendRequestDto respondedRequest = friendshipService.respondFriendRequest(friendRequestResponseDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CREATED.value())
                .message("Friend request responded successfully.")
                .data(respondedRequest)
                .build());
    }

    @PostMapping("/removeFriend")
    public ResponseEntity<ApiResponse<?>> removeFriend(@Valid @RequestBody FriendshipRemoveDto friendshipRemoveDto) {
        FriendDto removedFriend = friendshipService.removeFriend(friendshipRemoveDto);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Friend removed successfully.")
                .data(removedFriend)
                .build());
    }

    @GetMapping("/getFriends/{userId}")
    public ResponseEntity<ApiResponse<?>> getFriends(@PathVariable String userId) {
        List<FriendDto> friends = friendshipService.getFriends(userId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Friends retrieved successfully.")
                .data(friends)
                .build());
    }
}
