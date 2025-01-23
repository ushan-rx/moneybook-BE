package com.moneybook.controller;


import com.moneybook.dto.api.ApiResponse;
import com.moneybook.dto.transaction.*;
import com.moneybook.exception.InvalidOtpException;
import com.moneybook.exception.ResourceNotFoundException;
import com.moneybook.exception.UserMismatchException;
import com.moneybook.model.enums.TransactionStatus;
import com.moneybook.service.MutualTransactionService;
import com.moneybook.util.ApiUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("${api.base-path}/mutual-transactions")
@RequiredArgsConstructor
public class MutualTransactionController {

    private final MutualTransactionService mutualTransactionService;

    // Create a new transaction
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<MutualTransactionDto>> createTransaction(
            @Valid @RequestBody MutualTransCreateDto dto) {
        MutualTransactionDto response = mutualTransactionService.createTransaction(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<MutualTransactionDto>builder()
                        .status(HttpStatus.CREATED.value())
                        .timestamp(LocalDateTime.now())
                        .message("Transaction created successfully")
                        .data(response)
                        .build()
        );
    }

    // Validate QR data
    @GetMapping("/validate-qr")
    public ResponseEntity<ApiResponse<MutualTransactionDto>> validateQrData(
            @RequestParam UUID transactionID,
            @RequestParam String hashedOtp,
            @RequestParam String userID) throws UserMismatchException, InvalidOtpException, ResourceNotFoundException {
        MutualTransactionDto response = mutualTransactionService.validateQrData(transactionID, hashedOtp, userID);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<MutualTransactionDto>builder()
                        .status(HttpStatus.OK.value())
                        .timestamp(LocalDateTime.now())
                        .message("QR data validated successfully")
                        .data(response)
                        .build()
        );
    }

    // Accept transaction using OTP
    @PostMapping("/{transactionID}/accept/manual")
    public ResponseEntity<ApiResponse<MutualTransactionDto>> acceptTransactionManual(
            @PathVariable UUID transactionID,
            @Valid @RequestBody MutualTransactionManual manualData)
            throws UserMismatchException, InvalidOtpException, ResourceNotFoundException {
        MutualTransactionDto response = mutualTransactionService.acceptTransactionManual(transactionID, manualData);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<MutualTransactionDto>builder()
                        .status(HttpStatus.OK.value())
                        .timestamp(LocalDateTime.now())
                        .message("Transaction accepted successfully")
                        .data(response)
                        .build()
        );
    }

    // Accept transaction using QR code (hashed OTP)
    @PostMapping("/{transactionID}/accept/qr")
    public ResponseEntity<ApiResponse<MutualTransactionDto>> acceptTransactionQr(
            @PathVariable UUID transactionID,
            @Valid @RequestBody MutualTransactionQr qrData)
            throws UserMismatchException, InvalidOtpException, ResourceNotFoundException {
        MutualTransactionDto response = mutualTransactionService.acceptTransactionQr(transactionID, qrData);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<MutualTransactionDto>builder()
                        .status(HttpStatus.OK.value())
                        .timestamp(LocalDateTime.now())
                        .message("Transaction accepted successfully")
                        .data(response)
                        .build()
        );
    }

    // Reject transaction
    @PostMapping("/{transactionID}/reject")
    public ResponseEntity<ApiResponse<MutualTransactionDto>> rejectTransaction(
            @PathVariable UUID transactionID,
            @RequestBody Map<String, String> requestBody) throws UserMismatchException, ResourceNotFoundException {
        String userID = requestBody.get("userID");
        if (userID == null) {
            throw new IllegalArgumentException("User ID is required");
        }
        MutualTransactionDto response = mutualTransactionService.rejectTransaction(transactionID, userID);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<MutualTransactionDto>builder()
                        .status(HttpStatus.OK.value())
                        .timestamp(LocalDateTime.now())
                        .message("Transaction rejected successfully")
                        .data(response)
                        .build()
        );
    }

    // Cancel transaction
    @PostMapping("/{transactionID}/cancel")
    public ResponseEntity<ApiResponse<MutualTransactionDto>> cancelTransaction(
            @PathVariable UUID transactionID,
            @RequestBody Map<String, String> RequestData) throws UserMismatchException, ResourceNotFoundException {
        String userID = RequestData.get("userID");
        MutualTransactionDto response = mutualTransactionService.cancelTransaction(transactionID, userID);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<MutualTransactionDto>builder()
                        .status(HttpStatus.OK.value())
                        .timestamp(LocalDateTime.now())
                        .message("Transaction cancelled successfully")
                        .data(response)
                        .build()
        );
    }

    // Get user transactions with optional status & filtering
    @GetMapping("/{userID}/transactions")
    public ResponseEntity<ApiResponse<?>> getUserTransactions(
            @PathVariable String userID,
            @RequestParam(required = false) TransactionStatus status,
            MutualTransactionFilter filterRequest,
            Pageable pageable) {
        Map<String, String> filters = ApiUtil.getFilters(filterRequest); // generate filters
        Page<MutualTransactionDto> response = mutualTransactionService.getUserTransactions(userID, status, filters, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .timestamp(LocalDateTime.now())
                        .message("Transactions retrieved successfully")
                        .data(response.getContent()) // Send only the content
                        .pagination(ApiUtil.getPagination(response)) // Send pagination details
                        .build()
        );
    }
}
