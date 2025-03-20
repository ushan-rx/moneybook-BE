package com.moneybook.service.group;

import com.moneybook.dto.groupMembership.GroupMemberCreateDto;
import com.moneybook.dto.groupMembership.GroupMembershipDto;
import com.moneybook.mappers.GroupMemberMapper;
import com.moneybook.model.GroupMember;
import com.moneybook.repository.GroupMembershipRepo;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GroupMembershipService {

    private GroupMembershipRepo repo;

    @Transactional
    public GroupMembershipDto addGroupMember(GroupMemberCreateDto groupMemberCreateDto) {
        GroupMember membership = GroupMemberMapper.MAPPER.toGroupMember(groupMemberCreateDto);
        // Add the group member to the group
        GroupMember membershipCreated = repo.saveAndFlush(membership);
        return GroupMemberMapper.MAPPER.fromGroupMember(membershipCreated);
    }
}
