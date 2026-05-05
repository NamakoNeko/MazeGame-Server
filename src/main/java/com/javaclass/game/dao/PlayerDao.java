package com.javaclass.game.dao;

import com.javaclass.game.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerDao extends JpaRepository<Player, Long> {
    Player findByAccountId(String accountId);
    
}