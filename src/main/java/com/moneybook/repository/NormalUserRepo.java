package com.moneybook.repository;

import com.moneybook.model.NormalUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NormalUserRepo extends JpaRepository<NormalUser, String> {
    @Query("SELECT u FROM NormalUser u WHERE LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<NormalUser> findTop10ByNameContaining(@Param("searchTerm") String searchTerm);

    @Query("SELECT u FROM NormalUser u WHERE LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :searchTerm, '%')) AND u.userId != :userId")
    List<NormalUser> findTop10ByNameContainingExceptUser(@Param("searchTerm") String searchTerm, @Param("userId") String userId);
}
