package com.moneybook.unit.service.friend;

import com.moneybook.dto.friend.*;
import com.moneybook.dto.user.NormalUserBriefDto;
import com.moneybook.exception.ResourceNotFoundException;
import com.moneybook.mappers.FriendRequestMapper;
import com.moneybook.model.FriendRequest;
import com.moneybook.model.enums.FriendRequestStatus;
import com.moneybook.repository.FriendRequestRepo;
import com.moneybook.repository.FriendshipRepo;
import com.moneybook.service.NormalUserService;
import com.moneybook.service.friend.FriendshipService;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FriendshipServiceTest {

    @Mock
    private FriendshipRepo friendshipRepo;

    @Mock
    private FriendRequestRepo friendRequestRepo;

    @Mock
    private FriendRequestMapper requestMapper;

    @Mock
    private NormalUserService normalUserService;

    @InjectMocks
    private FriendshipService friendshipService;

    // Mock Security Context
    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private final String USER_ID = "user1";
    private final String FRIEND_ID = "user2";

    @BeforeEach
    void setUp() {
        // Configure Security Context for tests that require authentication
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        lenient().when(authentication.getName()).thenReturn(USER_ID);
    }

    @Test
    void sendFriendRequest_Success() {
        // Arrange
        FriendRequestCreateCancelDto createDto = new FriendRequestCreateCancelDto(USER_ID, FRIEND_ID);
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setSenderId(USER_ID);
        friendRequest.setReceiverId(FRIEND_ID);
        friendRequest.setStatus(FriendRequestStatus.PENDING);

        FriendRequestDto expectedDto = new FriendRequestDto();
        expectedDto.setSenderId(USER_ID);
        expectedDto.setReceiverId(FRIEND_ID);
        expectedDto.setStatus(FriendRequestStatus.PENDING);

        when(friendshipRepo.existsByUserIdAndFriendId(USER_ID, FRIEND_ID)).thenReturn(false);
        when(friendRequestRepo.existsBySenderIdAndReceiverId(anyString(), anyString())).thenReturn(false);
        when(requestMapper.toFriendRequest(createDto)).thenReturn(friendRequest);
        when(friendRequestRepo.save(any(FriendRequest.class))).thenReturn(friendRequest);
        when(requestMapper.fromFriendRequest(friendRequest)).thenReturn(expectedDto);

        // Act
        FriendRequestDto result = friendshipService.sendFriendRequest(createDto);

        // Assert
        assertNotNull(result);
        assertEquals(USER_ID, result.getSenderId());
        assertEquals(FRIEND_ID, result.getReceiverId());
        assertEquals(FriendRequestStatus.PENDING, result.getStatus());
        verify(friendRequestRepo).save(any(FriendRequest.class));
    }

    @Test
    void sendFriendRequest_ExistingFriendship() {
        // Arrange
        FriendRequestCreateCancelDto createDto = new FriendRequestCreateCancelDto(USER_ID, FRIEND_ID);
        when(friendshipRepo.existsByUserIdAndFriendId(USER_ID, FRIEND_ID)).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> friendshipService.sendFriendRequest(createDto));
        assertEquals("A friendship already exists between the users.", exception.getMessage());
    }

    @Test
    void sendFriendRequest_ExistingRequest() {
        // Arrange
        FriendRequestCreateCancelDto createDto = new FriendRequestCreateCancelDto(USER_ID, FRIEND_ID);
        when(friendshipRepo.existsByUserIdAndFriendId(USER_ID, FRIEND_ID)).thenReturn(false);
        when(friendRequestRepo.existsBySenderIdAndReceiverId(USER_ID, FRIEND_ID)).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> friendshipService.sendFriendRequest(createDto));
        assertEquals("A friend request is already pending between the users.", exception.getMessage());
    }

    @Test
    void getFriendRequestByReceiverIdAndSenderId_Success() {
        // Arrange
        String receiverId = FRIEND_ID;
        FriendRequest request = new FriendRequest();
        request.setSenderId(USER_ID);
        request.setReceiverId(receiverId);

        FriendRequestDto expectedDto = new FriendRequestDto();

        when(friendRequestRepo.findBySenderAndReceiver(USER_ID, receiverId)).thenReturn(Optional.of(request));
        when(requestMapper.fromFriendRequest(request)).thenReturn(expectedDto);

        // Act
        FriendRequestDto result = friendshipService.getFriendRequestByReceiverIdAndSenderId(receiverId);

        // Assert
        assertNotNull(result);
        verify(friendRequestRepo).findBySenderAndReceiver(USER_ID, receiverId);
    }

    @Test
    void getFriendRequestByReceiverIdAndSenderId_NotFound() {
        // Arrange
        String receiverId = FRIEND_ID;
        when(friendRequestRepo.findBySenderAndReceiver(USER_ID, receiverId)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> friendshipService.getFriendRequestByReceiverIdAndSenderId(receiverId));
        assertEquals("Friend requests not found.", exception.getMessage());
    }

    @Test
    void cancelFriendRequestById_Success() throws ResourceNotFoundException {
        // Arrange
        Long requestId = 1L;
        FriendRequest request = new FriendRequest();
        request.setRequestId(requestId);
        request.setSenderId(USER_ID);
        request.setReceiverId(FRIEND_ID);

        FriendRequestDto dto = new FriendRequestDto();
        dto.setRequestId(requestId);
        dto.setSenderId(USER_ID);
        dto.setReceiverId(FRIEND_ID);

        NormalUserBriefDto senderDetails = new NormalUserBriefDto();
        senderDetails.setUserId(USER_ID);

        when(friendRequestRepo.findById(requestId)).thenReturn(Optional.of(request));
        when(requestMapper.fromFriendRequest(request)).thenReturn(dto);
        when(normalUserService.getUserBrief(USER_ID)).thenReturn(senderDetails);

        // Act
        FriendRequestDto result = friendshipService.cancelFriendRequestById(requestId);

        // Assert
        assertNotNull(result);
        assertEquals(requestId, result.getRequestId());
        assertEquals(senderDetails, result.getSender());
        verify(friendRequestRepo).delete(request);
    }

    @Test
    void cancelFriendRequestById_NotFound() {
        // Arrange
        Long requestId = 1L;
        when(friendRequestRepo.findById(requestId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> friendshipService.cancelFriendRequestById(requestId));
    }

    @Test
    void cancelFriendRequestById_NotSender() {
        // Arrange
        Long requestId = 1L;
        FriendRequest request = new FriendRequest();
        request.setRequestId(requestId);
        request.setSenderId("different-user");
        request.setReceiverId(FRIEND_ID);

        when(friendRequestRepo.findById(requestId)).thenReturn(Optional.of(request));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> friendshipService.cancelFriendRequestById(requestId));
        assertEquals("Only the sender can cancel the friend request", exception.getMessage());
    }

    @Test
    void respondFriendRequest_Accept_Success() {
        // Arrange
        FriendRequestResponseDto responseDto = new FriendRequestResponseDto(FRIEND_ID, USER_ID, "ACCEPTED");

        FriendRequest request = new FriendRequest();
        request.setSenderId(FRIEND_ID);
        request.setReceiverId(USER_ID);

        FriendRequestDto expectedDto = new FriendRequestDto();
        expectedDto.setSenderId(FRIEND_ID);
        expectedDto.setReceiverId(USER_ID);
        expectedDto.setStatus(FriendRequestStatus.ACCEPTED);

        when(friendRequestRepo.findBySenderIdAndReceiverId(USER_ID, FRIEND_ID)).thenReturn(request);
        when(friendRequestRepo.save(any(FriendRequest.class))).thenReturn(request);
        when(requestMapper.fromFriendRequest(any(FriendRequest.class))).thenReturn(expectedDto);

        // Act
        FriendRequestDto result = friendshipService.respondFriendRequest(responseDto);

        // Assert
        assertNotNull(result);
        assertEquals(FriendRequestStatus.ACCEPTED, result.getStatus());
        verify(friendshipRepo).saveAll(anyList());
    }

    @Test
    void respondFriendRequest_Reject_Success() {
        // Arrange
        FriendRequestResponseDto responseDto = new FriendRequestResponseDto(FRIEND_ID, USER_ID, "REJECTED");

        FriendRequest request = new FriendRequest();
        request.setSenderId(FRIEND_ID);
        request.setReceiverId(USER_ID);

        FriendRequestDto expectedDto = new FriendRequestDto();
        expectedDto.setSenderId(FRIEND_ID);
        expectedDto.setReceiverId(USER_ID);

        when(friendRequestRepo.findBySenderIdAndReceiverId(USER_ID, FRIEND_ID)).thenReturn(request);
        when(requestMapper.fromFriendRequest(any(FriendRequest.class))).thenReturn(expectedDto);

        // Act
        FriendRequestDto result = friendshipService.respondFriendRequest(responseDto);

        // Assert
        assertNotNull(result);
        verify(friendRequestRepo).delete(request);
    }

    @Test
    void respondFriendRequest_NotFound() {
        // Arrange
        FriendRequestResponseDto responseDto = new FriendRequestResponseDto(FRIEND_ID, USER_ID, "ACCEPTED");
        when(friendRequestRepo.findBySenderIdAndReceiverId(USER_ID, FRIEND_ID)).thenReturn(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> friendshipService.respondFriendRequest(responseDto));
        assertEquals("Friend request not found.", exception.getMessage());
    }

    @Test
    void removeFriend_Success() {
        // Arrange
        FriendshipRemoveDto removeDto = new FriendshipRemoveDto(USER_ID, FRIEND_ID);
        when(friendshipRepo.existsByUserIdAndFriendId(USER_ID, FRIEND_ID)).thenReturn(true);

        // Act
        FriendDto result = friendshipService.removeFriend(removeDto);

        // Assert
        assertNotNull(result);
        assertEquals(FRIEND_ID, result.getUserId());
        verify(friendshipRepo).deleteByUserIdAndFriendId(USER_ID, FRIEND_ID);
        verify(friendshipRepo).deleteByUserIdAndFriendId(FRIEND_ID, USER_ID);
        verify(friendRequestRepo).deleteBySenderIdAndReceiverId(USER_ID, FRIEND_ID);
        verify(friendRequestRepo).deleteBySenderIdAndReceiverId(FRIEND_ID, USER_ID);
    }

    @Test
    void removeFriend_NotFound() {
        // Arrange
        FriendshipRemoveDto removeDto = new FriendshipRemoveDto(USER_ID, FRIEND_ID);
        when(friendshipRepo.existsByUserIdAndFriendId(USER_ID, FRIEND_ID)).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> friendshipService.removeFriend(removeDto));
        assertEquals("Friend not found.", exception.getMessage());
    }

    @Test
    void getFriends_Success() {
        // Arrange
        List<FriendDto> expectedFriends = Collections.singletonList(
            FriendDto.builder().userId(FRIEND_ID).firstName("John").lastName("Doe").build()
        );
        when(friendshipRepo.findFriendsByUserId(USER_ID)).thenReturn(expectedFriends);

        // Act
        List<FriendDto> result = friendshipService.getFriends(USER_ID);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(FRIEND_ID, result.getFirst().getUserId());
    }

    @Test
    void searchMyFriends_Success() {
        // Arrange
        String query = "john";
        List<FriendBriefDto> expectedFriends = Collections.singletonList(
            FriendBriefDto.builder()
                .userId(FRIEND_ID)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .build()
        );
        when(friendshipRepo.searchFriendsByUsernameOrEmail(USER_ID, query.trim())).thenReturn(expectedFriends);

        // Act
        List<FriendBriefDto> result = friendshipService.searchMyFriends(USER_ID, query);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(FRIEND_ID, result.getFirst().getUserId());
        assertEquals("John", result.getFirst().getFirstName());
    }

    @Test
    void getPendingFriendRequests_Success() throws ResourceNotFoundException {
        // Arrange
        FriendRequest request = new FriendRequest();
        request.setRequestId(1L);
        request.setSenderId(FRIEND_ID);
        request.setReceiverId(USER_ID);
        request.setStatus(FriendRequestStatus.PENDING);

        FriendRequestDto dto = new FriendRequestDto();
        dto.setRequestId(1L);
        dto.setSenderId(FRIEND_ID);
        dto.setReceiverId(USER_ID);
        dto.setStatus(FriendRequestStatus.PENDING);

        NormalUserBriefDto senderDetails = new NormalUserBriefDto();
        senderDetails.setUserId(FRIEND_ID);
        senderDetails.setFirstName("John");

        when(friendRequestRepo.findByReceiverIdAndStatus(USER_ID, FriendRequestStatus.PENDING))
            .thenReturn(Collections.singletonList(request));
        when(requestMapper.fromFriendRequest(request)).thenReturn(dto);
        when(normalUserService.getUserBrief(FRIEND_ID)).thenReturn(senderDetails);

        // Act
        List<FriendRequestDto> result = friendshipService.getPendingFriendRequests();

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(FRIEND_ID, result.getFirst().getSenderId());
        assertEquals(USER_ID, result.getFirst().getReceiverId());
        assertEquals(senderDetails, result.getFirst().getSender());
    }

    @Test
    void getPendingFriendRequests_UserNotAuthenticated() {
        // Arrange
        when(authentication.getName()).thenReturn(null);

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> friendshipService.getPendingFriendRequests());
        assertEquals("User not authenticated", exception.getMessage());
    }

    @Test
    void getFriendSummary_Success() throws ResourceNotFoundException {
        // Arrange
        Long friendshipId = 1L;
        FriendSummaryDto expectedSummary = FriendSummaryDto.builder()
            .friendName("John Doe")
            .balance(BigDecimal.TEN)
            .totalLent(BigDecimal.valueOf(100))
            .totalBorrowed(BigDecimal.valueOf(90))
            .friendsSince(OffsetDateTime.now())
            .avatar("profile.jpg")
            .friendID(FRIEND_ID)
            .build();

        when(friendshipRepo.getFriendSummaryByFriendshipId(friendshipId)).thenReturn(expectedSummary);

        // Act
        FriendSummaryDto result = friendshipService.getFriendSummary(friendshipId);

        // Assert
        assertNotNull(result);
        assertEquals(expectedSummary.getFriendName(), result.getFriendName());
        assertEquals(expectedSummary.getBalance(), result.getBalance());
        assertEquals(expectedSummary.getFriendID(), result.getFriendID());
    }

    @Test
    void getFriendSummary_NotFound() {
        // Arrange
        Long friendshipId = 1L;
        when(friendshipRepo.getFriendSummaryByFriendshipId(friendshipId)).thenReturn(null);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
            () -> friendshipService.getFriendSummary(friendshipId));
        assertEquals("Friend summary information not found.", exception.getMessage());
    }

    @Test
    void getAllFriendshipDetails_WithoutFriendName_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        FriendWithTransactionSummaryDto dto = FriendWithTransactionSummaryDto.builder()
            .friendName("John Doe")
            .avatar("profile.jpg")
            .friendshipId(FRIEND_ID)
            .lastTransactionName("Lunch")
            .build();

        Page<FriendWithTransactionSummaryDto> expectedPage = new PageImpl<>(
            Collections.singletonList(dto), pageable, 1
        );

        when(friendshipRepo.findAllFriendshipDetailsByUserId(USER_ID, pageable)).thenReturn(expectedPage);

        // Act
        Page<FriendWithTransactionSummaryDto> result = friendshipService.getAllFriendshipDetails(USER_ID, null, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("John Doe", result.getContent().getFirst().getFriendName());
    }

    @Test
    void getAllFriendshipDetails_WithFriendName_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        String friendName = "John";
        FriendWithTransactionSummaryDto dto = FriendWithTransactionSummaryDto.builder()
            .friendName("John Doe")
            .avatar("profile.jpg")
            .friendshipId(FRIEND_ID)
            .lastTransactionName("Lunch")
            .build();

        Page<FriendWithTransactionSummaryDto> expectedPage = new PageImpl<>(
            Collections.singletonList(dto), pageable, 1
        );

        when(friendshipRepo.findAllFriendshipDetailsByUserIdAndFriendName(USER_ID, friendName.trim(), pageable))
            .thenReturn(expectedPage);

        // Act
        Page<FriendWithTransactionSummaryDto> result = friendshipService.getAllFriendshipDetails(USER_ID, friendName, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("John Doe", result.getContent().getFirst().getFriendName());
    }

    @Test
    void getAllFriendshipDetails_EmptyUserId() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> friendshipService.getAllFriendshipDetails("", null, pageable));
        assertEquals("User ID cannot be empty", exception.getMessage());
    }
}
