package com.moneybook.repository;

import com.moneybook.model.FriendGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepo extends JpaRepository<FriendGroup, String> {

}
