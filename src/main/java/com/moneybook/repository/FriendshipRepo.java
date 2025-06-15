package com.moneybook.repository;

import com.moneybook.dto.friend.FriendBriefDto;
import com.moneybook.dto.friend.FriendDto;
import com.moneybook.model.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendshipRepo extends JpaRepository<Friendship, Long> {
    void deleteByUserIdAndFriendId(String userId, String friendId); // For deleting a friend

    @Query("SELECT new com.moneybook.dto.friend.FriendDto(u.userId, u.firstName, u.lastName) " +
            "FROM Friendship f " +
            "JOIN NormalUser u ON f.friendId = u.userId " +
            "WHERE f.userId = :userId")
    List<FriendDto> findFriendsByUserId(@Param("userId") String userId); // For fetching friends

    @Query("SELECT new com.moneybook.dto.friend.FriendBriefDto(u.userId, u.firstName, u.lastName, u.email, u.profilePicture) " +
           "FROM Friendship f " +
           "JOIN NormalUser u ON f.friendId = u.userId " +
           "WHERE f.userId = :userId AND " +
           "(LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<FriendBriefDto> searchFriendsByUsernameOrEmail(@Param("userId") String userId, @Param("searchTerm") String searchTerm);

    boolean existsByUserIdAndFriendId(String userId, String friendId);
}