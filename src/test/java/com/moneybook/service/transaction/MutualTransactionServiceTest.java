package com.moneybook.service.transaction;

import com.moneybook.dto.transaction.*;
import com.moneybook.exception.InvalidOtpException;
import com.moneybook.exception.ResourceNotFoundException;
import com.moneybook.exception.UserMismatchException;
import com.moneybook.mappers.MutualTransactionMapper;
import com.moneybook.model.MutualTransaction;
import com.moneybook.model.enums.TransactionStatus;
import com.moneybook.model.enums.TransactionType;
import com.moneybook.repository.FriendBalanceRepo;
import com.moneybook.repository.MutualTransactionRepo;
import com.moneybook.util.OtpUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MutualTransactionServiceTest {

    @Mock
    private MutualTransactionRepo transactionRepo;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private MutualTransactionMapper mapper;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private MutualTransactionService service;

    private MutualTransaction testTransaction;
    private MutualTransactionDto testTransactionDto;
    private static final String TEST_USER_ID = "user1";
    private static final String TEST_FRIEND_ID = "user2";
    private static final UUID TEST_TRANSACTION_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        // Setup test transaction
        testTransaction = new MutualTransaction();
        testTransaction.setTransactionID(TEST_TRANSACTION_ID);
        testTransaction.setTransactionName("Test Transaction");
        testTransaction.setAmount(BigDecimal.valueOf(100));
        testTransaction.setLenderID(TEST_USER_ID);
        testTransaction.setBorrowerID(TEST_FRIEND_ID);
        testTransaction.setRequestedTo(TEST_FRIEND_ID);
        testTransaction.setStatus(TransactionStatus.PENDING);
        testTransaction.setTransactionDate(OffsetDateTime.now());
        testTransaction.setTransactionType(TransactionType.LOAN);

        // Setup test DTO
        testTransactionDto = new MutualTransactionDto();
        testTransactionDto.setTransactionID(TEST_TRANSACTION_ID);
        testTransactionDto.setAmount(BigDecimal.valueOf(100));
        testTransactionDto.setLenderID(TEST_USER_ID);
        testTransactionDto.setBorrowerID(TEST_FRIEND_ID);
        testTransactionDto.setStatus(String.valueOf(TransactionStatus.PENDING));
    }

    @Test
    void createTransaction_Success() {
        // Arrange
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        MutualTransCreateDto createDto = new MutualTransCreateDto();
        createDto.setAmount(BigDecimal.valueOf(100));
        createDto.setTransactionName("Test Transaction");

        when(mapper.toMutualTransaction(any())).thenReturn(testTransaction);
        when(transactionRepo.save(any())).thenReturn(testTransaction);
        when(mapper.fromMutualTransaction(any())).thenReturn(testTransactionDto);

        // Act
        MutualTransactionDto result = service.createTransaction(createDto);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getOtp());
        assertNotNull(result.getQrPayload());
        verify(redisTemplate.opsForValue()).set(anyString(), anyString(), any());
    }

    @Test
    void validateQrData_Success() throws InvalidOtpException, UserMismatchException, ResourceNotFoundException {
        // Arrange
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        String hashedOtp = OtpUtil.hashOtp("123456");
        when(transactionRepo.findById(TEST_TRANSACTION_ID)).thenReturn(Optional.of(testTransaction));
        when(redisTemplate.opsForValue().get(anyString())).thenReturn(hashedOtp);
        when(mapper.fromMutualTransaction(any())).thenReturn(testTransactionDto);

        // Mock SecurityContext
        mockSecurityContext(TEST_FRIEND_ID);

        // Act
        MutualTransactionDto result = service.validateQrData(TEST_TRANSACTION_ID, hashedOtp);

        // Assert
        assertNotNull(result);
        assertEquals(TEST_TRANSACTION_ID, result.getTransactionID());
    }

    @Test
    void transactionManualRespond_AcceptSuccess() throws InvalidOtpException, UserMismatchException, ResourceNotFoundException {
        // Arrange
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        String otp = "123456";
        String hashedOtp = OtpUtil.hashOtp(otp);
        testTransaction.setOtpHash(hashedOtp);

        MutualTransactionManual manualData = new MutualTransactionManual();
        manualData.setOtp(otp);
        manualData.setStatus(TransactionStatus.ACCEPTED);

        when(transactionRepo.findById(TEST_TRANSACTION_ID)).thenReturn(Optional.of(testTransaction));
        when(redisTemplate.opsForValue().get(anyString())).thenReturn(hashedOtp);
        when(mapper.fromMutualTransaction(any())).thenReturn(testTransactionDto);
        mockSecurityContext(TEST_FRIEND_ID);

        // Act
        MutualTransactionDto result = service.transactionManualRespond(TEST_TRANSACTION_ID, manualData);

        // Assert
        assertNotNull(result);
        verify(redisTemplate).delete(anyString());
        verify(transactionRepo).save(any());
    }

    @Test
    void cancelTransaction_Success() throws UserMismatchException, ResourceNotFoundException {
        // Arrange
        when(transactionRepo.findById(TEST_TRANSACTION_ID)).thenReturn(Optional.of(testTransaction));
        when(mapper.fromMutualTransaction(any())).thenReturn(testTransactionDto);
        mockSecurityContext(TEST_USER_ID);

        // Act
        MutualTransactionDto result = service.cancelTransaction(TEST_TRANSACTION_ID);

        // Assert
        assertNotNull(result);
        verify(transactionRepo).save(argThat(transaction ->
            transaction.getStatus() == TransactionStatus.CANCELLED
        ));
    }

    @Test
    void getMutualTransactions_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<MutualTransaction> transactions = Collections.singletonList(testTransaction);
        Page<MutualTransaction> transactionPage = new PageImpl<>(transactions);

        when(transactionRepo.findAll(any(Specification.class), any(Pageable.class)))
            .thenReturn(transactionPage);
        when(mapper.fromMutualTransaction(any())).thenReturn(testTransactionDto);

        // Act
        Page<MutualTransactionDto> result = service.getMutualTransactions(
            TEST_USER_ID,
            new HashMap<>(),
            pageable
        );

        // Assert
        assertNotNull(result);
        assertFalse(result.getContent().isEmpty());
        assertEquals(1, result.getContent().size());
    }

    @Test
    void getAllRequestedPendingTransactions_Success() {
        // Arrange
        List<MutualTransaction> transactions = Collections.singletonList(testTransaction);
        when(transactionRepo.findByRequestedToAndStatus(anyString(), eq(TransactionStatus.PENDING)))
            .thenReturn(transactions);
        when(mapper.fromMutualTransaction(any())).thenReturn(testTransactionDto);
        mockSecurityContext(TEST_USER_ID);

        // Act
        List<MutualTransactionDto> result = service.getAllRequestedPendingTransactions();

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    // Helper method to mock SecurityContext
    private void mockSecurityContext(String username) {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(username);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }
}
