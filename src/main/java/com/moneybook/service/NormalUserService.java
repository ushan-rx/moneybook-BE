package com.moneybook.service;

import com.moneybook.dto.user.NormalUserCreateDto;
import com.moneybook.dto.user.NormalUserDto;
import com.moneybook.mappers.NormalUserMapper;
import com.moneybook.model.NormalUser;
import com.moneybook.repository.NormalUserRepo;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class NormalUserService {

    private NormalUserRepo repo;

    @Transactional
    public NormalUserDto saveNormalUser(NormalUserCreateDto userDto) {
        NormalUser user = NormalUserMapper.MAPPER.toNormalUser(userDto);
        user.setUserId("2ewe8233dv833f3r83qc3f381"); //temporary
        NormalUser userCreated = repo.saveAndFlush(user);
        return NormalUserMapper.MAPPER.fromNormalUser(userCreated);
    }

    //add check username service
}
