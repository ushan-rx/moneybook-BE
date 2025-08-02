package com.moneybook.service.transaction;

import com.moneybook.dto.transaction.*;
import com.moneybook.exception.InvalidOtpException;
import com.moneybook.exception.ResourceNotFoundException;
import com.moneybook.exception.UserMismatchException;
import com.moneybook.mappers.MutualTransactionMapper;
import com.moneybook.model.FriendBalance;
import com.moneybook.model.MutualTransaction;
import com.moneybook.model.enums.TransactionStatus;
import com.moneybook.repository.FriendBalanceRepo;
import com.moneybook.repository.MutualTransactionRepo;
import com.moneybook.util.FilterSpecification;
import com.moneybook.util.OtpUtil;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Slf4j
@Service
@AllArgsConstructor
public class MutualTransactionService {

    private final MutualTransactionRepo repo;
    private final FriendBalanceRepo friendBalanceRepo;
    private final RedisTemplate<String, String> redisTemplate;
    private final MutualTransactionMapper mapper;

    @Transactional
    public MutualTransactionDto createTransaction(MutualTransCreateDto dto) {
        String otp = OtpUtil.generateOtp();
        String otpHash = OtpUtil.hashOtp(otp);

        MutualTransaction transaction = mapper.toMutualTransaction(dto);
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

    public MutualTransactionDto validateQrData(UUID transactionID, String hashedOtp)
            throws InvalidOtpException, UserMismatchException, ResourceNotFoundException {
        MutualTransaction transaction = fetchAndValidateTransactionAndResponder(transactionID);
        validateOtpInRedis(transactionID, hashedOtp);
        return mapToDto(transaction);
    }

    @Transactional
    public MutualTransactionDto transactionManualAccept(UUID transactionID, MutualTransactionManual manualData)
            throws UserMismatchException, InvalidOtpException, ResourceNotFoundException {
        validateOtp(transactionID, manualData.getOtp());
        MutualTransactionDto response =  updateTransactionStatus(transactionID, manualData.getStatus());
        if (manualData.getStatus().equals(TransactionStatus.ACCEPTED)) {
            updateBalance(response);
        }
        redisTemplate.delete("transaction:" + transactionID);
        return response;
    }

    // This method marks a transaction as rejected by the responder.
    public MutualTransactionDto rejectTransaction(UUID transactionID)
            throws UserMismatchException, ResourceNotFoundException {
        MutualTransaction transaction = fetchAndValidateTransactionAndResponder(transactionID);
        // Check if the transaction is already accepted
        if (transaction.getStatus() == TransactionStatus.ACCEPTED) {
            throw new IllegalArgumentException("Transaction already accepted");
        }
        transaction.setStatus(TransactionStatus.REJECTED);
        repo.save(transaction);
        // Remove transaction data from Redis
        redisTemplate.delete("transaction:" + transactionID);
        return mapToDto(transaction);
    }

    @Transactional
    public MutualTransactionDto transactionQrRespond(UUID transactionID, MutualTransactionQr qrData)
            throws UserMismatchException, InvalidOtpException, ResourceNotFoundException {
        validateOtpInRedis(transactionID, qrData.getHashedOtp());
        MutualTransactionDto response =  updateTransactionStatus(transactionID, qrData.getStatus());
        if (qrData.getStatus().equals(TransactionStatus.ACCEPTED)) {
            updateBalance(response);
        }
        redisTemplate.delete("transaction:" + transactionID);
        return response;
    }

    // This method updates the status of a transaction and removes it from Redis cache if it was previously stored.
    private MutualTransactionDto updateTransactionStatus(UUID transactionID, TransactionStatus status)
            throws UserMismatchException, ResourceNotFoundException {
        MutualTransaction transaction = fetchAndValidateTransactionAndResponder(transactionID);
        // Check if the transaction is already accepted
        if (transaction.getStatus() == TransactionStatus.ACCEPTED) {
            throw new IllegalArgumentException("Transaction already accepted");
        }
        transaction.setStatus(status);
        repo.save(transaction);
        // Remove transaction data from Redis
        redisTemplate.delete("transaction:" + transactionID);
        log.info("Transaction ID {} marked as {} by user ID {}", transactionID, status);
        return mapToDto(transaction);
    }

    // cancel transaction by the requested user (transaction initiator)
    @Transactional
    public MutualTransactionDto cancelTransaction(UUID transactionID)
            throws UserMismatchException, ResourceNotFoundException {
        MutualTransaction transaction = fetchAndValidateTransactionAndCreator(transactionID);
        // Check if the transaction is already accepted
        if (transaction.getStatus() == TransactionStatus.ACCEPTED) {
            throw new IllegalArgumentException("Transaction already accepted");
        }
        transaction.setStatus(TransactionStatus.CANCELLED);
        repo.save(transaction);
        // Remove transaction data from Redis
        redisTemplate.delete("transaction:" + transactionID);
        log.info("Transaction ID {} marked as {} by user ID {}", transactionID, TransactionStatus.CANCELLED);
        return mapToDto(transaction);
    }

    // get all mutual transactions for a user with optional filters
    public Page<MutualTransactionDto> getMutualTransactions(
            String userID,
            Map<String, String> filters,
            Pageable pageable) {
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("transactionDate").descending());
        Specification<MutualTransaction> specifications = new FilterSpecification<>(filters);
        specifications = specifications.and(((root, query, cb) ->  cb.or(
                cb.equal(root.get("borrowerID"), userID), cb.equal(root.get("lenderID"), userID))
        ));
        return repo.findAll(specifications, sortedPageable).map(mapper::fromMutualTransaction);
    }

