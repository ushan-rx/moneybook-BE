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
import org.mapstruct.factory.Mappers;

@Mapper
public interface NormalUserMapper {

    NormalUserMapper MAPPER = Mappers.getMapper(NormalUserMapper.class);

    @Mapping(target = "phoneNumber", ignore = true)
    @Mapping(target = "lastName", ignore = true)
    @Mapping(target = "firstName", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "groups", ignore = true)
    NormalUser FromCreateUsertoNormalUser(NormalUserCreateDto dto);

    @InheritConfiguration
    NormalUserDto fromNormalUser(NormalUser normalUser);

    @InheritConfiguration
    NormalUserBriefDto fromNormalUserToBrief(NormalUser user);

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "profilePicture", ignore = true)
    @Mapping(target = "groups", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void  FromUpdateUserToNormalUser(NormalUserUpdateDto dto, @MappingTarget NormalUser existingUser);
}
