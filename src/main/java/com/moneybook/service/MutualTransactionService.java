package com.moneybook.service;

import com.moneybook.dto.transaction.MutualTransCreateDto;
import com.moneybook.dto.transaction.MutualTransactionDto;
import com.moneybook.dto.transaction.QrPayload;
import com.moneybook.exception.InvalidOtpException;
import com.moneybook.exception.ResourceNotFoundException;
import com.moneybook.exception.UserMismatchException;
import com.moneybook.mappers.MutualTransactionMapper;
import com.moneybook.model.MutualTransaction;
import com.moneybook.model.enums.TransactionStatus;
import com.moneybook.repository.MutualTransactionRepo;
import com.moneybook.repository.specifications.MutualTransactionSpecification;
import com.moneybook.util.OtpUtil;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;


@Slf4j
@Service
@AllArgsConstructor
public class MutualTransactionService {

    private MutualTransactionRepo repo;
    private MutualTransactionSpecification specification;
    RedisTemplate<String, String> redisTemplate;

    @Transactional
    public MutualTransactionDto createTransaction(MutualTransCreateDto dto) {
        // Generate and hash OTP0
        String otp = OtpUtil.generateOtp();
        String otpHash = OtpUtil.hashOtp(otp);

        MutualTransaction transaction = MutualTransactionMapper.MAPPER.toMutualTransaction(dto);
        transaction.setOtpHash(otpHash);

        // generate transactionId
        transaction.setTransactionID(UUID.randomUUID());

        // Save to database
        MutualTransaction savedTransaction = repo.save(transaction);

        // Store OTP in Redis with TTL (24 hours)
        redisTemplate.opsForValue().set(
                "transaction:" + transaction.getTransactionID(),
                otpHash,
                Duration.ofHours(24)
        );

//         Generate QR payload
        QrPayload qrPayload = QrPayload.builder()
                .transactionID(savedTransaction.getTransactionID())
                .otpHash(otpHash)
                .build();

        // Notify borrower
//        notificationService.notifyBorrower(transaction.getBorrowerID(), transaction);

        // Return transaction details + OTP + QR payload
        MutualTransactionDto response = MutualTransactionMapper.MAPPER.fromMutualTransaction(savedTransaction);
        response.setOtp(otp); // Send raw OTP for QR payload
        response.setQrPayload(qrPayload); // Include QR payload
        return response;
    }

    //
    public MutualTransactionDto validateQrData(UUID transactionID, String hashedOtp, String userID)
            throws ResourceNotFoundException, UserMismatchException, InvalidOtpException {
        MutualTransaction transaction = repo.findById(transactionID)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + transactionID));

        // Check if user matches
        if (!transaction.getBorrowerID().equals(userID)) {
            throw new UserMismatchException("Unauthorized user with ID: " + userID);
        }

        // Validate OTP hash in Redis
        String storedOtpHash = redisTemplate.opsForValue().get("transaction:" + transactionID);
        if (storedOtpHash == null || !storedOtpHash.equals(hashedOtp)) {
            throw new InvalidOtpException("Invalid or expired OTP");
        }

        // Return transaction details
        return MutualTransactionMapper.MAPPER.fromMutualTransaction(transaction);
    }

    @Transactional
    public MutualTransactionDto acceptTransactionManual(UUID transactionID, String otp, String userID)
            throws UserMismatchException, ResourceNotFoundException, InvalidOtpException {
        String hashedOtp = OtpUtil.hashOtp(otp);
        return acceptTransaction(transactionID, hashedOtp, userID);
    }

    @Transactional
    public MutualTransactionDto acceptTransactionQr(UUID transactionID, String hashedOtp, String userID)
            throws UserMismatchException, ResourceNotFoundException, InvalidOtpException {
        return acceptTransaction(transactionID, hashedOtp, userID);
    }

    @Transactional
    public MutualTransactionDto acceptTransaction(UUID transactionID,  String hashedOtp, String userID)
            throws ResourceNotFoundException, UserMismatchException, InvalidOtpException {
        log.info("Starting transaction acceptance for ID: {}", transactionID);

        // Fetch the transaction
        MutualTransaction transaction = repo.findById(transactionID)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + transactionID));

        // Verify user identity
        if (!transaction.getBorrowerID().equals(userID)) {
            log.warn("Unauthorized action by user ID: {}", userID);
            throw new UserMismatchException("Unauthorized action for user with ID: " + userID);
        }

        // Validate OTP
        String storedOtpHash = redisTemplate.opsForValue().get("transaction:" + transactionID);
        if (storedOtpHash == null || !storedOtpHash.equals(hashedOtp)) {
            log.warn("Invalid or expired OTP for transaction ID: {}", transactionID);
            throw new IllegalArgumentException("Invalid or expired OTP");
        }

        // Mark as ACCEPTED
        transaction.setStatus(TransactionStatus.ACCEPTED);
        repo.save(transaction);
        log.info("Transaction ID {} marked as ACCEPTED by user ID {}", transactionID, userID);

        // Notify lender asynchronously (if enabled)
        // notificationService.notifyLenderAsync(transaction.getLenderID(), transaction);

        // Return transaction details
        return MutualTransactionMapper.MAPPER.fromMutualTransaction(transaction);
    }


    @Transactional
    public MutualTransactionDto rejectTransaction(UUID transactionID, String userID) throws ResourceNotFoundException {
        MutualTransaction transaction = repo.findById(transactionID)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + transactionID));

        // Verify borrower identity
        if (!transaction.getBorrowerID().equals(userID)) {
            throw new IllegalArgumentException("Unauthorized action for user with ID: " + userID);
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
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + transactionID));

        // Verify user identity
        if (!transaction.getLenderID().equals(userID)) {
            throw new IllegalArgumentException("Unauthorized action for user with ID: " + userID);
        }

        // Mark as CANCELLED
        transaction.setStatus(TransactionStatus.CANCELLED);
        repo.save(transaction);

        // Notify borrower
//        notificationService.notifyBorrower(transaction.getBorrowerID(), transaction);

        return MutualTransactionMapper.MAPPER.fromMutualTransaction(transaction);
    }

    public Page<MutualTransactionDto> getUserTransactions(
            String userID, TransactionStatus status, Map<String, String> filters, Pageable pageable) {
        Specification<MutualTransaction> specifications = specification.buildSpecification(userID, status, filters);
        Page<MutualTransaction> transactions = repo.findAll(specifications, pageable);
        return transactions.map(MutualTransactionMapper.MAPPER::fromMutualTransaction);
    }






    @Scheduled(cron = "0 30 23 * * *") // Runs every day at 11:30 PM
    @Transactional
    public void checkTransactionExpiry() {
        int updatedCount = repo.batchUpdateExpiredTransactions(
                TransactionStatus.CANCELLED, TransactionStatus.PENDING, OffsetDateTime.now());
        log.info("{} transactions expired and were marked as CANCELLED", updatedCount);
    }


}
