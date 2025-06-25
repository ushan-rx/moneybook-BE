package com.moneybook.repository;

import com.moneybook.model.MutualTransaction;
import com.moneybook.model.enums.TransactionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface MutualTransactionRepo extends JpaRepository<MutualTransaction, UUID>, JpaSpecificationExecutor<MutualTransaction> {
    List<MutualTransaction> findByStatusAndExpiryDateBefore(TransactionStatus status, OffsetDateTime expiryDate);

    List<MutualTransaction> findByRequestedToAndStatus(String requestedTo, TransactionStatus status);

    Page<MutualTransaction> findByBorrowerIDOrLenderID(String borrowerID, String lenderID, Pageable pageable);

    Page<MutualTransaction> findByBorrowerIDOrLenderIDAndStatus(String borrowerID, String lenderID, TransactionStatus status, Pageable pageable);

    @Modifying
    @Query("UPDATE MutualTransaction mt SET mt.status = :status WHERE mt.status = :currentStatus AND mt.expiryDate < :currentDate")
    int batchUpdateExpiredTransactions(TransactionStatus status, TransactionStatus currentStatus, OffsetDateTime currentDate);

}
