package com.moneybook.service;

import com.moneybook.dto.NormalUserBriefDto;
import com.moneybook.dto.NormalUserDto;
import com.moneybook.entity.NormalUser;
import com.moneybook.mappers.NormalUserMapper;
import com.moneybook.repository.NormalUserRepo;
import com.moneybook.validators.ObjectValidator;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@AllArgsConstructor
@Service
public class NormalUserService {

    private NormalUserRepo repo;
    private ObjectValidator<NormalUserDto> normalUserValidator;
    private ObjectValidator<NormalUserBriefDto> normalUserBriefValidator;

    @Transactional
    public String saveNormalUser(NormalUserDto userDto) {

        Set<String> violations = normalUserValidator.validate(userDto);
        if (!violations.isEmpty()) {
            return String.join("\n", violations);
        }
        NormalUser user = NormalUserMapper.MAPPER.toNormalUser(userDto);
        user.setUser_id("2ewe833dv833f3r83c3f38");
        repo.save(user);
        return "User successfully saved";
    }

}
