package com.moneybook.service;

import com.moneybook.dto.transaction.MutualTransCreateDto;
import com.moneybook.dto.transaction.MutualTransactionDto;
import com.moneybook.exception.ResourceNotFoundException;
import com.moneybook.mappers.MutualTransactionMapper;
import com.moneybook.model.MutualTransaction;
import com.moneybook.model.enums.TransactionStatus;
import com.moneybook.repository.MutualTransactionRepo;
import com.moneybook.util.GenerateQRPayload;
import com.moneybook.util.OtpUtil;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MutualTransactionService {

    private MutualTransactionRepo repo;
    RedisTemplate<String, String> redisTemplate;

    @Transactional
    public MutualTransactionDto createTransaction(MutualTransCreateDto dto) {
        // Generate and hash OTP
        String otp = OtpUtil.generateOtp();
        String otpHash = OtpUtil.hashOtp(otp);

        MutualTransaction transaction = MutualTransactionMapper.MAPPER.toMutualTransaction(dto);
        transaction.setOtpHash(otpHash);

        // Save to database
        transaction = repo.save(transaction);

        // Store OTP in Redis with TTL (24 hours)
        redisTemplate.opsForValue().set(
                "transaction:" + transaction.getTransactionID(),
                otpHash,
                Duration.ofHours(24)
        );

        String qrPayload = GenerateQRPayload.
                generate(transaction.getTransactionID(), transaction.getBorrowerID(), otpHash);

        // Notify borrower
//        notificationService.notifyBorrower(transaction.getBorrowerID(), transaction);

        // Return transaction details + OTP + QR payload
        MutualTransactionDto response = MutualTransactionMapper.MAPPER.fromMutualTransaction(transaction);
        response.setOtp(otp); // Send raw OTP for QR payload
        response.setQrPayload(qrPayload); // Include QR payload
        return response;
    }

    public MutualTransactionDto validateQrData(UUID transactionID, String borrowerID, String hashedOtp)
            throws ResourceNotFoundException {
        MutualTransaction transaction = repo.findById(transactionID)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        // Check if borrower matches
        if (!transaction.getBorrowerID().equals(borrowerID)) {
            throw new IllegalArgumentException("User mismatch");
        }

        // Validate OTP hash in Redis
        String storedOtpHash = redisTemplate.opsForValue().get("transaction:" + transactionID);
        if (!storedOtpHash.equals(hashedOtp)) {
            throw new IllegalArgumentException("Invalid or expired OTP hash");
        }

        // Return transaction details
        return MutualTransactionMapper.MAPPER.fromMutualTransaction(transaction);
    }

    @Transactional
    public MutualTransactionDto acceptTransaction(UUID transactionID, String otp, String hashedOtp, String userID)
            throws ResourceNotFoundException {
        MutualTransaction transaction = repo.findById(transactionID)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        // Verify borrower identity
        if (!transaction.getBorrowerID().equals(userID)) {
            throw new IllegalArgumentException("Unauthorized action");
        }

        // Validate OTP (manual or QR)
        String storedOtpHash = redisTemplate.opsForValue().get("transaction:" + transactionID);
        if (!OtpUtil.verifyOtp(otp, storedOtpHash) && !storedOtpHash.equals(hashedOtp)) {
            throw new IllegalArgumentException("Invalid or expired OTP");
        }

        // Mark as ACCEPTED
        transaction.setStatus(TransactionStatus.ACCEPTED);
        repo.save(transaction);

        // Notify lender
//        notificationService.notifyLender(transaction.getLenderID(), transaction);

        // Return transaction details
        return MutualTransactionMapper.MAPPER.fromMutualTransaction(transaction);
    }

    @Transactional
    public MutualTransactionDto rejectTransaction(UUID transactionID, String userID) throws ResourceNotFoundException {
        MutualTransaction transaction = repo.findById(transactionID)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        // Verify borrower identity
        if (!transaction.getBorrowerID().equals(userID)) {
            throw new IllegalArgumentException("Unauthorized action");
        }

        // Mark as REJECTED
        transaction.setStatus(TransactionStatus.REJECTED);
        repo.save(transaction);

        // Notify lender
//        notificationService.notifyLender(transaction.getLenderID(), transaction);

        return MutualTransactionMapper.MAPPER.fromMutualTransaction(transaction);
    }

    @Transactional
    public MutualTransactionDto cancelTransaction(UUID transactionID, String userID) throws ResourceNotFoundException {
        MutualTransaction transaction = repo.findById(transactionID)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        // Verify lender identity
        if (!transaction.getLenderID().equals(userID)) {
            throw new IllegalArgumentException("Unauthorized action");
        }

        // Mark as CANCELLED
        transaction.setStatus(TransactionStatus.CANCELLED);
        repo.save(transaction);

        // Notify borrower
//        notificationService.notifyBorrower(transaction.getBorrowerID(), transaction);

        return MutualTransactionMapper.MAPPER.fromMutualTransaction(transaction);

    }

    public List<MutualTransactionDto> getUserTransactions(String userID, TransactionStatus status) {
        List<MutualTransaction> transactions;

        if (status == null) {
            transactions = repo.findByBorrowerIDOrLenderID(userID, userID);
        } else {
            transactions = repo.findByBorrowerIDOrLenderIDAndStatus(userID, userID, status);
        }

        return transactions.stream()
                .map(MutualTransactionMapper.MAPPER::fromMutualTransaction)
                .collect(Collectors.toList());
    }

    @Scheduled(cron = "0 30 23 * * *") // Runs every day at 11:30 PM
    @Transactional
    @Async
    public void checkTransactionExpiry() {
        OffsetDateTime now = OffsetDateTime.now();
        List<MutualTransaction> expiredTransactions = repo.findByStatusAndExpiryDateBefore(TransactionStatus.PENDING, now);

        for (MutualTransaction transaction : expiredTransactions) {
            transaction.setStatus(TransactionStatus.CANCELLED);
            repo.save(transaction);

            // Notify lender about expiry
//            notificationService.notifyLender(transaction.getLenderID(), transaction);
        }
    }


}
