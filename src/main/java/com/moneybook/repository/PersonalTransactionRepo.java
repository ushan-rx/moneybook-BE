package com.moneybook.repository;

import com.moneybook.model.PersonalTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PersonalTransactionRepo extends JpaRepository<PersonalTransaction, UUID>, JpaSpecificationExecutor<PersonalTransaction> {

}
