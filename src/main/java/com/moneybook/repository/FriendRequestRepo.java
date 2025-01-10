package com.moneybook.repository;

import com.moneybook.model.FriendRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendRequestRepo extends JpaRepository<FriendRequest, Long> {
    boolean existsBySenderIdAndReceiverId(String userId, String friendId);

    FriendRequest findBySenderIdAndReceiverId(String senderId, String receiverId);

    void deleteBySenderIdAndReceiverId(String userId, String friendId);
}
