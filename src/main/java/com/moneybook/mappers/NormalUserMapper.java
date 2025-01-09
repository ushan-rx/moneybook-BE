package com.moneybook.mappers;

import com.moneybook.dto.user.NormalUserBriefDto;
import com.moneybook.dto.user.NormalUserCreateDto;
import com.moneybook.dto.user.NormalUserDto;
import com.moneybook.model.NormalUser;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface NormalUserMapper {

    NormalUserMapper MAPPER = Mappers.getMapper(NormalUserMapper.class);
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "groups", ignore = true)
    NormalUser toNormalUser(NormalUserCreateDto dto);

    @InheritConfiguration
    NormalUserDto fromNormalUser(NormalUser normalUser);

    @InheritConfiguration
    NormalUserBriefDto fromNormalUserToBrief(NormalUser user);
}
