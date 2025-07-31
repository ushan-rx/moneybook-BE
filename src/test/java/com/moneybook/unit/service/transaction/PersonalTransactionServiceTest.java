package com.moneybook.unit.service.transaction;

import com.moneybook.dto.transaction.PersonalTransactionCreateDto;
import com.moneybook.dto.transaction.PersonalTransactionDto;
import com.moneybook.exception.ResourceNotFoundException;
import com.moneybook.mappers.PersonalTransactionMapper;
import com.moneybook.model.PersonalTransaction;
import com.moneybook.repository.NormalUserRepo;
import com.moneybook.repository.PersonalTransactionRepo;
import com.moneybook.service.transaction.PersonalTransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PersonalTransactionServiceTest {

    @Mock
    private PersonalTransactionRepo personalTransactionRepo;
    @Mock
    private NormalUserRepo normalUserRepo;
    @Mock
    private PersonalTransactionMapper personalTransactionMapper;
    @InjectMocks
    private PersonalTransactionService personalTransactionService;

    @Test
    void savePersonalTransaction_Success() throws ResourceNotFoundException {
        // Arrange
        String userId = "user123";
        PersonalTransactionCreateDto createDto = PersonalTransactionCreateDto.builder()
                .userId(userId)
                .transactionName("Test transaction")
                .transactionType("Expense")
                .category("FOOD")
                .transactionDate(OffsetDateTime.now())
                .build();

        PersonalTransaction transaction = new PersonalTransaction();
        PersonalTransactionDto expectedDto = new PersonalTransactionDto();

        when(normalUserRepo.findById(userId)).thenReturn(Optional.of(new com.moneybook.model.NormalUser()));
        when(personalTransactionMapper.toPersonalTransaction(createDto)).thenReturn(transaction);
        when(personalTransactionRepo.saveAndFlush(any(PersonalTransaction.class))).thenReturn(transaction);
        when(personalTransactionMapper.fromPersonalTransaction(transaction)).thenReturn(expectedDto);

        // Act
        PersonalTransactionDto result = personalTransactionService.savePersonalTransaction(createDto);

        // Assert
        assertNotNull(result);
        assertEquals(expectedDto, result);
    }

    @Test
    void savePersonalTransaction_UserNotFound() {
        // Arrange
        String userId = "nonexistent";
        PersonalTransactionCreateDto createDto = PersonalTransactionCreateDto.builder()
                .userId(userId)
                .transactionName("Test transaction")
                .transactionType("Expense")
                .category("FOOD")
                .transactionDate(OffsetDateTime.now())
                .build();

        when(normalUserRepo.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> personalTransactionService.savePersonalTransaction(createDto));
    }

    @Test
    void getTransactionById_Success() throws ResourceNotFoundException {
        // Arrange
        UUID transactionId = UUID.randomUUID();
        PersonalTransaction transaction = new PersonalTransaction();
        PersonalTransactionDto expectedDto = new PersonalTransactionDto();

        when(personalTransactionRepo.findById(transactionId)).thenReturn(Optional.of(transaction));
        when(personalTransactionMapper.fromPersonalTransaction(transaction)).thenReturn(expectedDto);

        // Act
        PersonalTransactionDto result = personalTransactionService.getTransactionById(transactionId);

        // Assert
        assertNotNull(result);
        assertEquals(expectedDto, result);
    }

    @Test
    void getTransactionById_NotFound() {
        // Arrange
        UUID transactionId = UUID.randomUUID();
        when(personalTransactionRepo.findById(transactionId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> personalTransactionService.getTransactionById(transactionId));
    }
}
