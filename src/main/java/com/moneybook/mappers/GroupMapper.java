package com.moneybook.mappers;

import com.moneybook.dto.group.GroupCreateDto;
import com.moneybook.dto.group.GroupDto;
import com.moneybook.model.FriendGroup;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GroupMapper {

    @Mapping(target = "ownerName", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "groupId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    FriendGroup toGroup(GroupCreateDto groupCreateDto);

    @InheritConfiguration
    GroupDto fromGroup(FriendGroup group);
}
