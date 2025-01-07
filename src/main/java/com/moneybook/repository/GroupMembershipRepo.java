package com.moneybook.repository;

import com.moneybook.model.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupMembershipRepo extends JpaRepository<GroupMember, Long> {
}
