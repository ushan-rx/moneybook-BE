package com.moneybook.repository;

import com.moneybook.model.FriendRequest;
import com.moneybook.model.enums.FriendRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRequestRepo extends JpaRepository<FriendRequest, Long> {
    boolean existsBySenderIdAndReceiverId(String userId, String friendId);

    FriendRequest findBySenderIdAndReceiverId(String senderId, String receiverId);

    @Query("SELECT r FROM FriendRequest r WHERE (r.senderId = ?1 AND r.receiverId = ?2) OR (r.senderId = ?2 AND r.receiverId = ?1)")
    Optional<FriendRequest> findBySenderAndReceiver(String senderId, String receiverId);

    void deleteBySenderIdAndReceiverId(String userId, String friendId);

    List<FriendRequest> findByReceiverIdAndStatus(String receiverId, FriendRequestStatus status);
}
