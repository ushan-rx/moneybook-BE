package com.moneybook.mappers;

import com.moneybook.dto.groupMembership.GroupMemberCreateDto;
import com.moneybook.dto.groupMembership.GroupMembershipDto;
import com.moneybook.model.GroupMember;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GroupMemberMapper {

    @Mapping(target = "membershipId", ignore = true)
    @Mapping(target = "joinedAt", ignore = true)
    GroupMember toGroupMember(GroupMemberCreateDto groupMemberCreateDto);

    @InheritConfiguration
    GroupMembershipDto fromGroupMember(GroupMember groupMember);
}
