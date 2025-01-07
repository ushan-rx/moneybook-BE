package com.moneybook.service;

import com.moneybook.dto.groupMembership.GroupMemberCreateDto;
import com.moneybook.dto.groupMembership.GroupMembershipDto;
import com.moneybook.mappers.GroupMemberMapper;
import com.moneybook.model.GroupMember;
import com.moneybook.repository.GroupMembershipRepo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GroupMembershipService {

    private GroupMembershipRepo repo;

    public GroupMembershipDto addGroupMember(GroupMemberCreateDto groupMemberCreateDto) {
        GroupMember membership = GroupMemberMapper.MAPPER.ToGroupMember(groupMemberCreateDto);
        // Add the group member to the group
        GroupMember membershipCreated = repo.saveAndFlush(membership);
        return GroupMemberMapper.MAPPER.fromGroupMember(membershipCreated);
    }
}
