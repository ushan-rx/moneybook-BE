package com.moneybook.repository;

import com.moneybook.dto.transaction.MutualTransactionsAllDto;
import com.moneybook.model.MutualTransaction;
import com.moneybook.model.enums.TransactionStatus;
import com.moneybook.repository.custom.MutualTransactionRepoCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface MutualTransactionRepo extends JpaRepository<MutualTransaction, UUID>,
        JpaSpecificationExecutor<MutualTransaction>, MutualTransactionRepoCustom {

    List<MutualTransaction> findByStatusAndExpiryDateBefore(TransactionStatus status, OffsetDateTime expiryDate);

    List<MutualTransaction> findByRequestedToAndStatus(String requestedTo, TransactionStatus status);

    Page<MutualTransaction> findByBorrowerIDOrLenderID(String borrowerID, String lenderID, Pageable pageable);

    Page<MutualTransaction> findByBorrowerIDOrLenderIDAndStatus(String borrowerID, String lenderID, TransactionStatus status, Pageable pageable);

    @Modifying
    @Query("UPDATE MutualTransaction mt SET mt.status = :status WHERE mt.status = :currentStatus AND mt.expiryDate < :currentDate")
    int batchUpdateExpiredTransactions(TransactionStatus status, TransactionStatus currentStatus, OffsetDateTime currentDate);

    boolean existsByBorrowerIDOrLenderID(String userId, String userId1);

    @Query("SELECT new com.moneybook.dto.transaction.MutualTransactionsAllDto(" +
           "mt.transactionID, " +
           "mt.transactionName, " +
           "mt.amount, " +
           "mt.transactionDate, " +
           "CAST(mt.transactionType AS string), " +
           "mt.description, " +
           "CASE " +
           "    WHEN mt.lenderID = :userId THEN CONCAT(borrower.firstName, ' ', borrower.lastName) " +
           "    ELSE CONCAT(lender.firstName, ' ', lender.lastName) " +
           "END, " +
           "mt.status) " +
           "FROM MutualTransaction mt " +
           "LEFT JOIN NormalUser borrower ON mt.borrowerID = borrower.userId " +
           "LEFT JOIN NormalUser lender ON mt.lenderID = lender.userId " +
           "WHERE (mt.borrowerID = :userId OR mt.lenderID = :userId)")
    Page<MutualTransactionsAllDto> findAllTransactionsWithFriendNamesByUserId(@Param("userId") String userId, Pageable pageable);

    @Query("SELECT SUM(mt.amount) " +
           "FROM MutualTransaction mt " +
           "WHERE mt.lenderID = :userId " +
           "AND mt.status = 'ACCEPTED' " +
           "AND mt.transactionDate >= :dateFrom " +
           "AND mt.transactionDate <= :dateTo")
    BigDecimal getTotalLentByUser(@Param("userId") String userId,
                                  @Param("dateFrom") OffsetDateTime dateFrom,
                                  @Param("dateTo") OffsetDateTime dateTo);

    @Query("SELECT SUM(mt.amount) " +
           "FROM MutualTransaction mt " +
           "WHERE mt.borrowerID = :userId " +
           "AND mt.status = 'ACCEPTED' " +
           "AND mt.transactionDate >= :dateFrom " +
           "AND mt.transactionDate <= :dateTo")
    BigDecimal getTotalBorrowedByUser(@Param("userId") String userId,
                                      @Param("dateFrom") OffsetDateTime dateFrom,
                                      @Param("dateTo") OffsetDateTime dateTo);

    @Query("SELECT COUNT(mt) " +
           "FROM MutualTransaction mt " +
           "WHERE (mt.borrowerID = :userId OR mt.lenderID = :userId) " +
           "AND mt.status = 'PENDING'")
    Integer countPendingTransactionsByUser(@Param("userId") String userId);

    @Query("SELECT COUNT(mt) " +
           "FROM MutualTransaction mt " +
           "WHERE (mt.borrowerID = :userId OR mt.lenderID = :userId) " +
           "AND mt.transactionDate >= :dateFrom " +
           "AND mt.transactionDate <= :dateTo")
    Long countByUserAndDateRange(@Param("userId") String userId,
                                @Param("dateFrom") OffsetDateTime dateFrom,
                                @Param("dateTo") OffsetDateTime dateTo);
}
