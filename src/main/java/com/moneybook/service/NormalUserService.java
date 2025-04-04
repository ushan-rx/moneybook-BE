package com.moneybook.service;

import com.moneybook.dto.user.NormalUserBriefDto;
import com.moneybook.dto.user.NormalUserCreateDto;
import com.moneybook.dto.user.NormalUserDto;
import com.moneybook.dto.user.NormalUserUpdateDto;
import com.moneybook.exception.ResourceNotFoundException;
import com.moneybook.mappers.NormalUserMapper;
import com.moneybook.model.NormalUser;
import com.moneybook.repository.NormalUserRepo;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class NormalUserService {

    private final NormalUserRepo repo;
    private final NormalUserMapper mapper;

    @Transactional
    public NormalUserDto saveNormalUser(@Valid NormalUserCreateDto userDto) {
        NormalUser user = mapper.FromCreateUsertoNormalUser(userDto);
        NormalUser userCreated = repo.saveAndFlush(user);
        return mapper.fromNormalUser(userCreated);
    }

    @Transactional
    public NormalUserDto updateNormalUser(String userID, NormalUserUpdateDto dto) throws ResourceNotFoundException {
        NormalUser existingUser = repo.findById(userID)
                .orElseThrow(()-> new ResourceNotFoundException(("User with id " + userID + " not found")));

        mapper.FromUpdateUserToNormalUser(dto, existingUser);
        NormalUser updatedUser = repo.save(existingUser);
        return mapper.fromNormalUser(updatedUser);
    }

    public boolean isNewUser(String subId) {
        NormalUser user = repo.findById(subId).orElse(null);
        if(user == null) {
            return true;
        } else if (user.getPhoneNumber() == null || user.getPhoneNumber().isEmpty() || user.getPhoneNumber().isBlank()) {
            return true;
        } else return user.getAddress() == null || user.getAddress().isEmpty() || user.getAddress().isBlank();
    }

    public NormalUserBriefDto getUserBrief(String userId) throws ResourceNotFoundException {
        NormalUser user = repo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found"));
        return mapper.fromNormalUserToBrief(user);
    }

    public List<NormalUserBriefDto> searchUsersByName(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return Collections.emptyList();
        }
        String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();
        return repo.findTop10ByNameContainingExceptUser(searchTerm.trim(), currentUserId).stream()
                .map(mapper::fromNormalUserToBrief)
                .collect(Collectors.toList());
    }
}
