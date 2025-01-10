package com.moneybook.service;

import com.moneybook.dto.friend.*;
import com.moneybook.mappers.FriendRequestMapper;
import com.moneybook.model.FriendRequest;
import com.moneybook.model.Friendship;
import com.moneybook.repository.FriendRequestRepo;
import com.moneybook.repository.FriendshipRepo;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
public class FriendshipService {

    private FriendshipRepo friendshipRepo;
    private FriendRequestRepo friendRequestRepo;

    @Transactional
    public FriendRequestDto sendFriendRequest(FriendRequestCreateDto friendRequestCreateDto) {
        String senderId = friendRequestCreateDto.getSenderId();
        String receiverId = friendRequestCreateDto.getReceiverId();

        if (friendshipRepo.existsByUserIdAndFriendId(senderId, receiverId)) {
            throw new IllegalArgumentException("A friendship already exists between the users.");
        }

        if (friendRequestRepo.existsBySenderIdAndReceiverId(senderId, receiverId) ||
                friendRequestRepo.existsBySenderIdAndReceiverId(receiverId, senderId)) {
            throw new IllegalArgumentException("A friend request is already pending between the users.");
        }
        FriendRequest request = FriendRequestMapper.MAPPER.toFriendRequest(friendRequestCreateDto);
        request.setStatus("pending");
        FriendRequest savedRequest = friendRequestRepo.save(request);
        return FriendRequestMapper.MAPPER.fromFriendRequest(savedRequest);
    }

    @Transactional
    public FriendRequestDto respondFriendRequest(FriendRequestResponseDto friendRequestResponseDto) {
        FriendRequest request = friendRequestRepo.
                findBySenderIdAndReceiverId(friendRequestResponseDto.getReceiverId(),
                        friendRequestResponseDto.getSenderId());
        if (request != null && friendRequestResponseDto.getStatus().equals("accepted")) {
            request.setStatus("accepted");
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

            return FriendRequestMapper.MAPPER.fromFriendRequest(acceptedRequest);
        } else if (request != null && friendRequestResponseDto.getStatus().equals("rejected")) {
            friendRequestRepo.delete(request);
            return FriendRequestMapper.MAPPER.fromFriendRequest(request);
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

}
