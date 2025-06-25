package com.moneybook.controller;


import com.moneybook.dto.api.ApiResponse;
import com.moneybook.dto.transaction.MutualTransCreateDto;
import com.moneybook.dto.transaction.MutualTransactionDto;
import com.moneybook.dto.transaction.MutualTransactionManual;
import com.moneybook.dto.transaction.MutualTransactionQr;
import com.moneybook.dto.transaction.filters.MutualTransactionFilter;
import com.moneybook.exception.InvalidOtpException;
import com.moneybook.exception.ResourceNotFoundException;
import com.moneybook.exception.UserMismatchException;
import com.moneybook.service.transaction.MutualTransactionService;
import com.moneybook.util.ApiUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("${api.base-path}/mutual-transactions")
@RequiredArgsConstructor
public class MutualTransactionController {

    private final MutualTransactionService mutualTransactionService;

    // Create a new transaction
    @PostMapping({"", "/"})
    public ResponseEntity<ApiResponse<MutualTransactionDto>> saveTransaction(
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
    public ResponseEntity<ApiResponse<MutualTransactionDto>> validateQR(
            @RequestParam UUID transactionID,
            @RequestParam String hashedOtp) throws UserMismatchException, InvalidOtpException, ResourceNotFoundException {
        MutualTransactionDto response = mutualTransactionService.validateQrData(transactionID, hashedOtp);
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
    @PostMapping("/{transactionID}/manual")
    public ResponseEntity<ApiResponse<MutualTransactionDto>> respondTransactionManual(
            @PathVariable UUID transactionID,
            @Valid @RequestBody MutualTransactionManual manualData)
            throws UserMismatchException, InvalidOtpException, ResourceNotFoundException {
        MutualTransactionDto response = mutualTransactionService.transactionManualRespond(transactionID, manualData);
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
    @PostMapping("/{transactionID}/qr")
    public ResponseEntity<ApiResponse<MutualTransactionDto>> respondTransactionQr(
            @PathVariable UUID transactionID,
            @Valid @RequestBody MutualTransactionQr qrData)
            throws UserMismatchException, InvalidOtpException, ResourceNotFoundException {
        MutualTransactionDto response = mutualTransactionService.transactionQrRespond(transactionID, qrData);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<MutualTransactionDto>builder()
                        .status(HttpStatus.OK.value())
                        .timestamp(LocalDateTime.now())
                        .message("Transaction accepted successfully")
                        .data(response)
                        .build()
        );
    }

    // Cancel transaction
    @PostMapping("/{transactionID}/cancel")
    public ResponseEntity<ApiResponse<MutualTransactionDto>> cancelTransaction(
            @PathVariable UUID transactionID) throws UserMismatchException, ResourceNotFoundException {
        MutualTransactionDto response = mutualTransactionService.cancelTransaction(transactionID);
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
    @GetMapping("/{userID}")
    public ResponseEntity<ApiResponse<?>> getUserTransactions(
            @PathVariable String userID,
            @Valid @ModelAttribute MutualTransactionFilter filterRequest,
            Pageable pageable) {
        Map<String, String> filters = ApiUtil.getFilters(filterRequest); // generate filters
        Page<MutualTransactionDto> response = mutualTransactionService.getMutualTransactions(userID,  filters, pageable);

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

    // Get all pending transactions requested to the current authenticated user
    @GetMapping("/pending-requests")
    public ResponseEntity<ApiResponse<List<MutualTransactionDto>>> getPendingRequests() {
        List<MutualTransactionDto> pendingTransactions = mutualTransactionService.getAllRequestedPendingTransactions();
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<List<MutualTransactionDto>>builder()
                        .status(HttpStatus.OK.value())
                        .timestamp(LocalDateTime.now())
                        .message("Pending transaction requests retrieved successfully")
                        .data(pendingTransactions)
                        .build()
        );
    }

    // Get mutual transactions between two users
    @GetMapping("/between")
    public ResponseEntity<ApiResponse<?>> getMutualTransactionsBetweenUsers(
            @RequestParam String user1,
            @RequestParam String user2,
            @Valid @ModelAttribute MutualTransactionFilter filterRequest,
            Pageable pageable) {
        Map<String, String> filters = ApiUtil.getFilters(filterRequest);
        Page<MutualTransactionDto> response = mutualTransactionService.getMutualTransactionsBetweenUsers(user1, user2, filters, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .timestamp(LocalDateTime.now())
                        .message("Mutual transactions retrieved successfully")
                        .data(response.getContent())
                        .pagination(ApiUtil.getPagination(response))
                        .build()
        );
    }
}
