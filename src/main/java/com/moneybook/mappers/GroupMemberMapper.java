package com.moneybook.mappers;

import com.moneybook.dto.groupMembership.GroupMemberCreateDto;
import com.moneybook.dto.groupMembership.GroupMembershipDto;
import com.moneybook.model.GroupMember;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface GroupMemberMapper {
    GroupMemberMapper MAPPER = Mappers.getMapper(GroupMemberMapper.class);
    @Mapping(target = "membership_id", ignore = true)
    @Mapping(target = "joined_at", ignore = true)
    GroupMember ToGroupMember(GroupMemberCreateDto groupMemberCreateDto);

    @InheritConfiguration
    GroupMembershipDto fromGroupMember(GroupMember groupMember);
}
