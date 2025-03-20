package com.moneybook.service;

import com.moneybook.dto.user.NormalUserCreateDto;
import com.moneybook.dto.user.NormalUserDto;
import com.moneybook.mappers.NormalUserMapper;
import com.moneybook.model.NormalUser;
import com.moneybook.repository.NormalUserRepo;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class NormalUserService {

    private NormalUserRepo repo;

    @Transactional
    public NormalUserDto saveNormalUser(@Valid NormalUserCreateDto userDto) {
        NormalUser user = NormalUserMapper.MAPPER.FromCreateUsertoNormalUser(userDto);
        NormalUser userCreated = repo.saveAndFlush(user);
        return NormalUserMapper.MAPPER.fromNormalUser(userCreated);
    }

    public boolean isNewUser(String subId) {
        return !repo.existsById(subId);
    }

    //add check username service
}
