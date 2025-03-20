package com.moneybook.service.transaction;

import com.moneybook.dto.transaction.*;
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

    private final MutualTransactionRepo repo;
    private final MutualTransactionSpecification specification;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public MutualTransactionDto createTransaction(MutualTransCreateDto dto) {
        String otp = OtpUtil.generateOtp();
        String otpHash = OtpUtil.hashOtp(otp);

        MutualTransaction transaction = MutualTransactionMapper.MAPPER.toMutualTransaction(dto);
        // Set the otp and transaction ID
        transaction.setOtpHash(otpHash);
        transaction.setTransactionID(UUID.randomUUID());

        MutualTransaction savedTransaction = repo.save(transaction);
        storeOtpInRedis(savedTransaction.getTransactionID(), otpHash);
        // Create the QR payload
        QrPayload qrPayload = QrPayload.builder()
                .transactionID(savedTransaction.getTransactionID())
                .otpHash(otpHash)
                .build();

        MutualTransactionDto response = mapToDto(savedTransaction);
        response.setOtp(otp);
        response.setQrPayload(qrPayload);

        return response;
    }

    public MutualTransactionDto validateQrData(UUID transactionID, String hashedOtp, String userID)
            throws InvalidOtpException, UserMismatchException, ResourceNotFoundException {
        MutualTransaction transaction = fetchAndValidateTransaction(transactionID, userID);
        validateOtpInRedis(transactionID, hashedOtp);
        return mapToDto(transaction);
    }

    @Transactional
    public MutualTransactionDto acceptTransactionManual(UUID transactionID, MutualTransactionManual manualData)
            throws UserMismatchException, InvalidOtpException, ResourceNotFoundException {
        validateOtp(transactionID, manualData.getOtp());
        return updateTransactionStatus(transactionID, manualData.getUserID(), TransactionStatus.ACCEPTED);
    }

    @Transactional
    public MutualTransactionDto acceptTransactionQr(UUID transactionID, MutualTransactionQr qrData)
            throws UserMismatchException, InvalidOtpException, ResourceNotFoundException {
        validateOtpInRedis(transactionID, qrData.getHashedOtp());
        return updateTransactionStatus(transactionID, qrData.getUserID(), TransactionStatus.ACCEPTED);
    }

    // reject transaction by borrower
    @Transactional
    public MutualTransactionDto rejectTransaction(UUID transactionID, String userID)
            throws UserMismatchException, ResourceNotFoundException {
        return updateTransactionStatus(transactionID, userID, TransactionStatus.REJECTED);
    }

    // cancel transaction by lender
    @Transactional
    public MutualTransactionDto cancelTransaction(UUID transactionID, String userID)
            throws UserMismatchException, ResourceNotFoundException {
        MutualTransaction transaction = repo.findById(transactionID)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + transactionID));

        if (!transaction.getLenderID().equals(userID)) {
            throw new UserMismatchException("Unauthorized user with ID: " + userID);
        }

        if (transaction.getStatus() == TransactionStatus.ACCEPTED) {
            throw new IllegalArgumentException("Transaction already accepted");
        }

        redisTemplate.delete("transaction:" + transactionID);
        transaction.setStatus(TransactionStatus.CANCELLED);
        repo.save(transaction);
        log.info("Transaction ID {} marked as {} by user ID {}", transactionID, TransactionStatus.CANCELLED, userID);

        return mapToDto(transaction);
    }

    public Page<MutualTransactionDto> getMutualTransactions(
            String userID, TransactionStatus status, Map<String, String> filters, Pageable pageable) {
        Specification<MutualTransaction> specifications = specification.buildSpecification(userID, status, filters);
        return repo.findAll(specifications, pageable).map(MutualTransactionMapper.MAPPER::fromMutualTransaction);
    }

    @Scheduled(cron = "0 30 23 * * *")
    @Transactional
    public void checkTransactionExpiry() {
        int updatedCount = repo.batchUpdateExpiredTransactions(
                TransactionStatus.CANCELLED, TransactionStatus.PENDING, OffsetDateTime.now());
        log.info("{} transactions expired and were marked as CANCELLED", updatedCount);
    }


    // Helper methods
    private void storeOtpInRedis(UUID transactionID, String otpHash) {
        redisTemplate.opsForValue().set(
                "transaction:" + transactionID,
                otpHash,
                Duration.ofHours(24)
        );
    }

    private void validateOtpInRedis(UUID transactionID, String providedOtpHash) throws InvalidOtpException {
        String storedOtpHash = redisTemplate.opsForValue().get("transaction:" + transactionID);
        if (storedOtpHash == null || !storedOtpHash.equals(providedOtpHash)) {
            throw new InvalidOtpException("Invalid or expired OTP");
        }
    }

    private void validateOtp(UUID transactionID, String otp) throws InvalidOtpException {
        String storedOtpHash = redisTemplate.opsForValue().get("transaction:" + transactionID);
        if (!OtpUtil.verifyOtp(otp, storedOtpHash)) {
            throw new InvalidOtpException("Invalid or expired OTP");
        }
    }

    private MutualTransaction fetchAndValidateTransaction(UUID transactionID, String userID)
            throws UserMismatchException, ResourceNotFoundException {
        MutualTransaction transaction = repo.findById(transactionID)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + transactionID));

        if (!transaction.getBorrowerID().equals(userID)) {
            throw new UserMismatchException("Unauthorized user with ID: " + userID);
        }
        return transaction;
    }

    private MutualTransactionDto updateTransactionStatus(UUID transactionID, String userID, TransactionStatus status)
            throws UserMismatchException, ResourceNotFoundException {
        MutualTransaction transaction = fetchAndValidateTransaction(transactionID, userID);

        if (transaction.getStatus() == TransactionStatus.ACCEPTED) {
            throw new IllegalArgumentException("Transaction already accepted");
        }

        redisTemplate.delete("transaction:" + transactionID);
        transaction.setStatus(status);
        repo.save(transaction);
        log.info("Transaction ID {} marked as {} by user ID {}", transactionID, status, userID);

        return mapToDto(transaction);
    }

    private MutualTransactionDto mapToDto(MutualTransaction transaction) {
        return MutualTransactionMapper.MAPPER.fromMutualTransaction(transaction);
    }
}
