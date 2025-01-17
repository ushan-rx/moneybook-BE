package com.moneybook.repository;

import com.moneybook.model.MutualTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MutualTransactionRepo extends JpaRepository<MutualTransaction, UUID> {
}
