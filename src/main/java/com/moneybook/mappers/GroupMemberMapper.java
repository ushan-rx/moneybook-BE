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

    @Mapping(target = "membershipId", ignore = true)
    @Mapping(target = "joinedAt", ignore = true)
    GroupMember toGroupMember(GroupMemberCreateDto groupMemberCreateDto);

    @InheritConfiguration
    GroupMembershipDto fromGroupMember(GroupMember groupMember);
}
