package com.moneybook.controller;

import com.moneybook.dto.transaction.PersonalTransactionCreateDto;
import com.moneybook.dto.transaction.PersonalTransactionDto;
import com.moneybook.dto.transaction.PersonalTransactionUpdateDto;
import com.moneybook.exception.ResourceNotFoundException;
import com.moneybook.service.PersonalTransactionService;
import com.moneybook.dto.api.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${api.base-path}/personal-transactions")
public class PersonalTransactionController {

    @Autowired
    private PersonalTransactionService personalTransactionService;

    @PostMapping("/create-transaction")
    public ResponseEntity<ApiResponse<?>> createTransaction(
            @Valid @RequestBody PersonalTransactionCreateDto personalTransactionCreateDto)
            throws ResourceNotFoundException {
        PersonalTransactionDto savedTransaction = personalTransactionService.
                savePersonalTransaction(personalTransactionCreateDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CREATED.value())
                .message("Transaction created successfully")
                .data(savedTransaction)
                .build());
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<ApiResponse<?>> getTransactionById(@PathVariable UUID transactionId)
            throws ResourceNotFoundException {
        PersonalTransactionDto transaction = personalTransactionService.getTransactionById(transactionId);
        if (transaction == null) {
            throw new ResourceNotFoundException("Transaction not found with ID: " + transactionId);
        }
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Transaction retrieved successfully")
                .data(transaction)
                .build());
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<ApiResponse<?>> deleteTransaction(@PathVariable UUID transactionId) throws ResourceNotFoundException {
        PersonalTransactionDto deletedTransaction = personalTransactionService.deleteTransaction(transactionId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Transaction deleted successfully")
                .data(deletedTransaction)
                .build());
    }

    @PutMapping("/{transactionId}")
    public ResponseEntity<ApiResponse<?>> updateTransaction(@PathVariable UUID transactionId,
                                            @Valid @RequestBody PersonalTransactionUpdateDto personalTransactionUpdateDto)
            throws ResourceNotFoundException {
        PersonalTransactionDto updatedTransaction = personalTransactionService
                .updateTransaction(transactionId, personalTransactionUpdateDto);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Transaction updated successfully")
                .data(updatedTransaction)
                .build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<?>> getAllTransactionsByUserId(@PathVariable String userId) throws ResourceNotFoundException {
        List<PersonalTransactionDto> transactions = personalTransactionService.getAllTransactionsByUserId(userId);
        if (transactions.isEmpty()) {
            throw new ResourceNotFoundException("No transactions found for user with ID: " + userId);
        }
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Transactions retrieved successfully")
                .data(transactions)
                .build());
    }
}