    // get all requested pending transactions for the current authenticated user
    public List<MutualTransactionDto> getAllRequestedPendingTransactions() {
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        List<MutualTransaction> pendingTransactions = repo.findByRequestedToAndStatus(
                currentUser, TransactionStatus.PENDING);
        return pendingTransactions.stream()
                .map(mapper::fromMutualTransaction)
                .collect(java.util.stream.Collectors.toList());
    }

    // get mutual transactions between two users with optional filters
    public Page<MutualTransactionDto> getMutualTransactionsBetweenUsers(
            String lenderID,
            String borrowerID,
            Map<String, String> filters,
            Pageable pageable) {
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("transactionDate").descending());
        Specification<MutualTransaction> specifications = new FilterSpecification<>(filters);
        specifications = specifications.and(((root, query, cb) -> cb.or(
                cb.and(cb.equal(root.get("lenderID"), lenderID), cb.equal(root.get("borrowerID"), borrowerID)),
                cb.and(cb.equal(root.get("lenderID"), borrowerID), cb.equal(root.get("borrowerID"), lenderID))
        )));
        return repo.findAll(specifications, sortedPageable).map(mapper::fromMutualTransaction);
    }

    // This method checks for transactions that have been in PENDING status for more than 24 hours and marks them as CANCELLED.
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

    // This method validates the OTP hash stored in Redis against the provided OTP hash. (when through QR code)
    private void validateOtpInRedis(UUID transactionID, String providedOtpHash) throws InvalidOtpException {
        String storedOtpHash = redisTemplate.opsForValue().get("transaction:" + transactionID);
        if (storedOtpHash == null || !storedOtpHash.equals(providedOtpHash)) {
            throw new InvalidOtpException("Invalid or expired OTP");
        }
    }

    // This method validates the OTP provided by the user against the stored OTP hash. (when manually inputted)
    private void validateOtp(UUID transactionID, String otp) throws InvalidOtpException {
        String storedOtpHash = redisTemplate.opsForValue().get("transaction:" + transactionID);
        if (!OtpUtil.verifyOtp(otp, storedOtpHash)) {
            throw new InvalidOtpException("Invalid or expired OTP");
        }
    }

    // This method updates the balances of the borrower and lender based on the transaction amount.
    private void updateBalance(MutualTransactionDto transaction) {
        String borrowerID = transaction.getBorrowerID();
        String lenderID = transaction.getLenderID();
        try {
            FriendBalance balanceByBorrower = friendBalanceRepo.findByUser1Id(borrowerID).getFirst();
            FriendBalance balanceByLender = friendBalanceRepo.findByUser1Id(lenderID).getFirst();
            if( balanceByBorrower != null && balanceByLender != null) {
                // Update the balances based on the transaction amount
                balanceByLender.setBalanceAmount(balanceByLender.getBalanceAmount().add(transaction.getAmount()));
                balanceByBorrower.setBalanceAmount(balanceByBorrower.getBalanceAmount().subtract(transaction.getAmount()));
            }
        } catch (Exception e) {
            FriendBalance newBalance1 = FriendBalance.builder().user1Id(borrowerID).user2Id(lenderID).
                    balanceAmount(BigDecimal.valueOf(0)).build();
            FriendBalance newBalance2 = FriendBalance.builder().user1Id(lenderID).user2Id(borrowerID).
                    balanceAmount(BigDecimal.valueOf(0)).build();

            newBalance1.setBalanceAmount(newBalance1.getBalanceAmount().subtract(transaction.getAmount()));
            newBalance2.setBalanceAmount(newBalance2.getBalanceAmount().add(transaction.getAmount()));
            friendBalanceRepo.save(newBalance1);
            friendBalanceRepo.save(newBalance2);
        }
    }

    // This method fetches the transaction by ID and validates the user.
    // (used to validate the responding user for both manual and QR code responses)
    private MutualTransaction fetchAndValidateTransactionAndResponder(UUID transactionID)
            throws UserMismatchException, ResourceNotFoundException {
        MutualTransaction transaction = repo.findById(transactionID)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + transactionID));
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!transaction.getRequestedTo().equals(currentUser)) {
            throw new UserMismatchException("Unauthorized user with ID: " + currentUser);
        }
        return transaction;
    }

    // This method fetches the transaction by ID and validates the user.
    // (used to validate the creator of the transaction for cancellation)
    private MutualTransaction fetchAndValidateTransactionAndCreator(UUID transactionID)
            throws UserMismatchException, ResourceNotFoundException {
        MutualTransaction transaction = repo.findById(transactionID)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + transactionID));
        // Validate the user making the request
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        String requestedUser = transaction.getRequestedTo().equals(transaction.getLenderID()) ?
                transaction.getBorrowerID() : transaction.getLenderID(); // get the requested user
        if (!requestedUser.equals(currentUser)) {
            throw new UserMismatchException("Unauthorized user with ID: " + currentUser);
        }
        return transaction;
    }

    private MutualTransactionDto mapToDto(MutualTransaction transaction) {
        return mapper.fromMutualTransaction(transaction);
    }

}
