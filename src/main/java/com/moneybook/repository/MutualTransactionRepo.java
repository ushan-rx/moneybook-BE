package com.moneybook.repository;

import com.moneybook.model.MutualTransaction;
import com.moneybook.model.enums.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface MutualTransactionRepo extends JpaRepository<MutualTransaction, UUID> {
    List<MutualTransaction> findByStatusAndExpiryDateBefore(TransactionStatus status, OffsetDateTime expiryDate);

    List<MutualTransaction> findByBorrowerIDOrLenderID(String userID, String userID1);

    List<MutualTransaction> findByBorrowerIDOrLenderIDAndStatus(String borrowerID, String lenderID, TransactionStatus status);
}
