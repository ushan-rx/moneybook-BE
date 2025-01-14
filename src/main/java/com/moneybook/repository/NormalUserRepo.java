package com.moneybook.repository;

import com.moneybook.model.NormalUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NormalUserRepo extends JpaRepository<NormalUser, String> {

}
