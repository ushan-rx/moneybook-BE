package com.moneybook.service.transaction;

import com.moneybook.dto.transaction.PersonalTransactionCreateDto;
import com.moneybook.dto.transaction.PersonalTransactionDto;
import com.moneybook.dto.transaction.PersonalTransactionUpdateDto;
import com.moneybook.exception.ResourceNotFoundException;
import com.moneybook.mappers.PersonalTransactionMapper;
import com.moneybook.model.PersonalTransaction;
import com.moneybook.repository.NormalUserRepo;
import com.moneybook.repository.PersonalTransactionRepo;
import com.moneybook.util.FilterSpecification;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PersonalTransactionService {

    private final PersonalTransactionRepo repo;
    private final NormalUserRepo normalUserRepo;
    private final PersonalTransactionMapper mapper;

    @Transactional
    public PersonalTransactionDto savePersonalTransaction(PersonalTransactionCreateDto personalTransactionCreateDto)
            throws ResourceNotFoundException {
        String userId = personalTransactionCreateDto.getUserId();
        normalUserRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found"));

        PersonalTransaction transaction = mapper
                .toPersonalTransaction(personalTransactionCreateDto);

        // generate transactionId
        transaction.setTransactionId(UUID.randomUUID());

        PersonalTransaction savedTransaction = repo.saveAndFlush(transaction);
        return mapper.fromPersonalTransaction(savedTransaction);
    }

    @Transactional
    public PersonalTransactionDto getTransactionById(UUID transactionId) throws ResourceNotFoundException {
        PersonalTransaction transaction = repo.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction with id " + transactionId + " not found"));
        return mapper.fromPersonalTransaction(transaction);
    }

    @Transactional
    public PersonalTransactionDto deleteTransaction(UUID transactionId) throws ResourceNotFoundException {
        PersonalTransaction transaction = repo.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction with id " + transactionId + " not found"));
        repo.delete(transaction);
        return mapper.fromPersonalTransaction(transaction);
    }

    @Transactional
    public PersonalTransactionDto updateTransaction(UUID transactionId, PersonalTransactionUpdateDto updateDto)
            throws ResourceNotFoundException {
        // ** check if the user is authorized to update the transaction (add later) **

        PersonalTransaction existingTransaction = repo.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction with id " + transactionId + " not found"));
        mapper.updatePersonalTransactionFromDto(updateDto, existingTransaction);
        PersonalTransaction updatedTransaction = repo.save(existingTransaction);
        return mapper.fromPersonalTransaction(updatedTransaction);
    }

    @Transactional
    public Page<PersonalTransactionDto> getAllTransactionsByUserId(
            String userId,
            Map<String, String> filters,
            Pageable pageable) throws ResourceNotFoundException {
        normalUserRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found"));
        filters.put("userId", userId); //
        Specification<PersonalTransaction> specifications = new FilterSpecification<>(filters);

        return repo.findAll(specifications, pageable).map(mapper::fromPersonalTransaction);
    }

}
