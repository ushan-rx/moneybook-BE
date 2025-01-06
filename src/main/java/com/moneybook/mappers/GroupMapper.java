package com.moneybook.mappers;

import com.moneybook.dto.group.GroupCreateDto;
import com.moneybook.dto.group.GroupDto;
import com.moneybook.model.FriendGroup;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface GroupMapper {
    GroupMapper MAPPER = Mappers.getMapper(GroupMapper.class);

    @Mapping(target = "ownerUsername", ignore = true)
    @Mapping(target = "created_by", ignore = true )
    @Mapping(target = "group_id", ignore = true)
    @Mapping(target = "created_at", ignore = true)
    FriendGroup toGroup(GroupCreateDto groupCreateDto);

    @InheritConfiguration
    GroupDto fromGroup(FriendGroup group);
}
