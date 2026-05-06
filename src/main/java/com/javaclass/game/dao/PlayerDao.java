package com.javaclass.game.dao;

import com.javaclass.game.model.Player;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayerDao extends JpaRepository<Player, Long> {

    Optional<Player> findByAccountId(String accountId);

    @Query("SELECT player FROM Player player WHERE " +
           "(:keyword IS NULL OR player.accountId LIKE %:keyword% OR player.nickname LIKE %:keyword%)")
    Page<Player> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
}