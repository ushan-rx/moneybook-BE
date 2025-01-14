package com.moneybook.service;

import com.moneybook.dto.transaction.PersonalTransactionCreateDto;
import com.moneybook.dto.transaction.PersonalTransactionDto;
import com.moneybook.dto.transaction.PersonalTransactionUpdateDto;
import com.moneybook.exception.ResourceNotFoundException;
import com.moneybook.mappers.PersonalTransactionMapper;
import com.moneybook.model.PersonalTransaction;
import com.moneybook.repository.NormalUserRepo;
import com.moneybook.repository.PersonalTransactionRepo;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PersonalTransactionService {

    private PersonalTransactionRepo personalTransactionRepo;
    private NormalUserRepo normalUserRepo;

    @Transactional
    public PersonalTransactionDto savePersonalTransaction(PersonalTransactionCreateDto personalTransactionCreateDto)
            throws ResourceNotFoundException {
        String userId = personalTransactionCreateDto.getUserId();
        normalUserRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User with id "+ userId +" not found"));

        PersonalTransaction transaction = PersonalTransactionMapper.MAPPER
                .toPersonalTransaction(personalTransactionCreateDto);
        PersonalTransaction savedTransaction = personalTransactionRepo.saveAndFlush(transaction);
        return PersonalTransactionMapper.MAPPER.fromPersonalTransaction(savedTransaction);
    }

    @Transactional
    public PersonalTransactionDto getTransactionById(UUID transactionId) throws ResourceNotFoundException {
        PersonalTransaction transaction = personalTransactionRepo.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction with id "+ transactionId +" not found"));
        return PersonalTransactionMapper.MAPPER.fromPersonalTransaction(transaction);
    }

    @Transactional
    public PersonalTransactionDto deleteTransaction(UUID transactionId) throws ResourceNotFoundException {
        PersonalTransaction transaction = personalTransactionRepo.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction with id "+ transactionId +" not found"));
        personalTransactionRepo.delete(transaction);
        return PersonalTransactionMapper.MAPPER.fromPersonalTransaction(transaction);
    }

    @Transactional
    public PersonalTransactionDto updateTransaction(UUID transactionId, PersonalTransactionUpdateDto updateDto)
            throws ResourceNotFoundException {
        PersonalTransaction existingTransaction = personalTransactionRepo.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction with id "+ transactionId +" not found"));
        PersonalTransactionMapper.MAPPER.updatePersonalTransactionFromDto(updateDto, existingTransaction);
        PersonalTransaction updatedTransaction = personalTransactionRepo.save(existingTransaction);
        return PersonalTransactionMapper.MAPPER.fromPersonalTransaction(updatedTransaction);
    }

    @Transactional
    public List<PersonalTransactionDto> getAllTransactionsByUserId(String userId) throws ResourceNotFoundException {
        normalUserRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User with id "+ userId +" not found"));
        return personalTransactionRepo.findByUserId(userId);
    }

}
