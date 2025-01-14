package com.moneybook.controller;

import com.moneybook.dto.transaction.PersonalTransactionCreateDto;
import com.moneybook.dto.transaction.PersonalTransactionDto;
import com.moneybook.dto.transaction.PersonalTransactionUpdateDto;
import com.moneybook.exception.ResourceNotFoundException;
import com.moneybook.service.PersonalTransactionService;
import com.moneybook.util.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public ApiResponse<?> createTransaction(@Valid @RequestBody PersonalTransactionCreateDto personalTransactionCreateDto)
            throws ResourceNotFoundException {
        PersonalTransactionDto savedTransaction = personalTransactionService.
                savePersonalTransaction(personalTransactionCreateDto);

        return ApiResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CREATED.value())
                .message("Transaction created successfully")
                .data(savedTransaction)
                .build();
    }

    @GetMapping("/{transactionId}")
    public ApiResponse<?> getTransactionById(@PathVariable UUID transactionId) throws ResourceNotFoundException {
        PersonalTransactionDto transaction = personalTransactionService.getTransactionById(transactionId);
        if (transaction == null) {
            return ApiResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.NOT_FOUND.value())
                    .message("Transaction not found")
                    .build();
        }
        return ApiResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Transaction retrieved successfully")
                .data(transaction)
                .build();
    }

    @DeleteMapping("/{transactionId}")
    public ApiResponse<?> deleteTransaction(@PathVariable UUID transactionId) throws ResourceNotFoundException {
        PersonalTransactionDto deletedTransaction = personalTransactionService.deleteTransaction(transactionId);
        return ApiResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Transaction deleted successfully")
                .data(deletedTransaction)
                .build();
    }

    @PutMapping("/{transactionId}")
    public ApiResponse<?> updateTransaction(@PathVariable UUID transactionId,
                                            @Valid @RequestBody PersonalTransactionUpdateDto personalTransactionUpdateDto)
            throws ResourceNotFoundException {
        PersonalTransactionDto updatedTransaction = personalTransactionService
                .updateTransaction(transactionId, personalTransactionUpdateDto);
        return ApiResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Transaction updated successfully")
                .data(updatedTransaction)
                .build();
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<?> getAllTransactionsByUserId(@PathVariable String userId) throws ResourceNotFoundException {
        List<PersonalTransactionDto> transactions = personalTransactionService.getAllTransactionsByUserId(userId);
        if (transactions.isEmpty()) {
            return ApiResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.NOT_FOUND.value())
                    .message("No transactions found")
                    .build();
        }
        return ApiResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Transactions retrieved successfully")
                .data(transactions)
                .build();
    }
}
