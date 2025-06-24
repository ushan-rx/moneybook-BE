package com.moneybook.repository;

import com.moneybook.model.FriendBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendBalanceRepo extends JpaRepository<FriendBalance, Integer> {
    List<FriendBalance> findByUser1Id(String user1Id);
}
