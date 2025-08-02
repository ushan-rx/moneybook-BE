package com.moneybook.controller;

import com.moneybook.dto.api.ApiResponse;
import com.moneybook.dto.transaction.PersonalTransactionBriefDto;
import com.moneybook.dto.transaction.PersonalTransactionCreateDto;
import com.moneybook.dto.transaction.PersonalTransactionDto;
import com.moneybook.dto.transaction.PersonalTransactionUpdateDto;
import com.moneybook.dto.transaction.filters.PersonalTransactionFilter;
import com.moneybook.exception.ResourceNotFoundException;
import com.moneybook.service.transaction.PersonalTransactionService;
import com.moneybook.util.ApiUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("${api.base-path}/personal-transactions")
@CrossOrigin(origins = "http://localhost:3000")
public class PersonalTransactionController {

    @Autowired
    private PersonalTransactionService personalTransactionService;

    @PostMapping("/create")
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

    @GetMapping("/{userId}/transactions")
    public ResponseEntity<ApiResponse<?>> getAllTransactionsByUserId(
            @PathVariable String userId,
            @Valid @ModelAttribute PersonalTransactionFilter filterRequest,
            Pageable pageable) throws ResourceNotFoundException {
        Map<String, String> filters = ApiUtil.getFilters(filterRequest);
        Page<PersonalTransactionDto> response = personalTransactionService.getAllTransactionsByUserId(userId, filters, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Transactions retrieved successfully")
                .data(response.getContent())
                .pagination(ApiUtil.getPagination(response))
                .build());
    }

    @GetMapping("/brief")
    public ResponseEntity<ApiResponse<?>> getCategoryExpenseBrief(
            @RequestParam("fromDate") String fromDate,
            @RequestParam("toDate") String toDate) {

        var categoryExpenseBrief = personalTransactionService.getCategoryExpenseBrief(fromDate, toDate);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("Category expense brief retrieved successfully")
                .data(categoryExpenseBrief)
                .build());
    }
}
