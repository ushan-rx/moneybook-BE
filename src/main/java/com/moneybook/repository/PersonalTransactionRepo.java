package com.moneybook.repository;

import com.moneybook.dto.transaction.PersonalTransactionDto;
import com.moneybook.model.PersonalTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PersonalTransactionRepo extends JpaRepository<PersonalTransaction, UUID> {

    List<PersonalTransactionDto> findByUserId(String userId);
}
