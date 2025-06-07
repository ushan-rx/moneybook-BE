package com.moneybook.service.friend;

import com.moneybook.dto.friend.*;
import com.moneybook.dto.user.NormalUserBriefDto;
import com.moneybook.exception.ResourceNotFoundException;
import com.moneybook.mappers.FriendRequestMapper;
import com.moneybook.model.FriendRequest;
import com.moneybook.model.Friendship;
import com.moneybook.model.enums.FriendRequestStatus;
import com.moneybook.repository.FriendRequestRepo;
import com.moneybook.repository.FriendshipRepo;
import com.moneybook.service.NormalUserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
public class FriendshipService {

    private FriendshipRepo friendshipRepo;
    private FriendRequestRepo friendRequestRepo;
    private final FriendRequestMapper requestMapper;
    private final NormalUserService normalUserService;

    @Transactional
    public FriendRequestDto sendFriendRequest(FriendRequestCreateCancelDto friendRequestCreateDto) {
        String senderId = friendRequestCreateDto.getSenderId();
        String receiverId = friendRequestCreateDto.getReceiverId();

        if (friendshipRepo.existsByUserIdAndFriendId(senderId, receiverId)) {
            throw new IllegalArgumentException("A friendship already exists between the users.");
        }

        if (friendRequestRepo.existsBySenderIdAndReceiverId(senderId, receiverId) ||
                friendRequestRepo.existsBySenderIdAndReceiverId(receiverId, senderId)) {
            throw new IllegalArgumentException("A friend request is already pending between the users.");
        }
        FriendRequest request = requestMapper.toFriendRequest(friendRequestCreateDto);
        request.setStatus(FriendRequestStatus.PENDING);
        FriendRequest savedRequest = friendRequestRepo.save(request);
        return requestMapper.fromFriendRequest(savedRequest);
    }

    // get friend request by receiverId and senderId
    public FriendRequestDto getFriendRequestByReceiverIdAndSenderId(String receiverId) {
        String senderId = SecurityContextHolder.getContext().getAuthentication().getName();
        FriendRequest request = friendRequestRepo.findBySenderAndReceiver(senderId, receiverId).orElseThrow(() ->
                new IllegalArgumentException("Friend requests not found."));
        return requestMapper.fromFriendRequest(request);
    }

    // cancel friend request
    @Transactional
    public FriendRequestDto cancelFriendRequestById(Long requestId) throws ResourceNotFoundException {
        String authenticatedUserId = SecurityContextHolder.getContext().getAuthentication().getName();

        FriendRequest request = friendRequestRepo.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Friend request not found"));

        // Check if the authenticated user is the sender
        if (!authenticatedUserId.equals(request.getSenderId())) {
            throw new IllegalStateException("Only the sender can cancel the friend request");
        }

        // Create DTO before deleting for return value
        FriendRequestDto dto = requestMapper.fromFriendRequest(request);
        try {
            NormalUserBriefDto senderDetails = normalUserService.getUserBrief(request.getSenderId());
            dto.setSender(senderDetails);
        } catch (ResourceNotFoundException e) {
            dto.setSender(null);
        }

        friendRequestRepo.delete(request);
        return dto;
    }

    @Transactional
    public FriendRequestDto respondFriendRequest(FriendRequestResponseDto friendRequestResponseDto) {
        FriendRequest request = friendRequestRepo.
                findBySenderIdAndReceiverId(friendRequestResponseDto.getReceiverId(),
                        friendRequestResponseDto.getSenderId());
        if (request != null && friendRequestResponseDto.getStatus().equals("ACCEPTED")) {
            request.setStatus(FriendRequestStatus.ACCEPTED);
            FriendRequest acceptedRequest = friendRequestRepo.save(request);
            // Add to friendship table
            Friendship friendship1 = Friendship.builder()
                    .userId(friendRequestResponseDto.getSenderId())
                    .friendId(friendRequestResponseDto.getReceiverId())
                    .build();
            Friendship friendship2 = Friendship.builder()
                    .userId(friendRequestResponseDto.getReceiverId())
                    .friendId(friendRequestResponseDto.getSenderId())
                    .build();
            friendshipRepo.saveAll(Arrays.asList(friendship1, friendship2));

            return requestMapper.fromFriendRequest(acceptedRequest);
        } else if (request != null && friendRequestResponseDto.getStatus().equals("REJECTED")) {
            friendRequestRepo.delete(request);
            return requestMapper.fromFriendRequest(request);
        }
        throw new IllegalArgumentException("Friend request not found.");
    }

    @Transactional
    public FriendDto removeFriend(FriendshipRemoveDto friendshipRemoveDto) {
        String userId = friendshipRemoveDto.getUserId();
        String friendId = friendshipRemoveDto.getFriendId();
        if (friendshipRepo.existsByUserIdAndFriendId(userId, friendId)) {
            friendshipRepo.deleteByUserIdAndFriendId(userId, friendId);
            friendshipRepo.deleteByUserIdAndFriendId(friendId, userId);
            // Remove friend request if exists
            friendRequestRepo.deleteBySenderIdAndReceiverId(userId, friendId);
            friendRequestRepo.deleteBySenderIdAndReceiverId(friendId, userId);
            return FriendDto.builder().userId(friendId).build();
        }
        throw new IllegalArgumentException("Friend not found.");
    }

    public List<FriendDto> getFriends(String userId) {
        return friendshipRepo.findFriendsByUserId(userId);
    }

    public List<FriendRequestDto> getPendingFriendRequests() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (userId == null) {
            throw new IllegalStateException("User not authenticated");
        }
        List<FriendRequest> friendRequests = friendRequestRepo
                .findByReceiverIdAndStatus(userId, FriendRequestStatus.PENDING);
        return friendRequests.stream()
                .map(request -> {
                    FriendRequestDto dto = requestMapper.fromFriendRequest(request);
                    try {
                        NormalUserBriefDto senderDetails = normalUserService.getUserBrief(request.getSenderId());
                        dto.setSender(senderDetails);
                    } catch (ResourceNotFoundException e) {
                        dto.setSender(null);
                    }
                    return dto;
                })
                .toList();
    }
}
