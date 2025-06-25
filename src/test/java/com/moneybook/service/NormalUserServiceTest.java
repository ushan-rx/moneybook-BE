package com.moneybook.service;

import com.moneybook.dto.user.NormalUserBriefDto;
import com.moneybook.dto.user.NormalUserCreateDto;
import com.moneybook.dto.user.NormalUserDto;
import com.moneybook.dto.user.NormalUserUpdateDto;
import com.moneybook.exception.ResourceNotFoundException;
import com.moneybook.mappers.NormalUserMapper;
import com.moneybook.model.NormalUser;
import com.moneybook.repository.NormalUserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NormalUserServiceTest {

    @Mock
    private NormalUserRepo repo;

    @Mock
    private NormalUserMapper mapper;

    @InjectMocks
    private NormalUserService normalUserService;

    @Test
    void saveNormalUser_Success() {
        // Arrange
        NormalUserCreateDto createDto = new NormalUserCreateDto();
        NormalUser user = new NormalUser();
        NormalUser savedUser = new NormalUser();
        NormalUserDto userDto = new NormalUserDto();

        when(mapper.FromCreateUsertoNormalUser(any(NormalUserCreateDto.class))).thenReturn(user);
        when(repo.saveAndFlush(any(NormalUser.class))).thenReturn(savedUser);
        when(mapper.fromNormalUser(any(NormalUser.class))).thenReturn(userDto);

        // Act
        NormalUserDto result = normalUserService.saveNormalUser(createDto);

        // Assert
        assertNotNull(result);
        assertEquals(userDto, result);
        verify(repo).saveAndFlush(user);
    }

    @Test
    void getUserDetails_Success() throws ResourceNotFoundException {
        // Arrange
        String userId = "testId";
        NormalUser user = new NormalUser();
        NormalUserDto userDto = new NormalUserDto();

        when(repo.findById(userId)).thenReturn(Optional.of(user));
        when(mapper.fromNormalUser(user)).thenReturn(userDto);

        // Act
        NormalUserDto result = normalUserService.getUserDetails(userId);

        // Assert
        assertNotNull(result);
        assertEquals(userDto, result);
        verify(repo).findById(userId);
    }

    @Test
    void getUserDetails_NotFound() {
        // Arrange
        String userId = "testId";
        when(repo.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> normalUserService.getUserDetails(userId));
        verify(repo).findById(userId);
    }

    @Test
    void updateNormalUser_Success() throws ResourceNotFoundException {
        // Arrange
        String userId = "testId";
        NormalUserUpdateDto updateDto = NormalUserUpdateDto.builder()
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("1234567890")
                .address("123 Main St")
                .bio("A bio")
                .profilePicture("https://example.com/pic.jpg")
                .status(true)
                .build();
        NormalUser existingUser = new NormalUser();
        NormalUser updatedUser = new NormalUser();
        NormalUserDto userDto = new NormalUserDto();

        when(repo.findById(userId)).thenReturn(Optional.of(existingUser));
        when(repo.save(existingUser)).thenReturn(updatedUser);
        when(mapper.fromNormalUser(updatedUser)).thenReturn(userDto);

        // Act
        NormalUserDto result = normalUserService.updateNormalUser(userId, updateDto);

        // Assert
        assertNotNull(result);
        assertEquals(userDto, result);
        verify(repo).findById(userId);
        verify(mapper).FromUpdateUserToNormalUser(updateDto, existingUser);
        verify(repo).save(existingUser);
        verify(mapper).fromNormalUser(updatedUser);
    }

    @Test
    void updateNormalUser_NotFound() {
        // Arrange
        String userId = "testId";
        NormalUserUpdateDto updateDto = new NormalUserUpdateDto();
        when(repo.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> normalUserService.updateNormalUser(userId, updateDto));
        verify(repo).findById(userId);
    }

    @Test
    void isNewUser_True_WhenUserNotFound() {
        // Arrange
        String subId = "testId";
        when(repo.findById(subId)).thenReturn(Optional.empty());

        // Act
        boolean result = normalUserService.isNewUser(subId);

        // Assert
        assertTrue(result);
    }

    @Test
    void isNewUser_True_WhenPhoneNumberNull() {
        // Arrange
        String subId = "testId";
        NormalUser user = new NormalUser();
        user.setPhoneNumber(null);
        when(repo.findById(subId)).thenReturn(Optional.of(user));

        // Act
        boolean result = normalUserService.isNewUser(subId);

        // Assert
        assertTrue(result);
    }

    @Test
    void isNewUser_True_WhenAddressNull() {
        // Arrange
        String subId = "testId";
        NormalUser user = new NormalUser();
        user.setPhoneNumber("1234567890");
        user.setAddress(null);
        when(repo.findById(subId)).thenReturn(Optional.of(user));

        // Act
        boolean result = normalUserService.isNewUser(subId);

        // Assert
        assertTrue(result);
    }

    @Test
    void isNewUser_False_WhenUserExistsAndComplete() {
        // Arrange
        String subId = "testId";
        NormalUser user = new NormalUser();
        user.setPhoneNumber("1234567890");
        user.setAddress("Test Address");
        when(repo.findById(subId)).thenReturn(Optional.of(user));

        // Act
        boolean result = normalUserService.isNewUser(subId);

        // Assert
        assertFalse(result);
    }

    @Test
    void getUserBrief_Success() throws ResourceNotFoundException {
        // Arrange
        String userId = "testId";
        NormalUser user = new NormalUser();
        NormalUserBriefDto briefDto = new NormalUserBriefDto();

        when(repo.findById(userId)).thenReturn(Optional.of(user));
        when(mapper.fromNormalUserToBrief(user)).thenReturn(briefDto);

        // Act
        NormalUserBriefDto result = normalUserService.getUserBrief(userId);

        // Assert
        assertNotNull(result);
        assertEquals(briefDto, result);
        verify(repo).findById(userId);
    }

    @Test
    void getUserBrief_NotFound() {
        // Arrange
        String userId = "testId";
        when(repo.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> normalUserService.getUserBrief(userId));
        verify(repo).findById(userId);
    }

    @Test
    void searchUsersByName_Success() {
        // Arrange
        String searchTerm = "test";
        String currentUserId = "currentUserId";
        NormalUser user = new NormalUser();
        NormalUserBriefDto briefDto = new NormalUserBriefDto();
        List<NormalUser> users = Collections.singletonList(user);

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(currentUserId);
        SecurityContextHolder.setContext(securityContext);

        when(repo.findTop10ByNameContainingExceptUser(searchTerm.trim(), currentUserId)).thenReturn(users);
        when(mapper.fromNormalUserToBrief(user)).thenReturn(briefDto);

        // Act
        List<NormalUserBriefDto> result = normalUserService.searchUsersByName(searchTerm);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(briefDto, result.getFirst());
    }

    @Test
    void searchUsersByName_EmptySearchTerm() {
        // Arrange
        String searchTerm = " ";

        // Act
        List<NormalUserBriefDto> result = normalUserService.searchUsersByName(searchTerm);

        // Assert
        assertTrue(result.isEmpty());
    }
}
