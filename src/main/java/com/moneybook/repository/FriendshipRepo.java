package com.moneybook.repository;

import com.moneybook.dto.friend.FriendBriefDto;
import com.moneybook.dto.friend.FriendDto;
import com.moneybook.dto.friend.FriendSummaryDto;
import com.moneybook.dto.friend.FriendWithTransactionSummaryDto;
import com.moneybook.model.Friendship;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query("SELECT new com.moneybook.dto.friend.FriendWithTransactionSummaryDto(" +
            "   CONCAT(u.firstName, ' ', u.lastName), " +
            "   u.profilePicture, " +
            "   CAST(f.friendshipId AS string), " +
            "   mt.transactionName, " +
            "   CAST(mt.transactionType AS string), " +
            "   mt.amount, " +
            "   mt.transactionDate" +
            ") " +
            "FROM Friendship f " +
            "JOIN NormalUser u ON f.friendId = u.userId " +
            "LEFT JOIN MutualTransaction mt ON ((mt.borrowerID = f.userId AND mt.lenderID = f.friendId) OR (mt.borrowerID = f.friendId AND mt.lenderID = f.userId)) " +
            "AND mt.transactionDate = (" +
            "   SELECT MAX(mt2.transactionDate) " +
            "   FROM MutualTransaction mt2 " +
            "   WHERE (mt2.borrowerID = f.userId AND mt2.lenderID = f.friendId) OR (mt2.borrowerID = f.friendId AND mt2.lenderID = f.userId)" +
            ") " +
            "WHERE f.userId = :userId " +
            "ORDER BY mt.transactionDate DESC NULLS LAST")
    Page<FriendWithTransactionSummaryDto> findAllFriendshipDetailsByUserId(
            @Param("userId") String userId,
            Pageable pageable);

    @Query("SELECT new com.moneybook.dto.friend.FriendWithTransactionSummaryDto(" +
            "   CONCAT(u.firstName, ' ', u.lastName), " +
            "   u.profilePicture, " +
            "   CAST(f.friendshipId AS string), " +
            "   mt.transactionName, " +
            "   CAST(mt.transactionType AS string), " +
            "   mt.amount, " +
            "   mt.transactionDate" +
            ") " +
            "FROM Friendship f " +
            "JOIN NormalUser u ON f.friendId = u.userId " +
            "LEFT JOIN MutualTransaction mt ON ((mt.borrowerID = f.userId AND mt.lenderID = f.friendId) OR (mt.borrowerID = f.friendId AND mt.lenderID = f.userId)) " +
            "AND mt.transactionDate = (" +
            "   SELECT MAX(mt2.transactionDate) " +
            "   FROM MutualTransaction mt2 " +
            "   WHERE (mt2.borrowerID = f.userId AND mt2.lenderID = f.friendId) OR (mt2.borrowerID = f.friendId AND mt2.lenderID = f.userId)" +
            ") " +
            "WHERE f.userId = :userId AND LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :friendName, '%')) " +
            "ORDER BY mt.transactionDate DESC NULLS LAST")
    Page<FriendWithTransactionSummaryDto> findAllFriendshipDetailsByUserIdAndFriendName(
            @Param("userId") String userId,
            @Param("friendName") String friendName,
            Pageable pageable);


    @Query("SELECT new com.moneybook.dto.friend.FriendSummaryDto(" +
            "CONCAT(u.firstName, ' ', u.lastName), " +
            "fb.balanceAmount, " +
            "(SELECT COALESCE(SUM(mt.amount), 0) FROM MutualTransaction mt WHERE mt.lenderID = f.userId AND mt.borrowerID = f.friendId AND mt.status = 'ACCEPTED'), " +
            "(SELECT COALESCE(SUM(mt.amount), 0) FROM MutualTransaction mt WHERE mt.borrowerID = f.userId AND mt.lenderID = f.friendId AND mt.status = 'ACCEPTED'), " +
            "f.createdAt, " +
            "u.profilePicture, " +
            "f.friendId) " +
            "FROM Friendship f " +
            "JOIN NormalUser u ON f.friendId = u.userId " +
            "LEFT JOIN FriendBalance fb ON (f.userId = fb.user1Id AND f.friendId = fb.user2Id) " +
            "WHERE f.friendshipId = :friendshipId")
    FriendSummaryDto getFriendSummaryByFriendshipId(@Param("friendshipId") Long friendshipId);
}
