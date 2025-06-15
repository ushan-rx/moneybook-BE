package com.moneybook.mappers;

import com.moneybook.dto.user.NormalUserBriefDto;
import com.moneybook.dto.user.NormalUserCreateDto;
import com.moneybook.dto.user.NormalUserDto;
import com.moneybook.dto.user.NormalUserUpdateDto;
import com.moneybook.model.NormalUser;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface NormalUserMapper {

    @Mapping(target = "bio", ignore = true)
    @Mapping(target = "phoneNumber", ignore = true)
    @Mapping(target = "lastName", ignore = true)
    @Mapping(target = "firstName", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "groups", ignore = true)
    NormalUser FromCreateUsertoNormalUser(NormalUserCreateDto dto);

    @InheritConfiguration
    @Mapping(target = "bio", source = "bio")
    NormalUserDto fromNormalUser(NormalUser normalUser);

    @InheritConfiguration
    @Mapping(target = "userId", source = "userId")
    NormalUserBriefDto fromNormalUserToBrief(NormalUser user);

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "groups", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void  FromUpdateUserToNormalUser(NormalUserUpdateDto dto, @MappingTarget NormalUser existingUser);
}
