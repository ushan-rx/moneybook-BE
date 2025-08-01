package com.moneybook.controller;

import com.moneybook.dto.api.ApiResponse;
import com.moneybook.dto.friend.*;
import com.moneybook.exception.ResourceNotFoundException;
import com.moneybook.service.friend.FriendshipService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("${api.base-path}/friends")
@AllArgsConstructor
public class FriendController {

    private FriendshipService friendshipService;

    @PostMapping("/sendFriendRequest")
    public ResponseEntity<ApiResponse<?>> sendFriendRequest(@Valid @RequestBody FriendRequestCreateCancelDto friendRequestCreateCancelDto) {
        FriendRequestDto sentRequest = friendshipService.sendFriendRequest(friendRequestCreateCancelDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CREATED.value())
                .message("Friend request sent successfully.")
                .data(sentRequest)
                .build());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/getFriendRequest/{userId}")
    public ResponseEntity<ApiResponse<?>> getFriendRequest(@PathVariable String userId) {
        FriendRequestDto friendRequest = friendshipService.getFriendRequestByReceiverIdAndSenderId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Friend request retrieved successfully.")
                .data(friendRequest)
                .build());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<?>> getPendingFriendRequests() {
        List<FriendRequestDto> friendRequests = friendshipService.getPendingFriendRequests();
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Friend requests retrieved successfully.")
                .data(friendRequests)
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

    @DeleteMapping("/request/{requestId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<?>> cancelFriendRequestById(@PathVariable Long requestId) throws ResourceNotFoundException {
        FriendRequestDto canceledRequest = friendshipService.cancelFriendRequestById(requestId);
        return ResponseEntity.ok(ApiResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Friend request canceled successfully.")
                .data(canceledRequest)
                .build());
    }

    @GetMapping("/{userId}/friendList")
    public ResponseEntity<ApiResponse<?>> getFriends(@PathVariable String userId) {
        List<FriendDto> friends = friendshipService.getFriends(userId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Friends retrieved successfully.")
                .data(friends)
                .build());
    }

    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<?>> searchFriends(@RequestParam String query) {
        String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<FriendBriefDto> friends = friendshipService.searchMyFriends(currentUserId, query);
        return ResponseEntity.ok(ApiResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Friends search completed successfully.")
                .data(friends)
                .build());
    }

    @GetMapping("/{friendshipId}/summary")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<?>> getFriendSummary(@PathVariable Long friendshipId) throws ResourceNotFoundException {
        FriendSummaryDto summary = friendshipService.getFriendSummary(friendshipId);
        return ResponseEntity.ok(ApiResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Friend summary retrieved successfully.")
                .data(summary)
                .build());
    }

    @GetMapping("/FriendDetailedList")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<?>> getAllFriendshipDetails(
            @RequestParam(required = false) String friendName,
            Pageable pageable) {
        String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();
        Page<FriendWithTransactionSummaryDto> friendships = friendshipService.getAllFriendshipDetails(currentUserId, friendName, pageable);
        return ResponseEntity.ok(ApiResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Friendship details retrieved successfully.")
                .data(friendships.getContent())
                .build());
    }
}